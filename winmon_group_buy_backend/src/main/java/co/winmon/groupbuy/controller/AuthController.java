package co.winmon.groupbuy.controller;

import co.winmon.groupbuy.dto.JwtResponse;
import co.winmon.groupbuy.dto.LineLoginRequest;
import co.winmon.groupbuy.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/line/login")
    public ResponseEntity<JwtResponse> lineLogin(@RequestBody LineLoginRequest loginRequest) {
        JwtResponse jwtResponse = authService.lineLogin(loginRequest);
        return ResponseEntity.ok(jwtResponse);
    }
}