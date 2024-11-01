package org.sparta.springtask.domain.waiting.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sparta.springtask.common.entity.AuthUser;
import org.sparta.springtask.domain.waiting.service.WaitingService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/waiting")
public class WaitingController {

    private final WaitingService waitingService;

    @GetMapping
    public void test(
            @AuthenticationPrincipal AuthUser authUser) {
        // 대기열을 어떻게 걸것인가?
    }
}
