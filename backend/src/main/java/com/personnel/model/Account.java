package com.personnel.model;

import jakarta.persistence.*;

@Entity
@Table(name = "account")
public class Account {
  @Id
  @Column(name = "username", nullable = false, length = 64)
  private String username;
  @Column(name = "password_hash", nullable = false)
  private String passwordHash;
  @Column(name = "role", nullable = false, length = 16)
  private String role;
  @Column(name = "employee_id")
  private String employeeId;
  @Column(name = "enabled", nullable = false)
  private boolean enabled;

  public Account() {}
  public Account(String username, String passwordHash, String role, String employeeId, boolean enabled) {
    this.username = username; this.passwordHash = passwordHash; this.role = role;
    this.employeeId = employeeId; this.enabled = enabled;
  }
  public String getUsername() { return username; }
  public void setUsername(String username) { this.username = username; }
  public String getPasswordHash() { return passwordHash; }
  public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
  public String getRole() { return role; }
  public void setRole(String role) { this.role = role; }
  public String getEmployeeId() { return employeeId; }
  public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }
  public boolean isEnabled() { return enabled; }
  public void setEnabled(boolean enabled) { this.enabled = enabled; }
}
