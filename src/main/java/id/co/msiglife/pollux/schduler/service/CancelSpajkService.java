package id.co.msiglife.pollux.schduler.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.co.msiglife.pollux.schduler.dto.ApiResponseDTO;
import id.co.msiglife.pollux.schduler.dto.CancelPolicyDTO;
import id.co.msiglife.pollux.schduler.entity.*;
import id.co.msiglife.pollux.schduler.repository.*;
import id.co.msiglife.pollux.schduler.util.ConvertUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.*;

import static id.co.msiglife.pollux.schduler.util.StaticValue.*;

@Service
public class CancelSpajkService {

    @Autowired
    MasterPolicyRepository masterPolicyRepository;

    @Autowired
    MasterInsuredRepository masterInsuredRepository;

    @Autowired
    MasterWFDocumentRepository masterWFDocumentRepository;

    @Autowired
    MappingBcaRepository mappingBcaRepository;

    @Autowired
    ListWFConfigRepository listWFConfigRepository;

    @Autowired
    MasterWFDocumentHistoryRepository masterWFDocumentHistoryRepository;

    @Autowired
    MasterUnderwritingRepository masterUnderwritingRepository;

    @Autowired
    ListWFStatusRepository listWFStatusRepository;

    @Autowired
    MasterPositionSPAJRepository masterPositionSPAJRepository;

    @Autowired
    MasterWfRequestRepository masterWFRequestRepository;

    @Autowired
    MasterWfLogActivityPolluxRepository masterWfLogActivityPolluxRepository;

