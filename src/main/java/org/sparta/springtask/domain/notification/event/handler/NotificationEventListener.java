package org.sparta.springtask.domain.notification.event.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.sparta.springtask.domain.notification.event.UserCreateEvent;
import org.sparta.springtask.domain.notification.service.NotificationService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationEventListener {

    private final NotificationService notificationService;

    /**
     * 댓글 저장 알림 전송 이벤트
     * @param event
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleSavedCommentEvent(@NotNull UserCreateEvent event){
        //알림 서비스 로직 호출(@Transactional이 걸려 있으면 여기도 같이 탐)
        notificationService.sendNotification(event.getUserId() , "메세지");
    }
}
