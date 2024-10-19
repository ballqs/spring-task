package org.sparta.springtask.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.sparta.springtask.common.config.JwtUtil;
import org.sparta.springtask.common.exception.DuplicateException;
import org.sparta.springtask.common.exception.InvalidParameterException;
import org.sparta.springtask.domain.user.dto.UserSignInRequestDto;
import org.sparta.springtask.domain.user.dto.UserSignUpRequestDto;
import org.sparta.springtask.domain.user.entity.User;
import org.sparta.springtask.domain.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static org.sparta.springtask.common.code.ResponseCode.*;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public String saveUser(UserSignUpRequestDto userSignUpRequestDto) {
        boolean isExistUser = userRepository.existsByEmail(userSignUpRequestDto.getEmail());

        if (isExistUser) {
            throw new DuplicateException(DUPLICATE_EMAIL);
        }

        String encryptPassword = passwordEncoder.encode(userSignUpRequestDto.getPassword());
        User user = User.from(encryptPassword, userSignUpRequestDto);

        User savedUser = userRepository.save(user);

        return jwtUtil.createToken(savedUser.getId(), savedUser.getEmail(), savedUser.getAuthority());
    }

    public String getUserWithEmailAndPassword(UserSignInRequestDto userSignInRequestDto) {
        User user = userRepository.findByUserEmail(userSignInRequestDto.getEmail());

        if (!passwordEncoder.matches(userSignInRequestDto.getPassword(), user.getPassword())) {
            throw new InvalidParameterException(WRONG_EMAIL_OR_PASSWORD);
        }

        return jwtUtil.createToken(user.getId(), user.getEmail(), user.getAuthority());
    }
}
