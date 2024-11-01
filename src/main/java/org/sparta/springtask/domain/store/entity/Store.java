package org.sparta.springtask.domain.store.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
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

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private LocalTime openTime;

    @Column(nullable = false)
    private LocalTime closeTime;

    @Column(nullable = false)
    private Long tableCount;

    @Column(nullable = false)
    private String tel;

    @Column(nullable = false)
    private String zip;

    @Column(nullable = false)
    private String addr;

    @Column(nullable = false)
    private String addrDetail;

    @Column(nullable = false)
    private boolean isDelete;

    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id" , nullable = false)
    private User user;

    @Builder
    public Store(String name , LocalTime openTime , LocalTime closeTime , Long tableCount , String tel , String zip , String addr , String addrDetail , String description , boolean isDelete , User user) {
        this.name = name;
        this.openTime = openTime;
        this.closeTime = closeTime;
        this.tableCount = tableCount;
        this.tel = tel;
        this.zip = zip;
        this.addr = addr;
        this.addrDetail = addrDetail;
        this.description = description;
        this.isDelete = isDelete;
        this.user = user;
    }
}
