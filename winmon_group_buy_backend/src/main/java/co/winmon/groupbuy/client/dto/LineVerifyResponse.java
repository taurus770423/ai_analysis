package co.winmon.groupbuy.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

// 這是一個 DTO，用於接收 Line API 的回應
// /verify 和 /profile 的組合
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class LineVerifyResponse {

    // from /verify
    private String clientId; // 應該等於我們的 channel id
    private Long expiresIn;

    // from /profile
    private String userId; // Line 的用戶 ID
    private String displayName;
    private String pictureUrl;
    private String statusMessage;
}