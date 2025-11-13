package co.winmon.groupbuy.dto.line;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class LineVerifyResponse {
    private String scope;

    @JsonProperty("client_id")
    private String clientId;

    @JsonProperty("expires_in")
    private long expiresIn;
}