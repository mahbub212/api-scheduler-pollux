package id.co.msiglife.pollux.schduler.service;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import id.co.msiglife.pollux.schduler.dto.ApiResponseDTO;
import id.co.msiglife.pollux.schduler.util.JwtUtil;
import org.jsoup.Jsoup;
import org.jsoup.helper.W3CDom;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Entities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import static id.co.msiglife.pollux.schduler.util.ConvertUtil.stringBlank;

@Service
public class SurrenderService {

    @Value("${spring.custom.storage.path}")
    private String STORAGE_PATH;

    @Autowired
    private JwtUtil jwtUtil;

    public ApiResponseDTO<Map<String, Object>> generateSuratKonfirmasiPolisAkanDibatalkan(String regSpaj, String token) throws Exception {
        Map<String, Object> result = new HashMap<>();
        Integer userId = stringBlank(token) ? null : jwtUtil.getUserIdFromJwtToken(token);

        // read template file
        String filePath = "";
        String content;

        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(filePath)) {
            assert inputStream != null;
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                StringBuilder contentBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    contentBuilder.append(line).append(System.lineSeparator());
                }
                content = contentBuilder.toString();
            }
        }

        String base64Font = encodeFontToBase64("template/fonts/Ubuntu/Ubuntu-Regular.ttf");
        content = content.replace("[[MY_FONT_PATH]]", base64Font);

        String basePath = getBase64Image("template/images/watermark.png");
        content = content.replace("../../images/watermark.png", basePath);

        String basePathHeader = getBase64Image("template/images/watermark_header.png");
        content = content.replace("../../images/watermark_header.png", basePathHeader);

        String basePathFooter1 = getBase64Image("template/images/watermark_footer1.png");
        content = content.replace("../../images/watermark_footer1.png", basePathFooter1);

        String basePathFooter2 = getBase64Image("template/images/watermark_footer2.png");
        content = content.replace("../../images/watermark_footer2.png", basePathFooter2);

        //replacing template content
        content = content.replace("{nomor_spaj}", regSpaj);

        // create string HTML to a JSoup Document to render XHTML
        Document document = Jsoup.parse(content);
        document.outputSettings().syntax(Document.OutputSettings.Syntax.xml);
        document.outputSettings().escapeMode(Entities.EscapeMode.xhtml);
        document.outputSettings().charset("UTF-8");

        // check if folder hasn't existed
        String folderPath = STORAGE_PATH + regSpaj;

        File folder = new File(folderPath);
        File parentDirectory = folder.getParentFile();
        if (parentDirectory.canWrite()) {
            if (!folder.exists()) {
                folder.mkdirs();
            }
        } else {
            throw new IOException("No write permissions in the parent directory: " + parentDirectory);
        }

        // PdfRendererBuilder will take this XHTML document and create a PDF as the output file
//        List<MasterUploadDokumen> uploadDokumenList = masterUploadDokumenRepository.findByRegSpajAndJenisDokumenId(regSpaj, 15); // find Surat Konfirmasi Polis akan Dibatalkan docs
        String outputPdf = folderPath + "/" + regSpaj + "_Surat Konfirmasi Polis akan Dibatalkan" + ".pdf";

        try (OutputStream os = Files.newOutputStream(Paths.get(outputPdf))) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.withUri(outputPdf);
            builder.toStream(os);
            builder.withW3cDocument(new W3CDom().fromJsoup(document), folderPath + "/");
            builder.run();
        }

        // insert into uploaded file list
//        MasterUploadDokumen uploadDokumen;

        // check last id
//        Integer lastDokumenId = masterUploadDokumenRepository.findLastDokumenIdByRegSpaj(regSpaj);
//        lastDokumenId = lastDokumenId != null ? lastDokumenId : 0;

//        uploadDokumen = new MasterUploadDokumen();
//        uploadDokumen.setRegSpaj(regSpaj);
//        uploadDokumen.setDokumenId(lastDokumenId + 1);
//        uploadDokumen.setJenisDokumenId(15);
//        uploadDokumen.setUploadCreatedDate(new Date());
//        uploadDokumen.setLusId(userId);
//        uploadDokumen.setPathFile(outputPdf);
//        masterUploadDokumenRepository.save(uploadDokumen);

        return new ApiResponseDTO<>(200, "Surat Konfirmasi Polis Akan Dibatalkan has been generated successfully.", result);
    }

    private String getBase64Image(String resourcePath) throws IOException {
        ClassPathResource imgFile = new ClassPathResource(resourcePath);
        try (InputStream imgInputStream = imgFile.getInputStream()) {
            byte[] imageBytes = StreamUtils.copyToByteArray(imgInputStream);
            return "data:image/png;base64," + Base64.getEncoder().encodeToString(imageBytes);
        }
    }

    private String encodeFontToBase64(String fontPath) throws Exception {
        ClassPathResource fontResource = new ClassPathResource(fontPath);
        InputStream fontStream = fontResource.getInputStream();
        byte[] fontBytes = readAllBytes(fontStream);
        return "data:font/ttf;base64," + Base64.getEncoder().encodeToString(fontBytes);
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
}
