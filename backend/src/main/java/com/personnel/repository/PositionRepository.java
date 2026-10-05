package com.personnel.repository;

import com.personnel.model.Position;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PositionRepository extends JpaRepository<Position, String> {
  List<Position> findByDepartmentId(String departmentId);
}
