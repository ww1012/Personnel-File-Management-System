package com.personnel.service;

import com.personnel.model.*;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/** 内存实现（默认）：与原有 Controller 内置 Map 行为一致，重启回到种子数据。 */
@Repository
@Profile("!mysql")
public class InMemoryPersonnelStore implements PersonnelStore {
  private static final DateTimeFormatter TS_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
  private final BCryptPasswordEncoder passwords = new BCryptPasswordEncoder();
  private final Map<String, Account> accounts = new ConcurrentHashMap<>();
  private final Map<String, Employee> employees = new LinkedHashMap<>();
  private final Map<String, Department> departments = new LinkedHashMap<>();
  private final Map<String, Position> positions = new LinkedHashMap<>();
  private final List<Change> changes = new ArrayList<>();

  public InMemoryPersonnelStore() {
    departments.put("d-tech", new Department("d-tech", "技术部", "负责系统研发"));
    departments.put("d-hr", new Department("d-hr", "人力资源部", "负责组织与人员管理"));
    departments.put("d-fin", new Department("d-fin", "财务部", "负责财务与资金管理"));
    departments.put("d-mkt", new Department("d-mkt", "市场部", "负责品牌与市场推广"));
    departments.put("d-sales", new Department("d-sales", "销售部", "负责客户拓展与销售"));
    positions.put("p-engineer", new Position("p-engineer", "软件工程师", "", "d-tech"));
    positions.put("p-qa", new Position("p-qa", "测试工程师", "", "d-tech"));
    positions.put("p-tech-lead", new Position("p-tech-lead", "技术经理", "", "d-tech"));
    positions.put("p-specialist", new Position("p-specialist", "人事专员", "", "d-hr"));
    positions.put("p-recruiter", new Position("p-recruiter", "招聘主管", "", "d-hr"));
    positions.put("p-accountant", new Position("p-accountant", "会计", "", "d-fin"));
    positions.put("p-fin-lead", new Position("p-fin-lead", "财务经理", "", "d-fin"));
    positions.put("p-marketer", new Position("p-marketer", "市场专员", "", "d-mkt"));
    positions.put("p-mkt-lead", new Position("p-mkt-lead", "市场经理", "", "d-mkt"));
    positions.put("p-sales-rep", new Position("p-sales-rep", "销售代表", "", "d-sales"));
    positions.put("p-sales-lead", new Position("p-sales-lead", "销售经理", "", "d-sales"));
    LocalDateTime now = LocalDateTime.now();
    Object[][] emp = {
      {"e-001","E2024001","刘洋","18840241672","liuyang@example.com","北京市朝阳区","本科","d-tech","p-engineer","2024-03-11"},
      {"e-002","E2024002","张敏","13912345678","zhangmin@example.com","上海市浦东新区","硕士","d-hr","p-specialist","2024-05-20"},
      {"e-003","E2024003","陈伟","13700001111","chenwei@example.com","北京市海淀区","本科","d-tech","p-qa","2024-06-01"},
      {"e-004","E2024004","赵磊","13700002222","zhaolei@example.com","北京市西城区","硕士","d-tech","p-tech-lead","2023-11-15"},
      {"e-005","E2024005","王芳","13700003333","wangfang@example.com","上海市静安区","本科","d-hr","p-recruiter","2024-07-10"},
      {"e-006","E2024006","李娜","13700004444","lina@example.com","广州市天河区","本科","d-fin","p-accountant","2024-02-18"},
      {"e-007","E2024007","孙强","13700005555","sunqiang@example.com","深圳市南山区","硕士","d-fin","p-fin-lead","2022-09-01"},
      {"e-008","E2024008","周婷","13700006666","zhouting@example.com","成都市武侯区","本科","d-mkt","p-marketer","2024-08-05"},
      {"e-009","E2024009","吴昊","13700007777","wuhao@example.com","武汉市江汉区","本科","d-sales","p-sales-rep","2024-04-22"},
      {"e-010","E2024010","郑爽","13700008888","zhengshuang@example.com","杭州市西湖区","硕士","d-sales","p-sales-lead","2023-12-03"},
    };
    int idx = 0;
    for (Object[] r : emp) {
      Employee e = new Employee((String) r[0], (String) r[1], (String) r[2], (String) r[3], (String) r[4], (String) r[5], (String) r[6], (String) r[7], (String) r[8], (String) r[9], "ACTIVE", now, now);
      employees.put(e.getId(), e);
      changes.add(new Change("c-" + e.getId(), "ONBOARD", e.getId(), e.getName(), e.getEmployeeNo(), now.minusMinutes(idx * 3).format(TS_FMT), "", "", e.getDepartmentId(), e.getPositionId(), "新增员工入职", "admin"));
      idx++;
    }
    accounts.put("admin", new Account("admin", passwords.encode("Admin@123"), "ADMIN", null, true));
    accounts.put("liuyang", new Account("liuyang", passwords.encode("Employee@123"), "EMPLOYEE", "e-001", true));
    accounts.put("zhangmin", new Account("zhangmin", passwords.encode("Employee@123"), "EMPLOYEE", "e-002", true));
  }

