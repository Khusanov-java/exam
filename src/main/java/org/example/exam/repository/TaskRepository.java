package org.example.exam.repository;

import org.example.exam.entity.Task;
import org.example.exam.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Integer> {
}