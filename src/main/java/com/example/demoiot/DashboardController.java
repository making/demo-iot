package com.example.demoiot;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.reactive.result.view.Rendering;

@Controller
public class DashboardController {
    private final Devices devices;

    public DashboardController(Devices devices) {
        this.devices = devices;
    }

    @GetMapping("dashboard")
    public Rendering dashboard() {
        return Rendering
                .view("dashboard")
                .modelAttribute("devices", this.devices.data())
                .build();
    }
}
