package org.sparta.springtask.domain.waiting.repository;

import org.sparta.springtask.domain.waiting.entity.Waiting;
import org.sparta.springtask.domain.waiting.enums.WaitingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;

public interface WaitingRepository extends JpaRepository<Waiting, Long> {
    @Query("SELECT IFNULL(MAX(w.waitNumber) + 1, 1) FROM Waiting w WHERE w.store.id = :storeId AND DATE_FORMAT(w.waitingTime, '%Y-%m-%d') = :date")
    long maxWaitNumber(@Param("storeId") Long storeId , @Param("date") LocalDate date);

    @Query("SELECT w FROM Waiting w WHERE w.store.id = :storeId AND DATE_FORMAT(w.waitingTime, '%Y-%m-%d') = :date AND w.status = :status ORDER BY w.waitNumber ASC LIMIT 1")
    Waiting handleWaitingCustomer(@Param("storeId") Long storeId , @Param("date") LocalDate date , @Param("status") WaitingStatus status);

    @Modifying
    @Query("UPDATE Waiting w SET w.status = :status WHERE w.waitNumber >= :waitNumber AND w.store.id = :storeId AND DATE_FORMAT(w.waitingTime, '%Y-%m-%d') = :date")
    void closeWaiting(@Param("status") WaitingStatus status , @Param("waitNumber") Long waitNumber , @Param("storeId") Long storeId , @Param("date") LocalDate date);

    @Query("SELECT w FROM Waiting w WHERE w.user.id = :userId AND w.store.id = :storeId AND w.waitNumber = :waitNumber")
    Waiting findWaitingByStoreIdAndWaitNumber(Long userId , Long storeId , Long waitNumber);

    @Query("SELECT w FROM Waiting w INNER JOIN FETCH w.store s WHERE s.user.id = :userId AND w.store.id = :storeId AND w.status = :status AND DATE_FORMAT(w.waitingTime, '%Y-%m-%d') = :date")
    Page<Waiting> findWaitingByList(Long userId, Long storeId, WaitingStatus status, LocalDate date, Pageable pageable);

    Optional<Waiting> findWaitingByIdAndUserIdAndStoreId(Long waitingId , Long userId , Long storeId);
}
