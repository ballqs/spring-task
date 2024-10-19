package org.sparta.springtask.domain.notification.event.handler;

import lombok.RequiredArgsConstructor;
import org.sparta.springtask.domain.notification.event.UserCreateEvent;
import org.sparta.springtask.domain.notification.service.SlackBotService;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SlackNotificationEventListener {

    private final SlackBotService slackBotService;
    private final String channelId="C07SERV9N4E";

    @EventListener
    public void handleSlackSavedCommentEvent(UserCreateEvent event){
        //슬랙 봇에 메시지 전송
        String message = "새로운 유저("+ event.getUserId() + "," + event.getName() +")가 등록되었습니다.";
        slackBotService.sendSlackMessage(channelId, message);
    }

}
