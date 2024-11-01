package org.sparta.springtask.domain.waiting.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sparta.springtask.domain.waiting.repository.WaitingRepository;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class WaitingService {

    private final WaitingRepository waitingRepository;

}
