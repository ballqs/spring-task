package org.sparta.springtask.domain.waiting.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sparta.springtask.common.dto.SuccessResponse;
import org.sparta.springtask.common.entity.AuthUser;
import org.sparta.springtask.domain.user.enums.UserRole;
import org.sparta.springtask.domain.waiting.dto.WaitingRequest;
import org.sparta.springtask.domain.waiting.service.WaitingService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/store/{storeId}")
public class WaitingController {

    private final WaitingService waitingService;

    @Secured({UserRole.Authority.USER})
    @PostMapping("/waiting")
    public ResponseEntity<SuccessResponse<Void>> waitingCreate(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long storeId,
            @Valid @RequestBody WaitingRequest.Create create) {
        waitingService.waitingCreate(authUser.getUserId() , storeId , create);
        return ResponseEntity.ok(SuccessResponse.of(null));
    }
}