  public Account findAccount(String username) { return accounts.get(username); }
  public List<Account> employeeAccounts() { List<Account> r = new ArrayList<>(); for (Account a : accounts.values()) if ("EMPLOYEE".equals(a.getRole())) r.add(a); return r; }
  public void saveAccount(Account a) { accounts.put(a.getUsername(), a); }
  public List<String> usernamesByEmployeeId(String employeeId) { List<String> r = new ArrayList<>(); for (Account a : accounts.values()) if (employeeId.equals(a.getEmployeeId())) r.add(a.getUsername()); return r; }
  public void disableAccount(String username) { Account a = accounts.get(username); if (a != null) a.setEnabled(false); }
  public void disableAccountByEmployeeId(String employeeId) { for (Account a : accounts.values()) if (employeeId.equals(a.getEmployeeId())) a.setEnabled(false); }
  public void resetPassword(String username, String passwordHash) { Account a = accounts.get(username); if (a != null) a.setPasswordHash(passwordHash); }
  public void createAccount(String username, String passwordHash, String employeeId) { accounts.put(username, new Account(username, passwordHash, "EMPLOYEE", employeeId, true)); }
  public Employee findEmployee(String id) { return employees.get(id); }
  public Collection<Employee> allEmployees() { return employees.values(); }
  public void saveEmployee(Employee e) { employees.put(e.getId(), e); }
  public Department findDepartment(String id) { return departments.get(id); }
  public Collection<Department> allDepartments() { return departments.values(); }
  public void saveDepartment(Department d) { departments.put(d.getId(), d); }
  public void deleteDepartment(String id) { departments.remove(id); }
  public Position findPosition(String id) { return positions.get(id); }
  public Collection<Position> allPositions() { return positions.values(); }
  public Collection<Position> positionsByDepartment(String departmentId) { List<Position> r = new ArrayList<>(); for (Position p : positions.values()) if (departmentId.equals(p.getDepartmentId())) r.add(p); return r; }
  public void savePosition(Position p) { positions.put(p.getId(), p); }
  public void deletePosition(String id) { positions.remove(id); }
  public List<Change> allChanges() { return changes; }
  public void addChange(Change c) { changes.add(c); }

  public void createEmployee(Employee e, String operator) {
    employees.put(e.getId(), e);
    changes.add(new Change("c-" + UUID.randomUUID(), "ONBOARD", e.getId(), e.getName(), e.getEmployeeNo(), LocalDateTime.now().format(TS_FMT), "", "", e.getDepartmentId(), e.getPositionId(), "新增员工入职", operator));
  }
  public void transferEmployee(String id, String departmentId, String positionId, String occurredAt, String reason, String operator) {
    Employee e = employees.get(id); if (e == null) return;
    String oldD = e.getDepartmentId(), oldP = e.getPositionId();
    e.setDepartmentId(departmentId); e.setPositionId(positionId); e.setUpdatedAt(LocalDateTime.now());
    changes.add(new Change("c-" + UUID.randomUUID(), "TRANSFER", e.getId(), e.getName(), e.getEmployeeNo(), occurredAt, oldD, oldP, departmentId, positionId, reason, operator));
  }
  public void resignEmployee(String id, String occurredAt, String reason, String operator) {
    Employee e = employees.get(id); if (e == null) return;
    e.setStatus("RESIGNED"); e.setUpdatedAt(LocalDateTime.now());
    changes.add(new Change("c-" + UUID.randomUUID(), "RESIGNATION", e.getId(), e.getName(), e.getEmployeeNo(), occurredAt, e.getDepartmentId(), e.getPositionId(), e.getDepartmentId(), e.getPositionId(), reason, operator));
    for (Account a : accounts.values()) if (id.equals(a.getEmployeeId())) a.setEnabled(false);
  }
}
