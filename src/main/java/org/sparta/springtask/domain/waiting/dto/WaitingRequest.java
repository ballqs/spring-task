package org.sparta.springtask.domain.waiting.dto;

public sealed interface WaitingRequest permits WaitingRequest.Create {
    record Create(
            Long peopleNumber
    ) implements WaitingRequest{}
}
