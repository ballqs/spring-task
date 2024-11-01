package org.sparta.springtask.domain.waiting.repository;

import org.sparta.springtask.domain.waiting.entity.Waiting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;

public interface WaitingRepository extends JpaRepository<Waiting, Long> {
    @Query("SELECT IFNULL(MAX(w.waitNumber) + 1, 1) FROM Waiting w WHERE DATE_FORMAT(w.waitingTime, '%Y-%m-%d') = :date")
    long maxWaitNumber(@Param("date") LocalDate date);
}
