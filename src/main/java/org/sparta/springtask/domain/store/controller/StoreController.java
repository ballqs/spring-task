package org.sparta.springtask.domain.store.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sparta.springtask.common.dto.SuccessResponse;
import org.sparta.springtask.common.entity.AuthUser;
import org.sparta.springtask.domain.store.dto.StoreRequest;
import org.sparta.springtask.domain.store.service.StoreService;
import org.sparta.springtask.domain.user.enums.UserRole;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/store")
@RestController
public class StoreController {

    private final StoreService storeService;


    @Secured({UserRole.Authority.OWNER})
    @PostMapping
    public ResponseEntity<SuccessResponse<Void>> createStore(
            @AuthenticationPrincipal AuthUser authUser,
            @Valid @RequestBody StoreRequest.Create create) {
        storeService.createStore(authUser.getUserId() , create);
        return ResponseEntity.ok(SuccessResponse.of(null));
    }
}
