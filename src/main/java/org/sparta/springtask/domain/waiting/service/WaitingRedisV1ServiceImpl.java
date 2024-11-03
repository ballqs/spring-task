package org.sparta.springtask.domain.waiting.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RMap;
import org.redisson.api.RQueue;
import org.redisson.api.RedissonClient;
import org.sparta.springtask.common.annotation.RedissonLock;
import org.sparta.springtask.common.exception.ApiException;
import org.sparta.springtask.domain.waiting.dto.WaitingRequest;
import org.sparta.springtask.domain.waiting.dto.WaitingResponse;
import org.sparta.springtask.domain.waiting.entity.Waiting;
import org.sparta.springtask.domain.waiting.enums.WaitingStatus;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Primary
@RequiredArgsConstructor
@Service
public class WaitingRedisV1ServiceImpl implements WaitingService {
    // RAtomicLong + RQueue + RMap 사용 방법!!

    private static final String KEY_PREFIX_WAITING = "waiting:";
    private static final String KEY_PREFIX_STORE = "store:";
    private static final String KEY_NUMBER = "number";
    private static final String KEY_QUEUE = "queue";
    private static final String KEY_MAP = "map";

    private final RedissonClient redissonClient;

    @Override
    @RedissonLock("#storeId")
    public void waitingCreate(Long userId, Long storeId, WaitingRequest.Create create) {
        // 키 이름 구성: store:1:waiting
        String redisKey = getRedisKey(storeId);

        // RAtomicLong
        RAtomicLong waitingNumber = redissonClient.getAtomicLong(redisKey + KEY_NUMBER); // 대기 번호용 AtomicLong
        long maxWaitNumber = waitingNumber.incrementAndGet(); // 현재 값에 1을 더하고 반환

        // RQueue
        RQueue<Object> waitingQueue = redissonClient.getQueue(redisKey + KEY_QUEUE); // 대기열

        // RMap
        RMap<Long, Waiting> waitingMap = redissonClient.getMap(redisKey + KEY_MAP); // 대기 정보 저장용 멀티맵

        // 대기 항목 생성(Dto 만들어서 처리하는게 더 좋아보임)
        // ※ userId를 미사용중이라 여기에다 사용!
        Waiting waiting = Waiting.builder()
                .waitNumber(maxWaitNumber)
                .waitingTime(LocalDateTime.now())
                .peopleNumber(create.peopleNumber())
                .status(WaitingStatus.WAITING)
                .build();

        // 대기 정보를 RMap에 추가
        waitingMap.put(maxWaitNumber, waiting);

        // 대기열에 대기 항목 추가
        waitingQueue.add(maxWaitNumber);
    }

    private String getRedisKey(Long storeId) {
        return KEY_PREFIX_STORE + storeId + ":" + KEY_PREFIX_WAITING;
    }

    @Override
    public void moveToReservation(Long userId, Long storeId) {
        // 키 이름 구성: store:1:waiting
        String redisKey = getRedisKey(storeId);

        // RQueue
        RQueue<Object> waitingQueue = redissonClient.getQueue(redisKey + KEY_QUEUE); // 대기열

        // RMap
        RMap<Long, Waiting> waitingMap = redissonClient.getMap(redisKey + KEY_MAP); // 대기 정보 저장용 맵

        Long waitNumber = (Long) waitingQueue.poll();
        log.info("waitingNumber : {}" , waitNumber);
        if (waitNumber == null) {
            log.warn("대기열이 비어 있습니다. 대기 번호를 가져올 수 없습니다.");
            throw new ApiException(HttpStatus.BAD_REQUEST , "대기열이 비어 있음!");
        }

        Waiting waiting = waitingMap.get(waitNumber);
        if (Objects.isNull(waiting)) {
            log.warn("대기 번호 {}에 해당하는 정보가 없습니다.", waitNumber);
            throw new ApiException(HttpStatus.BAD_REQUEST , "대기 정보가 없음!");
        }

        waitingMap.remove(waitNumber, waiting);
        log.info("waiting : {}" , waiting.toString());

        // 예약 테이블 이관 작업!
        // ...
    }

    @Override
    public void closeWaitingQueue(Long userId, Long storeId, WaitingRequest.Close close) {

    }

    @Override
    public void cancelWaiting(Long userId, Long storeId, WaitingRequest.Cancel cancel) {

    }

    @Override
    public Page<WaitingResponse.List> getWaitingList(Long userId, Long storeId, WaitingRequest.List list) {
        // 키 이름 구성
        String redisKey = getRedisKey(storeId);

        // 대기열에서 대기 항목 가져오기
        RQueue<Object> waitingQueue = redissonClient.getQueue(redisKey + KEY_QUEUE);
        RMap<Long, Waiting> waitingMap = redissonClient.getMap(redisKey + KEY_MAP);

        // 대기 항목을 페이지 형식으로 변환
        int pageNumber = list.page() - 1;   // 페이지 번호
        int pageSize = list.size();         // 페이지 크기

        List<Object> waitNumbers = waitingQueue.stream()
                .skip((long) pageNumber * pageSize)
                .limit(pageSize)
                .toList();

        // Set으로 변환하여 getAll 사용
        Set<Long> waitNumberSet = new HashSet<>();
        for (Object waitNumber : waitNumbers) {
            waitNumberSet.add((Long) waitNumber);
        }

        // 대기 정보를 한 번에 가져오기
        Map<Long, Waiting> waitingData = waitingMap.getAll(waitNumberSet);

        // 대기 응답 리스트 생성
        List<WaitingResponse.List> waitingResponses = waitingData.values().stream()
                .map(waiting -> new WaitingResponse.List(waiting.getWaitNumber(), waiting.getPeopleNumber()))
                .collect(Collectors.toList());

        return new PageImpl<>(waitingResponses, PageRequest.of(pageNumber, pageSize), waitingQueue.size());
    }

    @Override
    public WaitingResponse.Info getWaitingInfo(Long userId, Long storeId, Long waitingId) {
        // 키 이름 구성
        String redisKey = getRedisKey(storeId);

        // 대기 정보 맵에서 대기 정보 가져오기
        RMap<Long, Waiting> waitingMap = redissonClient.getMap(redisKey + KEY_MAP);
        // 대기 번호에 해당하는 대기 정보를 찾기
        Waiting waiting = waitingMap.get(waitingId);

        if (waiting == null) {
            // 대기 정보가 없을 경우 처리 (예: 예외를 던지거나 null 반환)
            return null; // 또는 적절한 예외 처리
        }

        return new WaitingResponse.Info(waiting.getWaitNumber(), waiting.getPeopleNumber());
    }

    @Override
    public void delayWaitingNumber(Long userId, Long storeId, WaitingRequest.Delay delay) {

    }
}
