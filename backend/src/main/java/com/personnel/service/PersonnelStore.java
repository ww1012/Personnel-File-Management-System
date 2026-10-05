package com.personnel.service;

import com.personnel.model.*;
import java.util.*;

/** 人事数据存储抽象：内存与 MySQL 两种实现按 profile 切换，Controller 只依赖此接口。 */
public interface PersonnelStore {
  // accounts
  Account findAccount(String username);
  List<Account> employeeAccounts();
  void saveAccount(Account a);
  List<String> usernamesByEmployeeId(String employeeId);
  void disableAccount(String username);
  void disableAccountByEmployeeId(String employeeId);
  void resetPassword(String username, String passwordHash);
  void createAccount(String username, String passwordHash, String employeeId);
  // employees
  Employee findEmployee(String id);
  Collection<Employee> allEmployees();
  void saveEmployee(Employee e);
  // departments
  Department findDepartment(String id);
  Collection<Department> allDepartments();
  void saveDepartment(Department d);
  void deleteDepartment(String id);
  // positions
  Position findPosition(String id);
  Collection<Position> allPositions();
  Collection<Position> positionsByDepartment(String departmentId);
  void savePosition(Position p);
  void deletePosition(String id);
  // changes
  List<Change> allChanges();
  void addChange(Change c);
  // aggregates (JPA 实现中事务化)
  void createEmployee(Employee e, String operator);
  void transferEmployee(String id, String departmentId, String positionId, String occurredAt, String reason, String operator);
  void resignEmployee(String id, String occurredAt, String reason, String operator);
}
