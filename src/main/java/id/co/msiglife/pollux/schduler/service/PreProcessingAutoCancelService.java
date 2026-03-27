package id.co.msiglife.pollux.schduler.service;

import id.co.msiglife.pollux.schduler.dto.PreProcessingKonfirmasiSuratDTO;
import id.co.msiglife.pollux.schduler.entity.MasterAutoCancelEmailLog;
import id.co.msiglife.pollux.schduler.repository.MasterAutoCancelEmailLogRepository;
import id.co.msiglife.pollux.schduler.repository.MasterPolicyRepository;
import id.co.msiglife.pollux.schduler.util.ConvertUtil;
import id.co.msiglife.pollux.schduler.util.StaticValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class PreProcessingAutoCancelService {

    @Autowired
    MasterPolicyRepository masterPolicyRepository;

    @Autowired
    MasterAutoCancelEmailLogRepository masterAutoCancelEmailLogRepository;

    public void saveToLoggerAutoCancel(List<PreProcessingKonfirmasiSuratDTO> preProcessingDTOList) {
        try{
            for (PreProcessingKonfirmasiSuratDTO dto : preProcessingDTOList) {
                MasterAutoCancelEmailLog masterAutoCancelEmailLog = new MasterAutoCancelEmailLog();
                masterAutoCancelEmailLog.setMeId(null);
                masterAutoCancelEmailLog.setRegSpaj(dto.getRegSpaj());
                masterAutoCancelEmailLog.setCreatedAt(new Date());
                masterAutoCancelEmailLog.setPathAttach(null);
                masterAutoCancelEmailLog.setReminderDays(dto.getReminderDays());
                masterAutoCancelEmailLog.setReminderType(dto.getReminderType());

                // skip email and notification
                if (StaticValue.SERTIFIKAT_DIBATALKAN_NOTIFICATION_WITHOUT_EMAIL_PDF_TYPE.equals(dto.getReminderType())) {
                    masterAutoCancelEmailLog.setFlagDone(1);
                    masterAutoCancelEmailLog.setFlagPolisCanceled(0);
                } else {
                    masterAutoCancelEmailLog.setFlagDone(0);
                }
                masterAutoCancelEmailLogRepository.save(masterAutoCancelEmailLog);
            }
        } catch (Exception e) {
            System.err.println("Error when insert Pre Processing");
            e.printStackTrace();
        }
    }

    public void saveToLoggerAutoCancelSPAJK(List<PreProcessingKonfirmasiSuratDTO> preProcessingDTOList) {
        try{
            for (PreProcessingKonfirmasiSuratDTO dto : preProcessingDTOList) {
                MasterAutoCancelEmailLog masterAutoCancelEmailLog = new MasterAutoCancelEmailLog();
                masterAutoCancelEmailLog.setMeId(null);
                masterAutoCancelEmailLog.setRegSpaj(dto.getRegSpaj());
                masterAutoCancelEmailLog.setCreatedAt(new Date());
                masterAutoCancelEmailLog.setPathAttach(null);
                masterAutoCancelEmailLog.setReminderDays(dto.getReminderDays());
                masterAutoCancelEmailLog.setReminderType(dto.getReminderType());
                masterAutoCancelEmailLog.setFlagDone(0);
                masterAutoCancelEmailLogRepository.save(masterAutoCancelEmailLog);
            }
        } catch (Exception e) {
            System.err.println("Error when insert Pre Processing");
            e.printStackTrace();
        }
    }

    public List<PreProcessingKonfirmasiSuratDTO> generatePreProcessingSurat() {
        List<Object[]> raw = new ArrayList<>();
        List<Object[]> temp = masterPolicyRepository.findAllSuratAutoCancel();
        if (temp != null && !temp.isEmpty()) {
            raw.addAll(temp);
        }

        if (raw.isEmpty()) {
            System.out.println("Tidak ada data pre-processing ditemukan untuk days={}");
        }

        List<PreProcessingKonfirmasiSuratDTO> result = new ArrayList<>();

        for (Object[] row : raw) {
            PreProcessingKonfirmasiSuratDTO dto = new PreProcessingKonfirmasiSuratDTO();
            dto.setRegSpaj(ConvertUtil.safeString(row[0]));
            dto.setReminderDays(ConvertUtil.safeInt(row[1]));
            dto.setReminderType(ConvertUtil.safeInt(row[2]));
            result.add(dto);
        }

        return result;
    }

    public List<PreProcessingKonfirmasiSuratDTO> generatePreProcessingCancelSPAJK() {
        List<Object[]> temp = masterPolicyRepository.findAllPolicyTobeCancelled();
        if (temp.isEmpty()) {
            System.out.println("Tidak ada data pre-processing ditemukan untuk days={}");
        }

        List<PreProcessingKonfirmasiSuratDTO> result = new ArrayList<>();
        for (Object[] row : temp) {
            PreProcessingKonfirmasiSuratDTO dto = new PreProcessingKonfirmasiSuratDTO();
            dto.setRegSpaj(ConvertUtil.safeString(row[0]));
            dto.setReminderDays(ConvertUtil.safeInt(row[1]));
            dto.setReminderType(ConvertUtil.safeInt(row[2]));
            result.add(dto);
        }

        return result;
    }

}
