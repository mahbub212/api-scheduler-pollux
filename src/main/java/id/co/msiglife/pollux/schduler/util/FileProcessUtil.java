package id.co.msiglife.pollux.schduler.util;

import id.co.msiglife.pollux.schduler.entity.MasterUploadDokumen;
import id.co.msiglife.pollux.schduler.repository.MasterUploadDokumenRepository;
import id.co.msiglife.pollux.schduler.repository.MasterUploadJenisDokumenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;

@Component
public class FileProcessUtil {

    @Autowired
    MasterUploadDokumenRepository masterUploadDokumenRepository;

    @Autowired
    MasterUploadJenisDokumenRepository masterUploadJenisDokumenRepository;

    public MasterUploadDokumen createOrUpdateFileMasterUploadDokumen(
            String regSpaj,
            Integer categoryFileId,
            String filePath,
            Integer lusId
    ) {
        MasterUploadDokumen masterUploadDokumen = this.masterUploadDokumenRepository.findFirstByRegSpajAndJenisDokumenId(regSpaj, categoryFileId)
                .orElse(new MasterUploadDokumen());

        boolean isUpdate = masterUploadDokumen.getDokumenId() != null;

        masterUploadDokumen.setRegSpaj(regSpaj);
        masterUploadDokumen.setJenisDokumenId(categoryFileId);
        masterUploadDokumen.setPathFile(filePath);
        masterUploadDokumen.setLusId(lusId == null || lusId.equals(null) ? 0 : lusId); // Assuming lusId is not set here, adjust as necessary
        masterUploadDokumen.setUploadCreatedDate(isUpdate ? masterUploadDokumen.getUploadCreatedDate() : new java.util.Date());
        masterUploadDokumen.setUploadUpdatedDate(isUpdate ? new java.util.Date() : null);

        Integer docId = isUpdate
                ? masterUploadDokumen.getDokumenId()
                : this.masterUploadDokumenRepository.findLastDokumenIdByRegSpaj(regSpaj) + 1;
        masterUploadDokumen.setDokumenId(docId);
        this.masterUploadDokumenRepository.save(masterUploadDokumen);

        return masterUploadDokumen;
    }

    private static byte[] readAllBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[16384]; // Buffer 16KB

        while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }

        return buffer.toByteArray();
    }

    public String encodeFontToBase64(String fontPath) throws Exception {
        ClassPathResource fontResource = new ClassPathResource(fontPath);
        InputStream fontStream = fontResource.getInputStream();
        byte[] fontBytes = readAllBytes(fontStream);
        return "data:font/ttf;base64," + Base64.getEncoder().encodeToString(fontBytes);
    }

    public String getBase64Image(String resourcePath) throws IOException {
        ClassPathResource imgFile = new ClassPathResource(resourcePath);
        try (InputStream imgInputStream = imgFile.getInputStream()) {
            byte[] imageBytes = StreamUtils.copyToByteArray(imgInputStream);
            return "data:image/png;base64," + Base64.getEncoder().encodeToString(imageBytes);
        }
    }

    public void deleteSingleFile(Path path) {
        if (path != null) {
            try {
                Files.deleteIfExists(path);
            } catch (IOException ex) {
                System.err.println("Failed to delete file: " + path + " | Error: " + ex.getMessage());
            }
        }
    }
}
