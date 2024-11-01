package org.sparta.springtask.domain.waiting.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sparta.springtask.common.code.ResponseCode;
import org.sparta.springtask.common.exception.NotFoundException;
import org.sparta.springtask.domain.store.entity.Store;
import org.sparta.springtask.domain.store.repository.StoreRepository;
import org.sparta.springtask.domain.user.entity.User;
import org.sparta.springtask.domain.user.repository.UserRepository;
import org.sparta.springtask.domain.waiting.dto.WaitingRequest;
import org.sparta.springtask.domain.waiting.entity.Waiting;
import org.sparta.springtask.domain.waiting.enums.WaitingStatus;
import org.sparta.springtask.domain.waiting.repository.WaitingRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Slf4j
@RequiredArgsConstructor
@Service
public class WaitingService {

    private final WaitingRepository waitingRepository;
    private final UserRepository userRepository;
    private final StoreRepository storeRepository;

    public void waitingCreate(Long userId , Long storeId , WaitingRequest.Create create) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException(ResponseCode.NOT_FOUND_USER));
        Store store = storeRepository.findById(storeId).orElseThrow(() -> new NotFoundException(ResponseCode.NOT_FOUND_STORE));

        LocalDate today = LocalDate.now();
        long maxWaitNumber = waitingRepository.maxWaitNumber(today);

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
}