    @Scheduled(cron = "0 */3 * * * ?")
    public void cancelSpajkScheduler(){

        Map<String, Object> result = new HashMap<>();
        MasterWfLogActivityPollux wfLog = new MasterWfLogActivityPollux();
        wfLog.setIpAddress("127.0.0.1");
        wfLog.setEndpointUrl("/cancel-data-peserta");
        wfLog.setMethod("PUT");
        wfLog.setUserId(0);

        try {
            //get list all spajk to be cancelled
            MasterPositionSPAJ masterPositionSPAJ = new MasterPositionSPAJ();

            List<Object[]> resultList = masterPolicyRepository.findPolicyTobeCancelled();
            List<CancelPolicyDTO> policyToBeCancelledList = new ArrayList<>();
            for (Object[] row : resultList) {
                CancelPolicyDTO dto = new CancelPolicyDTO();
                dto.setRegSpaj(ConvertUtil.safeString(row[0]));
                policyToBeCancelledList.add(dto);
            }
            System.out.println("Jumlah Policy yang akan dicancel = " + policyToBeCancelledList.size());

            for (CancelPolicyDTO cancelDto : policyToBeCancelledList) {
                MasterPolicy existingPolicy = masterPolicyRepository.findByRegSpaj(cancelDto.getRegSpaj());
                MasterInsured existingInsured = masterInsuredRepository.findByRegSpajAndInsuredNo(cancelDto.getRegSpaj(), 1);
                MasterWFDocument masterWFDocument = masterWFDocumentRepository.findMasterWFDocumentByDocNumberAndLswfTypeId(cancelDto.getRegSpaj(), 78);

                // check status cancel apakah sudah pernah diproses
                Optional<BcaData> bcaDataOrder = mappingBcaRepository.findByRegSpajAndTipeTrans(cancelDto.getRegSpaj(), 3);

                if (masterWFDocument == null) {
//                return new ApiResponseDTO<>(400, "SPAJ " + existingPolicy.getRegSpaj() + " Document not found.", result);
                    throw new RuntimeException("SPAJ " + cancelDto.getRegSpaj() + " Document not found.");
                }else {
                    ListWFConfig wfConfig = listWFConfigRepository.findListWFConfigByTypeIdAndStateIdFromAndStateIdTo(masterWFDocument.getLswfTypeId(), masterWFDocument.getLswfStateId(), -4);
                    Optional<MasterWFDocumentHistory> masterWFDocumentHistoryList = masterWFDocumentHistoryRepository.findFirstByDocIdAndLswfTypeIdAndDocNumberOrderByDocHistIdDesc(masterWFDocument.getDocId(), masterWFDocument.getLswfTypeId(), masterWFDocument.getDocNumber());
                    MasterUnderwriting existMasterUnderwriting = masterUnderwritingRepository.findbyRegSpajk(masterWFDocument.getDocNumber());
                    if(existMasterUnderwriting != null) {
                        ListWFStatus listWFStatus = listWFStatusRepository.findListWFStatusByTypeIdAndStatusId(masterWFDocument.getLswfTypeId(), masterWFDocument.getLswfStatusId());
                        if (existMasterUnderwriting.getFinalDecision().toUpperCase().equals("POSTPONE") || listWFStatus.getStatusId() == STATUS_WORKSHEET_POSTPONED || existMasterUnderwriting.getFinalDecision().toUpperCase().equals("DECLINE") || listWFStatus.getStatusId() == STATUS_WORKSHEET_DECLINED || listWFStatus.getStatusId() == STATUS_WORKSHEET_INFORCED || existMasterUnderwriting.getFinalDecision().toUpperCase().equals("CANCEL")) {
                            if (masterWFDocumentHistoryList.isPresent()) {
                                MasterWFDocumentHistory masterWFDocumentHistory = masterWFDocumentHistoryList.get();
                                if (masterWFDocumentHistory.getLswfStateIdTo() != STATUS_WORKSHEET_APPROVED && (listWFStatus.getStatusId() != STATUS_WORKSHEET_POSTPONED && listWFStatus.getStatusId() != STATUS_WORKSHEET_DECLINED)) {
//                                    return new ApiResponseDTO<>(400, "SPAJ is still pending approval. Please complete the approval process before proceeding with the cancellation.", result);
                                    throw new RuntimeException("SPAJ is still pending approval. Please complete the approval process before proceeding with the cancellation.");
                                } else {
                                    if (existMasterUnderwriting.getFinalDecision().toUpperCase().equals("DECLINE") || listWFStatus.getStatusId() == STATUS_WORKSHEET_DECLINED) {
//                                        return new ApiResponseDTO<>(400, "SPAJ with Decline status cannot be canceled.", result);
                                        throw new RuntimeException("SPAJ with Decline status cannot be canceled.");
                                    } else if (listWFStatus.getStatusId() == STATUS_WORKSHEET_INFORCED) {
//                                        return new ApiResponseDTO<>(400, "SPAJ with Inforce status cannot be canceled.", result);
                                        throw new RuntimeException("SPAJ with Inforce status cannot be canceled.");
                                    } else if (existMasterUnderwriting.getFinalDecision().toUpperCase().equals("CANCEL")) {
//                                        return new ApiResponseDTO<>(400, "SPAJ with Cancel status cannot be canceled.", result);
                                        throw new RuntimeException("SPAJ with Cancel status cannot be canceled.");
                                    } else if (existMasterUnderwriting.getFinalDecision().toUpperCase().equals("POSTPONE") || listWFStatus.getStatusId() == STATUS_WORKSHEET_POSTPONED) {
//                                        return new ApiResponseDTO<>(400, "SPAJ with Postpone status cannot be canceled.", result);
                                        throw new RuntimeException("SPAJ with Postpone status cannot be canceled.");
                                    }
                                }
                            }
                        }
                    }

                    //handling spaj bca start
                    if(bcaDataOrder.isPresent()){
                        BcaData bcaRevisionData = bcaDataOrder.get();
                        // update status ORDER di data order api
                        bcaRevisionData.setOrderStatus(1);
                        bcaRevisionData.setOrderProcessDate(new Date());
//                        bcaRevisionData.setOrderUserProcess(auth.getUserId());
                        bcaRevisionData.setOrderUserProcess(0);
                        System.out.println("Update cancel order data BCA Reg SPAJ: " + cancelDto.getRegSpaj());
                        mappingBcaRepository.save(bcaRevisionData);
                        System.out.println("Order data updated successfully...");

//                        ApiResponseDTO<Map<String, Object>> generatePdfBcaResponse = handlingPdfCancelBca(cancelDto.getRegSpaj(), 3, auth, bcaDataOrder, cancelDto, existingInsured);
//                        if (generatePdfBcaResponse.getCode() != 200) {
//                            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
//                            return generatePdfBcaResponse;
//                        }

                    }
                    //handling spaj bca end

                    existingPolicy.setLsspId(12);
                    existingPolicy.setLspdId(999);
                    existingPolicy.setNote("CANCELLED AUTOMATICALLY VIA SCHEDULER; EXCEEDED 61 DAYS");
                    existingInsured.setTglBatal(new Date());

                    masterPositionSPAJ.setRegSpaj(cancelDto.getRegSpaj());
                    masterPositionSPAJ.setPositionDate(existingInsured.getTglBatal());
                    masterPositionSPAJ.setLsspId(existingPolicy.getLsspId());
                    masterPositionSPAJ.setLspdId(existingPolicy.getLspdId());
//                    masterPositionSPAJ.setLusId(existingPolicy.getLusId());
                    masterPositionSPAJ.setLusId(0);
                    masterPositionSPAJ.setDesc(existingPolicy.getNote());
                    masterPositionSPAJ.setLssaId(existingInsured.getLssaId());

                    masterPolicyRepository.save(existingPolicy);
                    masterInsuredRepository.save(existingInsured);
                    masterPositionSPAJRepository.save(masterPositionSPAJ);

                    masterWFDocument.setLswfStateId(wfConfig.getStateIdTo());
                    masterWFDocument.setLswfStatusId(11);
                    this.insertMasterWFDocumentHistory(
                            masterWFDocument.getDocId(),
                            masterWFDocument.getLswfTypeId(),
                            masterWFDocument.getParentDocId(),
                            wfConfig.getStateIdFrom(),
                            wfConfig.getConfigAction(),
                            //auth.getUserId(),
                            0,
                            wfConfig.getStateIdTo(),
                            new Date(),
                            null,
                            null,
                            null,
                            masterWFDocument.getDocNumber(),
                            wfConfig.getStatusId()
                    );

                    MasterWfRequest masterWfRequest = this.insertMasterWfRequest(
                            masterWFDocument.getDocId(),
                            wfConfig.getConfigId(),
                            wfConfig.getConfigAction(),
                            masterWFDocument.getLswfTypeId(),
                            wfConfig.getStateIdFrom(),
                            new ObjectMapper().writeValueAsString(masterWFDocument),
                            new Date()
                    );

                    masterWFDocumentRepository.save(masterWFDocument);

                }
            wfLog.setDocNumber(cancelDto.getRegSpaj());
            wfLog.setSuccess(true);
            wfLog.setResponse(new ObjectMapper().writeValueAsString(result));
            wfLog.setLogDate(new Date());
            masterWfLogActivityPolluxRepository.save(wfLog);
            }
        } catch (Exception e) {
            System.out.println("Error log: ");
            result.put("error", e.getMessage());
            String message = !e.getMessage().isEmpty() ? e.getMessage() : "Error while updating document workflow";
            e.printStackTrace();
        }

    }

