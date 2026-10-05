package com.personnel.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "employee")
public class Employee {
  @Id
  @Column(name = "id", length = 64)
  private String id;
  @Column(name = "employee_no", unique = true, nullable = false, length = 32)
  private String employeeNo;
  @Column(name = "name", nullable = false, length = 64)
  private String name;
  @Column(name = "phone", length = 20)
  private String phone;
  @Column(name = "email", length = 128)
  private String email;
  @Column(name = "address", length = 255)
  private String address;
  @Column(name = "education", length = 32)
  private String education;
  @Column(name = "department_id", nullable = false, length = 64)
  private String departmentId;
  @Column(name = "position_id", nullable = false, length = 64)
  private String positionId;
  @Column(name = "hire_date", length = 20)
  private String hireDate;
  @Column(name = "status", nullable = false, length = 16)
  private String status;
  @Column(name = "created_at")
  private LocalDateTime createdAt;
  @Column(name = "updated_at")
  private LocalDateTime updatedAt;

  public Employee() {}
  public Employee(String id, String employeeNo, String name, String phone, String email, String address,
                  String education, String departmentId, String positionId, String hireDate,
                  String status, LocalDateTime createdAt, LocalDateTime updatedAt) {
    this.id = id; this.employeeNo = employeeNo; this.name = name; this.phone = phone; this.email = email;
    this.address = address; this.education = education; this.departmentId = departmentId; this.positionId = positionId;
    this.hireDate = hireDate; this.status = status; this.createdAt = createdAt; this.updatedAt = updatedAt;
  }
  public String getId() { return id; }
  public void setId(String id) { this.id = id; }
  public String getEmployeeNo() { return employeeNo; }
  public void setEmployeeNo(String employeeNo) { this.employeeNo = employeeNo; }
  public String getName() { return name; }
  public void setName(String name) { this.name = name; }
  public String getPhone() { return phone; }
  public void setPhone(String phone) { this.phone = phone; }
  public String getEmail() { return email; }
  public void setEmail(String email) { this.email = email; }
  public String getAddress() { return address; }
  public void setAddress(String address) { this.address = address; }
  public String getEducation() { return education; }
  public void setEducation(String education) { this.education = education; }
  public String getDepartmentId() { return departmentId; }
  public void setDepartmentId(String departmentId) { this.departmentId = departmentId; }
  public String getPositionId() { return positionId; }
  public void setPositionId(String positionId) { this.positionId = positionId; }
  public String getHireDate() { return hireDate; }
  public void setHireDate(String hireDate) { this.hireDate = hireDate; }
  public String getStatus() { return status; }
  public void setStatus(String status) { this.status = status; }
  public LocalDateTime getCreatedAt() { return createdAt; }
  public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
  public LocalDateTime getUpdatedAt() { return updatedAt; }
  public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
