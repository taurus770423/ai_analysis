package co.winmon.groupbuy.payload;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LineVerifyResponse {
    private String sub;
    private String name;
    private String picture;
    private String error;
    private String error_description;
}