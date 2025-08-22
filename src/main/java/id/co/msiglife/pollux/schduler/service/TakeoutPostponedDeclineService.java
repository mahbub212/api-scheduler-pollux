package id.co.msiglife.pollux.schduler.service;

import id.co.msiglife.pollux.schduler.entity.BcaData;
import id.co.msiglife.pollux.schduler.entity.MasterWFDocument;
import id.co.msiglife.pollux.schduler.entity.MasterWFDocumentHistory;
import id.co.msiglife.pollux.schduler.repository.MappingBcaRepository;
import id.co.msiglife.pollux.schduler.repository.MasterWFDocumentHistoryRepository;
import id.co.msiglife.pollux.schduler.repository.MasterWFDocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TakeoutPostponedDeclineService {

    @Autowired
    MappingBcaRepository mappingBcaRepository;

    @Autowired
    MasterWFDocumentRepository masterWFDocumentRepository;

    @Autowired
    MasterWFDocumentHistoryRepository masterWFDocumentHistoryRepository;

    @Scheduled(cron = "0 0 2 * * ?")
    public void takeoutPostponedDecline() {
        try {
            System.out.println("=================== START EXECUTING Takeout Postpone Decline Order BCA ===================");
            List<BcaData> dataTakeoutCancel = mappingBcaRepository.getDataTakeoutCancel();
            List<BcaData> dataTakeoutOrder = mappingBcaRepository.getDataTakeoutOrder();

            List<String> regSpajCancelList = dataTakeoutCancel.stream()
                    .map(BcaData::getRegSpaj)
                    .collect(Collectors.toList());

            List<String> regSpajOrderList = dataTakeoutOrder.stream()
                            .map(BcaData::getRegSpaj)
                            .collect(Collectors.toList());

            List<MasterWFDocument> dataPostponeDecline = masterWFDocumentRepository.getDataCancel();
            if (!dataPostponeDecline.isEmpty()){
                for (int i = 0 ; i < dataPostponeDecline.size() ; i++){
                    Integer lswfStateIdTo = null;

                    if (dataPostponeDecline.get(i).getLswfStatusId().equals(6)) {
                        lswfStateIdTo = -6;
                    } else if (dataPostponeDecline.get(i).getLswfStatusId().equals(7)){
                        lswfStateIdTo = -5;
                    }

                    this.insertMasterWFDocumentHistory(
                            dataPostponeDecline.get(i).getDocId(),
                            78,
                            dataPostponeDecline.get(i).getParentDocId(),
                            dataPostponeDecline.get(i).getLswfStateId(),
                            "Cancellation request from the API",
                            0,
                            lswfStateIdTo,
                            new Date(),
                            null,
                            null,
                            null,
                            dataPostponeDecline.get(i).getDocNumber(),
                            dataPostponeDecline.get(i).getLswfStatusId()
                    );

                    MasterWFDocument newMasterWFDocument = null;
                    newMasterWFDocument = dataPostponeDecline.get(i);
                    newMasterWFDocument.setLswfStateId(lswfStateIdTo);
                    newMasterWFDocument.setDocCreated(new Date());
                    newMasterWFDocument.setLusId(0);
                    masterWFDocumentRepository.saveAndFlush(newMasterWFDocument);
                }
            }

            mappingBcaRepository.updateBcaDataOrderStatusAndUserByRegSpajAndTipeTransAndOrderStatus(
                    regSpajCancelList,
                    0,
                    3,
                    1,
                    0
            );

            mappingBcaRepository.updateBcaDataOrderStatusAndUserByRegSpajAndTipeTransAndOrderStatus(
                    regSpajOrderList,
                    1,
                    1,
                    2,
                    0
            );
            System.out.println("=================== FINISHING EXECUTING Takeout Postpone Decline Order BCA ===================");
        } catch (RuntimeException e) {
            String message = !e.getMessage().isEmpty() ? e.getMessage() : "Error takeout Postponed / Decline in order bca";
            System.out.println("Error log: " + message);
            e.printStackTrace();
        } finally {
            System.gc();
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
}