package org.sparta.springtask.domain.waiting.dto;

import java.util.Objects;

public sealed interface WaitingRequest permits WaitingRequest.Create , WaitingRequest.Close , WaitingRequest.Cancel , WaitingRequest.List , WaitingRequest.Delay {
    record Create(
            Long peopleNumber
    ) implements WaitingRequest{}

    record Close(
            Long waitNumber
    ) implements WaitingRequest{}

    record Cancel(
            Long waitNumber
    ) implements WaitingRequest{}

    record List(
            Integer page,
            Integer size
    ) implements WaitingRequest {
        public List {
            if (Objects.isNull(page)) page = 1;
            if (Objects.isNull(size)) size = 10;
        }
    }

    record Delay(
            Long waitingId
    ) implements WaitingRequest {}
}
