package com.personnel.model;

import jakarta.persistence.*;

@Entity
@Table(name = "personnel_change")
public class Change {
  @Id
  @Column(name = "id", length = 64)
  private String id;
  @Column(name = "type", nullable = false, length = 16)
  private String type;
  @Column(name = "employee_id", length = 64)
  private String employeeId;
  @Column(name = "employee_name", length = 64)
  private String employeeName;
  @Column(name = "employee_no", length = 32)
  private String employeeNo;
  @Column(name = "occurred_at", length = 32)
  private String occurredAt;
  @Column(name = "before_department_id", length = 64)
  private String beforeDepartmentId;
  @Column(name = "before_position_id", length = 64)
  private String beforePositionId;
  @Column(name = "after_department_id", length = 64)
  private String afterDepartmentId;
  @Column(name = "after_position_id", length = 64)
  private String afterPositionId;
  @Column(name = "reason", length = 255)
  private String reason;
  @Column(name = "operator", length = 64)
  private String operator;

  public Change() {}
  public Change(String id, String type, String employeeId, String employeeName, String employeeNo, String occurredAt,
               String beforeDepartmentId, String beforePositionId, String afterDepartmentId, String afterPositionId,
               String reason, String operator) {
    this.id = id; this.type = type; this.employeeId = employeeId; this.employeeName = employeeName; this.employeeNo = employeeNo;
    this.occurredAt = occurredAt; this.beforeDepartmentId = beforeDepartmentId; this.beforePositionId = beforePositionId;
    this.afterDepartmentId = afterDepartmentId; this.afterPositionId = afterPositionId; this.reason = reason; this.operator = operator;
  }
  public String getId() { return id; }
  public void setId(String id) { this.id = id; }
  public String getType() { return type; }
  public void setType(String type) { this.type = type; }
  public String getEmployeeId() { return employeeId; }
  public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }
  public String getEmployeeName() { return employeeName; }
  public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }
  public String getEmployeeNo() { return employeeNo; }
  public void setEmployeeNo(String employeeNo) { this.employeeNo = employeeNo; }
  public String getOccurredAt() { return occurredAt; }
  public void setOccurredAt(String occurredAt) { this.occurredAt = occurredAt; }
  public String getBeforeDepartmentId() { return beforeDepartmentId; }
  public void setBeforeDepartmentId(String beforeDepartmentId) { this.beforeDepartmentId = beforeDepartmentId; }
  public String getBeforePositionId() { return beforePositionId; }
  public void setBeforePositionId(String beforePositionId) { this.beforePositionId = beforePositionId; }
  public String getAfterDepartmentId() { return afterDepartmentId; }
  public void setAfterDepartmentId(String afterDepartmentId) { this.afterDepartmentId = afterDepartmentId; }
  public String getAfterPositionId() { return afterPositionId; }
  public void setAfterPositionId(String afterPositionId) { this.afterPositionId = afterPositionId; }
  public String getReason() { return reason; }
  public void setReason(String reason) { this.reason = reason; }
  public String getOperator() { return operator; }
  public void setOperator(String operator) { this.operator = operator; }
}
