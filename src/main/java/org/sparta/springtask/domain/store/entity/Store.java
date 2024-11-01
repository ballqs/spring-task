package org.sparta.springtask.domain.store.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sparta.springtask.common.entity.Timestamped;
import org.sparta.springtask.domain.user.entity.User;

import java.time.LocalTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "store")
public class Store extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private LocalTime openTime;
    private LocalTime closeTime;

    private Long tableCount;

    private String tel;

    private String zip;
    private String addr;
    private String addrDetail;

    private boolean isDelete;

    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id" , nullable = false)
    private User user;
}
