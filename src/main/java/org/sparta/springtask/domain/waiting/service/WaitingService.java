package org.sparta.springtask.domain.waiting.service;

import org.sparta.springtask.domain.waiting.dto.WaitingRequest;
import org.sparta.springtask.domain.waiting.dto.WaitingResponse;
import org.springframework.data.domain.Page;

public interface WaitingService {
    void waitingCreate(Long userId , Long storeId , WaitingRequest.Create create);
    void moveToReservation(Long userId , Long storeId);
    void closeWaitingQueue(Long userId , Long storeId , WaitingRequest.Close close);
    void cancelWaiting(Long userId , Long storeId , WaitingRequest.Cancel cancel);
    Page<WaitingResponse.List> getWaitingList(Long userId , Long storeId , WaitingRequest.List list);
    WaitingResponse.Info getWaitingInfo(Long userId , Long storeId , Long waitingId);
    void delayWaitingNumber(Long userId , Long storeId , WaitingRequest.Delay delay);

}
