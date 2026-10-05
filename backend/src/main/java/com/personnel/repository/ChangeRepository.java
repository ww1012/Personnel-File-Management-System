package com.personnel.repository;

import com.personnel.model.Change;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChangeRepository extends JpaRepository<Change, String> {
}
