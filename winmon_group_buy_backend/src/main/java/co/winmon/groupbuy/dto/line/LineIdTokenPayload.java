package co.winmon.groupbuy.dto.line;

import lombok.Data;

@Data
public class LineIdTokenPayload {
    private String iss;

    private String sub;

    private String aud;

    private long exp;

    private long iat;

    private String name;

    private String picture;

    private String email;
}