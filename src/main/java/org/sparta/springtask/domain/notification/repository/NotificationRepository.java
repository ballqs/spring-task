package org.sparta.springtask.domain.notification.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class NotificationRepository {

    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

    public SseEmitter save(Long userId, SseEmitter sseEmitter) { // emitter를 저장
        emitters.put(userId, sseEmitter);
        return sseEmitter;
    }

    public void deleteById(Long userId) { // emitter를 지움
        emitters.remove(userId);
    }

    public SseEmitter findById(Long userId) {
        return emitters.get(userId);
    }
}
