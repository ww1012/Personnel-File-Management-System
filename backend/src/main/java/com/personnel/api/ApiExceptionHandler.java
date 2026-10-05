package com.personnel.api;
import java.util.Map;
import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
@RestControllerAdvice public class ApiExceptionHandler {
  @ExceptionHandler(ApiException.class) ResponseEntity<Map<String,String>> handle(ApiException e) { return ResponseEntity.status(e.status).body(Map.of("message",e.getMessage())); }
  @ExceptionHandler(MethodArgumentNotValidException.class) ResponseEntity<Map<String,String>> validation(MethodArgumentNotValidException e) { return ResponseEntity.badRequest().body(Map.of("message",e.getBindingResult().getFieldError()==null?"参数校验失败":e.getBindingResult().getFieldError().getDefaultMessage())); }
  @ExceptionHandler(Exception.class) ResponseEntity<Map<String,String>> unexpected(Exception e) { return ResponseEntity.status(500).body(Map.of("message","服务暂时不可用")); }
}
class ApiException extends RuntimeException { final HttpStatus status; ApiException(HttpStatus status,String message){super(message);this.status=status;} }
