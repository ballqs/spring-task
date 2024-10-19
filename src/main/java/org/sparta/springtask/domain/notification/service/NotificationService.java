package org.sparta.springtask.domain.notification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sparta.springtask.common.code.ResponseCode;
import org.sparta.springtask.common.exception.ApiException;
import org.sparta.springtask.domain.notification.repository.NotificationRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService  {
    private static final Long DEFAULT_TIMEOUT = 60L * 1000 * 60;
    // SSE 연결 지속 시간 설정

    private final NotificationRepository notificationRepository;

    public SseEmitter subscribe(Long targetId) {
        SseEmitter sseEmitter=notificationRepository.save(targetId,new SseEmitter(DEFAULT_TIMEOUT)); //사용자 id와 emitter를 매핑

        // 사용자에게 모든 데이터가 전송되었다면 emitter 삭제
        sseEmitter.onCompletion(()-> notificationRepository.deleteById(targetId));
        // Emitter의 유효 시간이 만료되면 emitter 삭제
        sseEmitter.onTimeout(()-> notificationRepository.deleteById(targetId));

        //첫 구독 시에 이벤트를 발생시킨다
        //SEE 연결이 이루어진 후, 하나의 데이터로 전송되지 않는다면 SSE 유효시간이 만료되고 504에러 발생
        sendToClient(targetId, "메세지");

        return sseEmitter;
    }

    /**
     * 이벤트가 구독되어 있는 클라이언트에게 데이터를 전송
     */
    public void sendNotification(Long targetId, String content){
        sendToClient(targetId,content);
    }

    /**
     * 서버의 이벤트를 클라이언트에게 보내는 메서드
     * 다른 서비스 로직에서 이 메서드를 사용해 데이터를 Object event에 넣고 전송하면 된다
     */
    public void sendToClient(Long targetId, String content) {

        SseEmitter sseEmitter= notificationRepository.findById(targetId);
        log.info("sseEmitter: "+sseEmitter);

        if(sseEmitter!=null){
            try{
                sseEmitter.send(
                        SseEmitter.event()
                                .id(targetId.toString())
                                .name("notification")
                                .data(content)
                );
                log.info("[알림 전송 완료 : userId={}]",targetId);
                //슬랙 봇에 메시지 전송
                // slackBotService.sendSlackMessage(channelId,content);

            }catch(IOException ex){
                log.info("[알림 전송 실패 : userId={}]",targetId);
                notificationRepository.deleteById(targetId); //전송 실패 시 구독 취소
                throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, ResponseCode.CONNECTION_ERROR.getMessage());
            }
        }
//        else{
//            throw new NotFoundException(ResponseCode.NOT_FOUND_EMITTER);
//        }

    }
}
