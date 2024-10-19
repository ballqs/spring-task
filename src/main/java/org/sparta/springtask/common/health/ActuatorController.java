package org.sparta.springtask.common.health;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/actuator")
public class ActuatorController {
    @GetMapping("/prometheus")
    public String prometheus() {
        return "UP";
    }
}
