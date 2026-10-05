package com.personnel;

import static org.assertj.core.api.Assertions.assertThat;
import java.util.Map;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class BusinessRulesIntegrationTest {
  @Autowired TestRestTemplate http;

  @Test void enforcesEmployeeIsolationAndCriticalBusinessRules() {
    String admin = login("admin", "Admin@123");
    String liuyang = login("liuyang", "Employee@123");
    String zhangmin = login("zhangmin", "Employee@123");

    var firstPage = http.exchange("/api/employees?page=1&size=1", HttpMethod.GET, request(admin), Map.class).getBody();
    assertThat(firstPage).containsEntry("total", 2);
    assertThat((List<?>) firstPage.get("content")).hasSize(1);
    var emptySearch = http.exchange("/api/employees?keyword=不存在&page=1&size=10", HttpMethod.GET, request(admin), Map.class).getBody();
    assertThat(emptySearch).containsEntry("total", 0);
    assertThat((List<?>) emptySearch.get("content")).isEmpty();

    assertThat(http.exchange("/api/dashboard", HttpMethod.GET, request(liuyang), Map.class).getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    assertThat(http.exchange("/api/employees/e-002", HttpMethod.GET, request(liuyang), Map.class).getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);

    var own = http.exchange("/api/profile", HttpMethod.GET, request(liuyang), Map.class);
    var other = http.exchange("/api/profile", HttpMethod.GET, request(zhangmin), Map.class);
    assertThat(own.getBody()).containsEntry("employeeNo", "E2024001");
    assertThat(other.getBody()).containsEntry("employeeNo", "E2024002");

    var updated = http.exchange("/api/profile", HttpMethod.PUT, request(liuyang, Map.of("phone", "18600000000", "name", "越权姓名")), Map.class);
    assertThat(updated.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(updated.getBody()).containsEntry("name", "刘洋").containsEntry("phone", "18600000000");

    assertThat(http.exchange("/api/departments/d-tech", HttpMethod.DELETE, request(admin), Map.class).getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    assertThat(http.exchange("/api/positions/p-engineer", HttpMethod.DELETE, request(admin), Map.class).getStatusCode()).isEqualTo(HttpStatus.CONFLICT);

    var transfer = http.exchange("/api/employees/e-001/transfer", HttpMethod.POST, request(admin, Map.of("departmentId", "d-hr", "positionId", "p-specialist", "occurredAt", "2026-10-01", "reason", "岗位调整")), Map.class);
    assertThat(transfer.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(http.exchange("/api/departments/d-tech", HttpMethod.DELETE, request(admin), Map.class).getStatusCode()).isEqualTo(HttpStatus.CONFLICT);

    var resign = http.exchange("/api/employees/e-001/resign", HttpMethod.POST, request(admin, Map.of("occurredAt", "2026-10-02", "reason", "个人原因")), Map.class);
    assertThat(resign.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(http.exchange("/api/auth/me", HttpMethod.GET, request(liuyang), Map.class).getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);

    var passwordChange = http.exchange("/api/auth/password", HttpMethod.PUT, request(zhangmin, Map.of("currentPassword", "Employee@123", "newPassword", "Employee@456")), Map.class);
    assertThat(passwordChange.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(http.exchange("/api/auth/me", HttpMethod.GET, request(zhangmin), Map.class).getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    assertThat(loginResponse("zhangmin", "Employee@456").getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  private String login(String username, String password) {
    var response = loginResponse(username, password);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    return (String) response.getBody().get("token");
  }
  private ResponseEntity<Map> loginResponse(String username, String password) { return http.postForEntity("/api/auth/login", Map.of("username", username, "password", password), Map.class); }
  private HttpEntity<?> request(String token) { return new HttpEntity<>(new HttpHeaders() {{ setBearerAuth(token); }}); }
  private HttpEntity<Map<String,String>> request(String token, Map<String,String> body) { var headers = new HttpHeaders(); headers.setBearerAuth(token); headers.setContentType(MediaType.APPLICATION_JSON); return new HttpEntity<>(body, headers); }

  @Test void completesAdministratorLifecycleWithConsistentHistory() {
    String admin = login("admin", "Admin@123");
    var department = http.exchange("/api/departments", HttpMethod.POST, request(admin, Map.of("name", "验收部门", "description", "联调用部门")), Map.class);
    var position = http.exchange("/api/positions", HttpMethod.POST, request(admin, Map.of("name", "验收岗位", "description", "联调用岗位")), Map.class);
    assertThat(department.getStatusCode()).isEqualTo(HttpStatus.OK); assertThat(position.getStatusCode()).isEqualTo(HttpStatus.OK);
    String departmentId = (String) department.getBody().get("id"), positionId = (String) position.getBody().get("id");
    assertThat(http.exchange("/api/departments", HttpMethod.POST, request(admin, Map.of("name", "验收部门")), Map.class).getStatusCode()).isEqualTo(HttpStatus.CONFLICT);

    var invalidEmployee = Map.of("employeeNo", "E2099000", "name", "无效关联", "departmentId", "missing", "positionId", positionId, "hireDate", "2026-10-01");
    assertThat(http.exchange("/api/employees", HttpMethod.POST, request(admin, invalidEmployee), Map.class).getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    var body = Map.of("employeeNo", "E2099001", "name", "验收员工", "phone", "13600000000", "email", "accept@example.com", "address", "测试地址", "education", "本科", "departmentId", departmentId, "positionId", positionId, "hireDate", "2026-10-01");
    var created = http.exchange("/api/employees", HttpMethod.POST, request(admin, body), Map.class);
    assertThat(created.getStatusCode()).isEqualTo(HttpStatus.OK);
    String employeeId = (String) created.getBody().get("id");
    assertThat(http.exchange("/api/employees", HttpMethod.POST, request(admin, body), Map.class).getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    assertThat(http.exchange("/api/employees?keyword=E2099001&page=1&size=10", HttpMethod.GET, request(admin), Map.class).getBody()).containsEntry("total", 1);

    var edited = Map.of("name", "验收员工改名", "phone", "13700000000", "email", "changed@example.com", "address", "新地址", "education", "硕士", "hireDate", "2026-10-02", "departmentId", "ignored");
    var updated = http.exchange("/api/employees/" + employeeId, HttpMethod.PUT, request(admin, edited), Map.class);
    assertThat(updated.getBody()).containsEntry("departmentId", departmentId).containsEntry("name", "验收员工改名");
    assertThat(http.exchange("/api/accounts", HttpMethod.POST, request(admin, Map.of("username", "acceptuser", "employeeId", employeeId, "password", "Accept@123")), Map.class).getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(http.exchange("/api/accounts", HttpMethod.POST, request(admin, Map.of("username", "acceptuser", "employeeId", employeeId, "password", "Accept@123")), Map.class).getStatusCode()).isEqualTo(HttpStatus.CONFLICT);

    var transfer = http.exchange("/api/employees/" + employeeId + "/transfer", HttpMethod.POST, request(admin, Map.of("departmentId", "d-tech", "positionId", "p-engineer", "occurredAt", "2026-10-03", "reason", "验收调岗")), Map.class);
    assertThat(transfer.getBody()).containsEntry("departmentId", "d-tech").containsEntry("positionId", "p-engineer");
    var changes = http.exchange("/api/changes?keyword=E2099001", HttpMethod.GET, request(admin), Map.class);
    assertThat(changes.getBody()).containsEntry("total", 2);
    assertThat(http.exchange("/api/departments/" + departmentId, HttpMethod.DELETE, request(admin), Map.class).getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    assertThat(http.exchange("/api/positions/" + positionId, HttpMethod.DELETE, request(admin), Map.class).getStatusCode()).isEqualTo(HttpStatus.CONFLICT);

    int totalBefore = (Integer) http.exchange("/api/dashboard", HttpMethod.GET, request(admin), Map.class).getBody().get("totalEmployees");
    assertThat(http.exchange("/api/employees/" + employeeId + "/resign", HttpMethod.POST, request(admin, Map.of("occurredAt", "2026-10-04", "reason", "验收离职")), Map.class).getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(http.exchange("/api/employees/" + employeeId, HttpMethod.GET, request(admin), Map.class).getBody()).containsEntry("status", "RESIGNED");
    assertThat(loginResponse("acceptuser", "Accept@123").getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    var dashboard = http.exchange("/api/dashboard", HttpMethod.GET, request(admin), Map.class).getBody();
    assertThat(dashboard).containsEntry("totalEmployees", totalBefore).containsEntry("resignedEmployees", 1);
  }
}
