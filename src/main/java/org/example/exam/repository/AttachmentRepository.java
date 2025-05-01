package org.example.exam.repository;

import org.example.exam.entity.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttachmentRepository extends JpaRepository<Attachment, Integer> {
    Attachment findByTaskId(int taskId);
}