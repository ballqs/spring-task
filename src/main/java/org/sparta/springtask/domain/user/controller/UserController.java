package org.sparta.springtask.domain.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.sparta.springtask.common.dto.SuccessResponse;
import org.sparta.springtask.domain.user.dto.UserSignInRequestDto;
import org.sparta.springtask.domain.user.dto.UserSignUpRequestDto;
import org.sparta.springtask.domain.user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
@RestController
public class UserController {

    private final UserService userService;

    @PostMapping("/sign-up")
    public ResponseEntity<SuccessResponse<Void>> signUp(
            @Valid @RequestBody UserSignUpRequestDto userSignUpRequestDto
    ) {
        String accessToken = userService.saveUser(userSignUpRequestDto);
        return ResponseEntity.ok().header(AUTHORIZATION, accessToken).body(SuccessResponse.of(null));
    }

    @PostMapping("/sign-in")
    public ResponseEntity<SuccessResponse<Void>> signIn(
            @Valid @RequestBody UserSignInRequestDto userSignInRequestDto
    ) {
        String accessToken = userService.getUserWithEmailAndPassword(userSignInRequestDto);
        return ResponseEntity.ok().header(AUTHORIZATION, accessToken).body(SuccessResponse.of(null));
    }

}
