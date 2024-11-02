package org.sparta.springtask.domain.waiting.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sparta.springtask.common.code.ResponseCode;
import org.sparta.springtask.common.exception.ForbiddenException;
import org.sparta.springtask.common.exception.NotFoundException;
import org.sparta.springtask.domain.store.entity.Store;
import org.sparta.springtask.domain.store.repository.StoreRepository;
import org.sparta.springtask.domain.user.entity.User;
import org.sparta.springtask.domain.user.repository.UserRepository;
import org.sparta.springtask.domain.waiting.dto.WaitingRequest;
import org.sparta.springtask.domain.waiting.dto.WaitingResponse;
import org.sparta.springtask.domain.waiting.entity.Waiting;
import org.sparta.springtask.domain.waiting.enums.WaitingStatus;
import org.sparta.springtask.domain.waiting.repository.WaitingRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Slf4j
@RequiredArgsConstructor
@Service
public class WaitingServiceImpl implements WaitingService {

    private final WaitingRepository waitingRepository;
    private final UserRepository userRepository;
    private final StoreRepository storeRepository;

    @Override
    @Transactional
    public synchronized void waitingCreate(Long userId , Long storeId , WaitingRequest.Create create) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException(ResponseCode.NOT_FOUND_USER));
        Store store = storeRepository.findById(storeId).orElseThrow(() -> new NotFoundException(ResponseCode.NOT_FOUND_STORE));

        LocalDate today = LocalDate.now();
        long maxWaitNumber = waitingRepository.maxWaitNumber(storeId , today);

        Waiting waiting = Waiting.builder()
                .waitNumber(maxWaitNumber)
                .waitingTime(LocalDateTime.now())
                .peopleNumber(create.peopleNumber())
                .status(WaitingStatus.WAITING)
                .user(user)
                .store(store)
                .build();

        waitingRepository.save(waiting);
    }

    // wait 1순위 처리(예약테이블로 이동?)
    @Override
    @Transactional
    public void moveToReservation(Long userId , Long storeId) {
        Store store = storeRepository.findById(storeId).orElseThrow(() -> new NotFoundException(ResponseCode.NOT_FOUND_STORE));

        if (userId != store.getUser().getId()) {
            throw new ForbiddenException(ResponseCode.FORBIDDEN);
        }

        LocalDate today = LocalDate.now();
        Waiting waiting = waitingRepository.handleWaitingCustomer(storeId , today , WaitingStatus.WAITING);

        waiting.updateWaitingStatus(WaitingStatus.COMPLETED);

        // 예약 테이블 이관 작업!
        // ...
    }

    // wait 어느 순번부터는 대기열 마감
    @Override
    @Transactional
    public void closeWaitingQueue(Long userId , Long storeId , WaitingRequest.Close close) {
        Store store = storeRepository.findById(storeId).orElseThrow(() -> new NotFoundException(ResponseCode.NOT_FOUND_STORE));

        if (userId != store.getUser().getId()) {
            throw new ForbiddenException(ResponseCode.FORBIDDEN);
        }

        LocalDate today = LocalDate.now();
        waitingRepository.closeWaiting(WaitingStatus.CANCELED , close.waitNumber() , storeId , today);
    }

    // 웨이팅 취소(번호를 반환하는건 아님)
    @Override
    @Transactional
    public void cancelWaiting(Long userId , Long storeId , WaitingRequest.Cancel cancel) {
        Waiting waiting = waitingRepository.findWaitingByStoreIdAndWaitNumber(userId , storeId , cancel.waitNumber());
        waiting.updateWaitingStatus(WaitingStatus.CANCELED);
    }

    // 대기열 조회 기능(OWNER)
    @Override
    public Page<WaitingResponse.List> getWaitingList(Long userId , Long storeId , WaitingRequest.List list) {
        Pageable pageable = PageRequest.of(list.page() - 1, list.size());
        LocalDate today = LocalDate.now();
        Page<Waiting> waiting =  waitingRepository.findWaitingByList(userId , storeId , WaitingStatus.WAITING , today , pageable);
        return waiting.map(it -> new WaitingResponse.List(it.getWaitNumber() , it.getPeopleNumber()));
    }

    // 대기열 조회 기능(USER)
    @Override
    public WaitingResponse.Info getWaitingInfo(Long userId , Long storeId , Long waitingId) {
        Waiting waiting = waitingRepository.findWaitingByIdAndUserIdAndStoreId(waitingId , userId , storeId).orElseThrow(() -> new NotFoundException(ResponseCode.NOT_FOUND_WAITING));
        return new WaitingResponse.Info(waiting.getWaitNumber() , waiting.getPeopleNumber());
    }

    // 대기 미루기 기능(맨 뒤로 미루는 기능)
    @Override
    @Transactional
    public void delayWaitingNumber(Long userId , Long storeId , WaitingRequest.Delay delay) {
        Waiting waiting = waitingRepository.findWaitingByIdAndUserIdAndStoreId(delay.waitingId() , userId , storeId).orElseThrow(() -> new NotFoundException(ResponseCode.NOT_FOUND_WAITING));
        waiting.updateWaitingStatus(WaitingStatus.CANCELED);

        waitingCreate(userId , storeId , new WaitingRequest.Create(waiting.getPeopleNumber()));
    }
}
