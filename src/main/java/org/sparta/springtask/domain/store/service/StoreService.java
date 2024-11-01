package org.sparta.springtask.domain.store.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sparta.springtask.common.code.ResponseCode;
import org.sparta.springtask.common.exception.NotFoundException;
import org.sparta.springtask.domain.store.dto.StoreRequest;
import org.sparta.springtask.domain.store.entity.Store;
import org.sparta.springtask.domain.store.repository.StoreRepository;
import org.sparta.springtask.domain.user.entity.User;
import org.sparta.springtask.domain.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class StoreService {

    private final StoreRepository storeRepository;
    private final UserRepository userRepository;

    @Transactional
    public void createStore(Long userId , StoreRequest.Create create) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException(ResponseCode.NOT_FOUND_USER));

        Store store = Store.builder()
                .name(create.name())
                .openTime(create.openTime())
                .closeTime(create.closeTime())
                .tableCount(create.tableCount())
                .tel(create.tel())
                .zip(create.zip())
                .addr(create.addr())
                .addrDetail(create.addrDetail())
                .description(create.description())
                .isDelete(false)
                .user(user)
                .build();

        storeRepository.save(store);
    }
}
