package org.sparta.springtask.domain.user.repository;

import org.sparta.springtask.common.exception.InvalidParameterException;
import org.sparta.springtask.common.exception.NotFoundException;
import org.sparta.springtask.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

import static org.sparta.springtask.common.code.ResponseCode.NOT_FOUND_USER;
import static org.sparta.springtask.common.code.ResponseCode.WRONG_EMAIL_OR_PASSWORD;

public interface UserRepository extends JpaRepository<User, Long>, UserQueryRepository {

    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);

    default User findByUserId(long id) {
        return findById(id)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_USER));
    }

    default User findByUserEmail(String email) {
        return findByEmail(email)
                .orElseThrow(() -> new InvalidParameterException(WRONG_EMAIL_OR_PASSWORD));
    }
}
