package id.co.msiglife.pollux.schduler.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ApiResponseDTO<T> {
    private int code;
    private String message;
    private T data;
    private Integer totalPages;

    public ApiResponseDTO(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public ApiResponseDTO(int code, String message, T data, Integer totalPages) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.totalPages = totalPages;
    }
}
