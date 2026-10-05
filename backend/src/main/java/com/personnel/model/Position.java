package com.personnel.model;

import jakarta.persistence.*;

@Entity
@Table(name = "position")
public class Position {
  @Id
  @Column(name = "id", length = 64)
  private String id;
  @Column(name = "name", nullable = false, length = 64)
  private String name;
  @Column(name = "description", length = 255)
  private String description;
  @Column(name = "department_id", length = 64)
  private String departmentId;

  public Position() {}
  public Position(String id, String name, String description, String departmentId) {
    this.id = id; this.name = name; this.description = description; this.departmentId = departmentId;
  }
  public String getId() { return id; }
  public void setId(String id) { this.id = id; }
  public String getName() { return name; }
  public void setName(String name) { this.name = name; }
  public String getDescription() { return description; }
  public void setDescription(String description) { this.description = description; }
  public String getDepartmentId() { return departmentId; }
  public void setDepartmentId(String departmentId) { this.departmentId = departmentId; }
}
