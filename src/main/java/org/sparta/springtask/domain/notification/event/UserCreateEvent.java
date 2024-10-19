package org.sparta.springtask.domain.notification.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class UserCreateEvent {
    private Long userId;
    private String name;
}