    private MasterWFDocumentHistory insertMasterWFDocumentHistory(Long docId, Integer lswfTypeId, Long parentDocId, Integer lswfStateIdFrom, String lswfConfigAction, Integer lusId, Integer lswfStateIdTo, Date docHistCreated, String docStatusKey, Integer docStatusValue, String docHistRemarks, String docNumber, Integer lswfStatusId) {
        MasterWFDocumentHistory masterWFDocumentHistory = new MasterWFDocumentHistory();
        masterWFDocumentHistory.setDocId(docId);
        masterWFDocumentHistory.setLswfTypeId(lswfTypeId);
        masterWFDocumentHistory.setParentDocId(parentDocId);
        masterWFDocumentHistory.setLswfStateIdFrom(lswfStateIdFrom);
        masterWFDocumentHistory.setLswfConfigAction(lswfConfigAction);
        masterWFDocumentHistory.setLusId(lusId);
        masterWFDocumentHistory.setLswfStateIdTo(lswfStateIdTo);
        masterWFDocumentHistory.setDocHistCreated(docHistCreated);
        masterWFDocumentHistory.setDocStatusKey(docStatusKey);
        masterWFDocumentHistory.setDocStatusValue(docStatusValue);
        masterWFDocumentHistory.setDocHistRemarks(docHistRemarks);
        masterWFDocumentHistory.setDocNumber(docNumber);
        masterWFDocumentHistory.setLswfStatusId(lswfStatusId);
        return masterWFDocumentHistoryRepository.save(masterWFDocumentHistory);
    }

    private MasterWfRequest insertMasterWfRequest(Long docId, Integer lswfConfigId, String lswfConfigAction, Integer lswfTypeId, Integer lswfStateIdFrom, String requestDet, Date requestCreatedDate) {
        MasterWfRequest masterWfRequest = new MasterWfRequest();
        masterWfRequest.setParentDocId(docId);
        masterWfRequest.setDocId(docId);
        masterWfRequest.setLswfConfigId(lswfConfigId);
        masterWfRequest.setLswfConfigAction(lswfConfigAction);
        masterWfRequest.setLswfTypeId(lswfTypeId);
        masterWfRequest.setLswfStateIdFrom(lswfStateIdFrom);
        masterWfRequest.setRequestDet(requestDet);
        masterWfRequest.setRequestCreatedDate(requestCreatedDate);
        return masterWFRequestRepository.save(masterWfRequest);
    }
}
