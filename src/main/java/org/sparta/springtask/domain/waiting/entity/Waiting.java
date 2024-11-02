package org.sparta.springtask.domain.waiting.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sparta.springtask.common.entity.Timestamped;
import org.sparta.springtask.domain.store.entity.Store;
import org.sparta.springtask.domain.user.entity.User;
import org.sparta.springtask.domain.waiting.enums.WaitingStatus;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "waiting")
public class Waiting extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long waitNumber;

    @Column(nullable = false)
    private LocalDateTime waitingTime;

    @Column(nullable = false)
    private Long peopleNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WaitingStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id" , nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id" , nullable = false)
    private Store store;

    @Builder
    public Waiting(Long waitNumber , LocalDateTime waitingTime , Long peopleNumber , WaitingStatus status , User user , Store store) {
        this.waitNumber = waitNumber;
        this.waitingTime = waitingTime;
        this.peopleNumber = peopleNumber;
        this.status = status;
        this.user = user;
        this.store = store;
    }

    public void updateWaitingStatus(WaitingStatus status) {
        this.status = status;
    }
}
