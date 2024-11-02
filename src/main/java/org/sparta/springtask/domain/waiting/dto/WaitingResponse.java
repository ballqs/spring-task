package org.sparta.springtask.domain.waiting.dto;

public sealed interface WaitingResponse permits WaitingResponse.List , WaitingResponse.Info {
    record List(
            Long waitNumber,
            Long peopleNumber
    ) implements WaitingResponse {}

    record Info (
            Long waitNumber,
            Long peopleNumber
    ) implements WaitingResponse {}
}
