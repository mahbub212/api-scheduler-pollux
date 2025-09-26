package id.co.msiglife.pollux.schduler.service;

import id.co.msiglife.pollux.schduler.dto.PreProcessingKonfirmasiSuratDTO;
import id.co.msiglife.pollux.schduler.entity.MasterAutoCancelEmailLog;
import id.co.msiglife.pollux.schduler.repository.MasterAutoCancelEmailLogRepository;
import id.co.msiglife.pollux.schduler.repository.MasterPolicyRepository;
import id.co.msiglife.pollux.schduler.util.ConvertUtil;
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
                masterAutoCancelEmailLog.setFlagDone(0);
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
                masterAutoCancelEmailLog.setFlagDone(0);
                masterAutoCancelEmailLogRepository.save(masterAutoCancelEmailLog);
            }
        } catch (Exception e) {
            System.err.println("Error when insert Pre Processing");
            e.printStackTrace();
        }
    }

    public List<PreProcessingKonfirmasiSuratDTO> generatePreProcessingSurat(List<Integer> days) {
        List<Object[]> raw = new ArrayList<>();
        for (Integer reminderDays : days) {
            List<Object[]> temp = masterPolicyRepository.findAllSuratAutoCancel(reminderDays);
            if (temp != null && !temp.isEmpty()) {
                raw.addAll(temp);
            }
        }

        if (raw.isEmpty()) {
            System.out.println("Tidak ada data pre-processing ditemukan untuk days={}");
        }

        List<PreProcessingKonfirmasiSuratDTO> result = new ArrayList<>();

        for (Object[] row : raw) {
            PreProcessingKonfirmasiSuratDTO dto = new PreProcessingKonfirmasiSuratDTO();
            dto.setRegSpaj(ConvertUtil.safeString(row[0]));
            dto.setReminderDays(ConvertUtil.safeInt(row[1]));
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
            dto.setReminderDays(ConvertUtil.safeInt(61));
            result.add(dto);
        }

        return result;
    }

}
