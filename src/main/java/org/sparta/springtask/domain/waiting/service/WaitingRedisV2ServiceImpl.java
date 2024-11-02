package org.sparta.springtask.domain.waiting.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RQueue;
import org.redisson.api.RSetMultimap;
import org.redisson.api.RedissonClient;
import org.sparta.springtask.domain.waiting.dto.WaitingRequest;
import org.sparta.springtask.domain.waiting.dto.WaitingResponse;
import org.sparta.springtask.domain.waiting.entity.Waiting;
import org.sparta.springtask.domain.waiting.enums.WaitingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class WaitingRedisV2ServiceImpl implements WaitingService {

    private static final String KEY_PREFIX_WAITING = "waiting:";
    private static final String KEY_PREFIX_STORE = "store:";

    private final RedissonClient redissonClient;

    @Override
//    @RedissonLock("#userId")
    public void waitingCreate(Long userId, Long storeId, WaitingRequest.Create create) {
        // 키 이름 구성: store:1:waiting
        String redisKey = getRedisKey(storeId);

        // RAtomicLong
        RAtomicLong waitingNumber = redissonClient.getAtomicLong(redisKey + "number"); // 대기 번호용 AtomicLong
        long maxWaitNumber = waitingNumber.incrementAndGet(); // 현재 값에 1을 더하고 반환

        // RQueue
        RQueue<Waiting> waitingQueue = redissonClient.getQueue(redisKey + "queue"); // 대기열

        // RSetMultimap
        RSetMultimap<Long, Waiting> waitingMap = redissonClient.getSetMultimap(redisKey + "map"); // 대기 정보 저장용 멀티맵

        // 대기 항목 생성
        Waiting waiting = Waiting.builder()
                .waitNumber(maxWaitNumber)
                .waitingTime(LocalDateTime.now())
                .peopleNumber(create.peopleNumber())
                .status(WaitingStatus.WAITING)
                .build();

        // 대기 정보를 RSetMultimap에 추가
        waitingMap.put(maxWaitNumber, waiting);

        // 대기열에 대기 항목 추가
        waitingQueue.add(waiting);
    }

    public String getRedisKey(Long storeId) {
        return KEY_PREFIX_STORE + storeId + ":" + KEY_PREFIX_WAITING;
    }

    @Override
    public void moveToReservation(Long userId, Long storeId) {

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
        RQueue<Waiting> waitingQueue = redissonClient.getQueue(redisKey + "queue");

        // 대기 항목을 페이지 형식으로 변환
        int pageNumber = list.page() - 1;   // 페이지 번호
        int pageSize = list.size();         // 페이지 크기

        List<WaitingResponse.List> waitingResponses = waitingQueue.stream()
                .skip(pageNumber * pageSize)
                .limit(pageSize)
                .map(waiting -> new WaitingResponse.List(waiting.getWaitNumber(), waiting.getPeopleNumber()))
                .collect(Collectors.toList());

        return new PageImpl<>(waitingResponses, PageRequest.of(pageNumber, pageSize), waitingQueue.size());
    }

    @Override
    public WaitingResponse.Info getWaitingInfo(Long userId, Long storeId, Long waitingId) {
        // 키 이름 구성
        String redisKey = getRedisKey(storeId);

        // 대기 정보 멀티맵에서 대기 정보 가져오기
        RSetMultimap<Long, Waiting> waitingMap = redissonClient.getSetMultimap(redisKey + "map");

        // 대기 번호에 해당하는 대기 정보를 찾기
        Waiting waiting = waitingMap.get(waitingId).stream().findFirst().orElse(null);

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