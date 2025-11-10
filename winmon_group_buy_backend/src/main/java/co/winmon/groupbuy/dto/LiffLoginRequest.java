package co.winmon.groupbuy.dto;

import lombok.Data;

@Data
public class LiffLoginRequest {
    // React (LIFF) 透過 liff.getAccessToken() 取得的 Token
    private String lineAccessToken;
}