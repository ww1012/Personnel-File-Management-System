package com.personnel.api;

import java.time.OffsetDateTime;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** 独立 Controller：供前端和联调脚本确认后端可用。 */
@RestController
@RequestMapping("/api")
public class HealthController {
  @GetMapping("/health")
  public Map<String, String> health() {
    return Map.of("status", "UP", "time", OffsetDateTime.now().toString());
  }
}
