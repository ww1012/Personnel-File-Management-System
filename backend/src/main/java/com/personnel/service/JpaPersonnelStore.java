package com.personnel.service;

import com.personnel.model.*;
import com.personnel.repository.*;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/** JPA/MySQL 实现（mysql profile 激活时生效）。写/聚合操作事务化。 */
@Repository
@Profile("mysql")
public class JpaPersonnelStore implements PersonnelStore {
  private static final DateTimeFormatter TS_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
  private final AccountRepository accountRepository;
  private final EmployeeRepository employeeRepository;
  private final DepartmentRepository departmentRepository;
  private final PositionRepository positionRepository;
  private final ChangeRepository changeRepository;

  public JpaPersonnelStore(AccountRepository ar, EmployeeRepository er, DepartmentRepository dr, PositionRepository pr, ChangeRepository cr) {
    this.accountRepository = ar; this.employeeRepository = er; this.departmentRepository = dr;
    this.positionRepository = pr; this.changeRepository = cr;
  }

  public Account findAccount(String username) { return accountRepository.findById(username).orElse(null); }
  public List<Account> employeeAccounts() { List<Account> r = new ArrayList<>(); for (Account a : accountRepository.findAll()) if ("EMPLOYEE".equals(a.getRole())) r.add(a); return r; }
  public void saveAccount(Account a) { accountRepository.save(a); }
  public List<String> usernamesByEmployeeId(String employeeId) { List<String> r = new ArrayList<>(); for (Account a : accountRepository.findByEmployeeId(employeeId)) r.add(a.getUsername()); return r; }
  public void disableAccount(String username) { Account a = accountRepository.findById(username).orElse(null); if (a != null) { a.setEnabled(false); accountRepository.save(a); } }
  public void disableAccountByEmployeeId(String employeeId) { for (Account a : accountRepository.findByEmployeeId(employeeId)) { a.setEnabled(false); accountRepository.save(a); } }
  public void resetPassword(String username, String passwordHash) { Account a = accountRepository.findById(username).orElse(null); if (a != null) { a.setPasswordHash(passwordHash); accountRepository.save(a); } }
  public void createAccount(String username, String passwordHash, String employeeId) { accountRepository.save(new Account(username, passwordHash, "EMPLOYEE", employeeId, true)); }
  public Employee findEmployee(String id) { return employeeRepository.findById(id).orElse(null); }
  public Collection<Employee> allEmployees() { return employeeRepository.findAll(); }
  public void saveEmployee(Employee e) { employeeRepository.save(e); }
  public Department findDepartment(String id) { return departmentRepository.findById(id).orElse(null); }
  public Collection<Department> allDepartments() { return departmentRepository.findAll(); }
  public void saveDepartment(Department d) { departmentRepository.save(d); }
  public void deleteDepartment(String id) { departmentRepository.deleteById(id); }
  public Position findPosition(String id) { return positionRepository.findById(id).orElse(null); }
  public Collection<Position> allPositions() { return positionRepository.findAll(); }
  public Collection<Position> positionsByDepartment(String departmentId) { return positionRepository.findByDepartmentId(departmentId); }
  public void savePosition(Position p) { positionRepository.save(p); }
  public void deletePosition(String id) { positionRepository.deleteById(id); }
  public List<Change> allChanges() { return changeRepository.findAll(); }
  public void addChange(Change c) { changeRepository.save(c); }

  @Transactional
  public void createEmployee(Employee e, String operator) {
    employeeRepository.save(e);
    changeRepository.save(new Change("c-" + UUID.randomUUID(), "ONBOARD", e.getId(), e.getName(), e.getEmployeeNo(), LocalDateTime.now().format(TS_FMT), "", "", e.getDepartmentId(), e.getPositionId(), "新增员工入职", operator));
  }
  @Transactional
  public void transferEmployee(String id, String departmentId, String positionId, String occurredAt, String reason, String operator) {
    Employee e = employeeRepository.findById(id).orElse(null); if (e == null) return;
    String oldD = e.getDepartmentId(), oldP = e.getPositionId();
    e.setDepartmentId(departmentId); e.setPositionId(positionId); e.setUpdatedAt(LocalDateTime.now());
    employeeRepository.save(e);
    changeRepository.save(new Change("c-" + UUID.randomUUID(), "TRANSFER", e.getId(), e.getName(), e.getEmployeeNo(), occurredAt, oldD, oldP, departmentId, positionId, reason, operator));
  }
  @Transactional
  public void resignEmployee(String id, String occurredAt, String reason, String operator) {
    Employee e = employeeRepository.findById(id).orElse(null); if (e == null) return;
    e.setStatus("RESIGNED"); e.setUpdatedAt(LocalDateTime.now());
    employeeRepository.save(e);
    changeRepository.save(new Change("c-" + UUID.randomUUID(), "RESIGNATION", e.getId(), e.getName(), e.getEmployeeNo(), occurredAt, e.getDepartmentId(), e.getPositionId(), e.getDepartmentId(), e.getPositionId(), reason, operator));
    for (Account a : accountRepository.findByEmployeeId(id)) { a.setEnabled(false); accountRepository.save(a); }
  }
}
