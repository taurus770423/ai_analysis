package co.winmon.groupbuy.dto.auth;

import jakarta.validation.constraints.NotBlank;

public record LineLoginRequest(
        @NotBlank String lineAccessToken
) {}