package id.co.msiglife.pollux.schduler.util;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

@Component
public class FileProcessUtil {

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
}
