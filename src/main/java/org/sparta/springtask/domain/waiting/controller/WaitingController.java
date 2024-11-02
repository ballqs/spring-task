package org.sparta.springtask.domain.waiting.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sparta.springtask.common.dto.SuccessResponse;
import org.sparta.springtask.common.entity.AuthUser;
import org.sparta.springtask.domain.user.enums.UserRole;
import org.sparta.springtask.domain.waiting.dto.WaitingRequest;
import org.sparta.springtask.domain.waiting.dto.WaitingResponse;
import org.sparta.springtask.domain.waiting.service.WaitingService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
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

    @Secured({UserRole.Authority.USER})
    @PatchMapping("/waiting/cancel")
    public ResponseEntity<SuccessResponse<Void>> cancelWaiting(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long storeId,
            @Valid @RequestBody WaitingRequest.Cancel cancel) {
        waitingService.cancelWaiting(authUser.getUserId() , storeId , cancel);
        return ResponseEntity.ok(SuccessResponse.of(null));
    }

    @Secured({UserRole.Authority.OWNER})
    @PatchMapping("/waiting/complete")
    public ResponseEntity<SuccessResponse<Void>> moveToReservation(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long storeId
    ) {
        waitingService.moveToReservation(authUser.getUserId() , storeId);
        return ResponseEntity.ok(SuccessResponse.of(null));
    }

    @Secured({UserRole.Authority.OWNER})
    @PatchMapping("/waiting/close")
    public ResponseEntity<SuccessResponse<Void>> closeWaitingQueue(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long storeId,
            @Valid @RequestBody WaitingRequest.Close close
    ) {
        waitingService.closeWaitingQueue(authUser.getUserId() , storeId , close);
        return ResponseEntity.ok(SuccessResponse.of(null));
    }

    @Secured({UserRole.Authority.OWNER})
    @GetMapping("/waiting/owner")
    public ResponseEntity<SuccessResponse<Page<WaitingResponse.List>>> getWaitingList(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long storeId,
            @ModelAttribute WaitingRequest.List list
    ) {
        return ResponseEntity.ok(SuccessResponse.of(waitingService.getWaitingList(authUser.getUserId() , storeId , list)));
    }

    @Secured({UserRole.Authority.USER})
    @GetMapping("/waiting/user")
    public ResponseEntity<SuccessResponse<WaitingResponse.Info>> getWaitingInfo(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long storeId,
            @RequestParam Long waitingId
    ) {
        return ResponseEntity.ok(SuccessResponse.of(waitingService.getWaitingInfo(authUser.getUserId() , storeId , waitingId)));
    }

    @Secured({UserRole.Authority.USER})
    @PostMapping("/waiting/delay")
    public ResponseEntity<SuccessResponse<WaitingResponse.Info>> delayWaitingNumber(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long storeId,
            @RequestBody WaitingRequest.Delay delay
    ) {
        waitingService.delayWaitingNumber(authUser.getUserId() , storeId , delay);
        return ResponseEntity.ok(SuccessResponse.of(null));
    }
}
