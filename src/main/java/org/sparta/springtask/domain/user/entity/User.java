package org.sparta.springtask.domain.user.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sparta.springtask.domain.user.dto.UserSignUpRequestDto;
import org.sparta.springtask.domain.user.enums.UserRole;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(unique = true)
    private String email;

    private String password;

    private String name;

    private boolean isDeleted = false;

    @Enumerated(EnumType.STRING)
    private UserRole authority;

    private User(String email, String password, String name, UserRole authority) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.authority = authority;
    }

    public static User from(String encryptPassword, UserSignUpRequestDto userSignUpRequestDto) {
        return new User(
                userSignUpRequestDto.getEmail(),
                encryptPassword,
                userSignUpRequestDto.getName(),
                UserRole.of(userSignUpRequestDto.getAuthority())
        );
    }
}
