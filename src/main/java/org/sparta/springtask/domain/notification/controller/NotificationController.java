package org.sparta.springtask.domain.notification.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sparta.springtask.common.entity.AuthUser;
import org.sparta.springtask.domain.notification.service.NotificationService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/notify")
public class NotificationController {

    private final NotificationService notificationService;

    /**
     * 이벤트를 받는 용도
     */
    @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<SseEmitter> subscribe(@AuthenticationPrincipal AuthUser authUser) {
        if (authUser == null) {
            throw new AuthenticationCredentialsNotFoundException("User is not authenticated.");
        }
        SseEmitter sseEmitter=notificationService.subscribe(authUser.getUserId());
        return ResponseEntity.ok().contentType(MediaType.TEXT_EVENT_STREAM).body(sseEmitter);
    }

    /**
     * 이벤트를 구독 중인 클라이언트들에게 데이터를 전송한다
     */
    @PostMapping("/sendData")
    public void sendData(@AuthenticationPrincipal AuthUser authUser,
                         @RequestParam String content){
        notificationService.sendNotification(authUser.getUserId(),content);
    }
}
