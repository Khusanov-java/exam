package org.example.exam.repository;

import org.example.exam.entity.Status;
import org.example.exam.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StatusRepository extends JpaRepository<Status, Integer> {
    List<Status> findAllByActiveTrueOrderByPositionNumber();
}