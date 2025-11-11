package co.winmon.groupbuy.controller;

import co.winmon.groupbuy.dto.auth.LineLoginRequest;
import co.winmon.groupbuy.dto.auth.LoginResponse;
import co.winmon.groupbuy.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/line/login")
    public ResponseEntity<LoginResponse> authenticateUser(@Valid @RequestBody LineLoginRequest loginRequest) {
        LoginResponse loginResponse = authService.lineLogin(loginRequest);
        return ResponseEntity.ok(loginResponse);
    }
}