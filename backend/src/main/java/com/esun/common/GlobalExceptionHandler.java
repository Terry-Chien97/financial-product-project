package com.esun.common;
import java.sql.SQLException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log =
            LoggerFactory.getLogger(GlobalExceptionHandler.class);
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleBadRequest(IllegalArgumentException e) {
        String message = e.getMessage() == null ? "請求資料不正確" : e.getMessage();
        return ResponseEntity.badRequest().body(Map.of("message", message));
    }
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, String>> handleStatus(ResponseStatusException e) {
        String message = e.getReason() == null ? "操作失敗" : e.getReason();
        return ResponseEntity.status(e.getStatusCode()).body(Map.of("message", message));
    }
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> handleConflict(DataIntegrityViolationException e) {
        ResponseEntity<Map<String, String>> response = handleSqlError(e);
        if (response != null) return response;
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("message", "資料重複或仍有關聯紀錄，無法完成操作"));
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleOther(Exception e) {
        ResponseEntity<Map<String, String>> response = handleSqlError(e);
        if (response != null) return response;
        if (e instanceof ErrorResponse error) {
            int status = error.getStatusCode().value();
            String message = status == 404 ? "找不到指定的 API" : "請求無法處理";
            return ResponseEntity.status(status).body(Map.of("message", message));
        }
        log.error("系統發生未預期錯誤", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "系統處理失敗，請稍後再試"));
    }
    private ResponseEntity<Map<String, String>> handleSqlError(Throwable error) {
        for (Throwable cause = error; cause != null; cause = cause.getCause()) {
            if (!(cause instanceof SQLException sql)) continue;
            HttpStatus status = switch (sql.getErrorCode()) {
                case 50021, 50022, 50026, 50031, 50032, 50033 -> HttpStatus.BAD_REQUEST;
                case 50023, 50024, 50027, 50041 -> HttpStatus.NOT_FOUND;
                case 50025, 50028 -> HttpStatus.CONFLICT;
                default -> null;
            };
            if (status != null) {
                return ResponseEntity.status(status)
                        .body(Map.of("message", sql.getMessage()));
            }
        }
        return null;
    }
}
