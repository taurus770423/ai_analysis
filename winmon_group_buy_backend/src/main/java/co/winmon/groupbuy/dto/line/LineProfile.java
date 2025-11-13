package co.winmon.groupbuy.dto.line;

import lombok.Data;

@Data
public class LineProfile {
    private String userId;

    private String displayName;

    private String pictureUrl;

    private String statusMessage;
}