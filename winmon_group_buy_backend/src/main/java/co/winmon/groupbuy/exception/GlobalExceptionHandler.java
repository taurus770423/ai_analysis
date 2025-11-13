package co.winmon.groupbuy.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(LoginFailedException.class)
    public ResponseEntity<Map<String, String>> handleLoginFailed(LoginFailedException ex) {
        // 當登入失敗 (401) 時，回傳錯誤訊息
        return new ResponseEntity<>(
                Map.of("error", "Unauthorized", "message", ex.getMessage()),
                HttpStatus.UNAUTHORIZED
        );
    }

    // 其他通用例外處理
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGeneralException(Exception ex) {
        return new ResponseEntity<>(
                Map.of("error", "Internal Server Error", "message", ex.getMessage()),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}