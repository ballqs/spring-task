package org.sparta.springtask.domain.waiting.repository;

import org.sparta.springtask.domain.waiting.entity.Waiting;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WaitingRepository extends JpaRepository<Waiting, Long> {
}
