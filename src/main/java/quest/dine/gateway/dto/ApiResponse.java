package quest.dine.gateway.dto;

import lombok.Data;

@Data
public class ApiResponse {
    private final String message;
    private final int statusCode;
}