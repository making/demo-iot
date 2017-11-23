package com.example.demoiot;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ConcurrentMap;

@RestController
public class DeviceEmulatorController {
    private final RandomDevice randomDevice;
    private final Devices devices;

    public DeviceEmulatorController(RandomDevice randomDevice, Devices devices) {
        this.randomDevice = randomDevice;
        this.devices = devices;
    }


    @GetMapping("heartbeat")
    public Flux<Device> heartbeat() {
        Mono<Device> device = this.randomDevice.takeId()
                .map(id -> new Device(id, 0.0, Instant.now()))
                .cache();
        Flux<Device> heartbeat = device.delayElement(Duration.ofSeconds(1)).repeat();
        return device.concatWith(heartbeat)
                .doOnCancel(() -> device
                        .map(Device::getId)
                        .map(this.devices::remove)
                        .flatMap(this.randomDevice::putId)
                        .subscribe());
    }

    @PostMapping("device")
    public Mono<Void> postDevices(@RequestBody Mono<Device> device) {
        return device
                .doOnNext(this.devices::update)
                .then();
    }

    @GetMapping(path = {"device", "/"}, produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<?> redirect() {
        return ResponseEntity
                .status(HttpStatus.SEE_OTHER)
                .header(HttpHeaders.LOCATION, "/device.html")
                .build();
    }


    @GetMapping(path = "device", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ConcurrentMap<Integer, Device>> getDevices() {
        return this.devices.stream()
                .buffer(Duration.ofMillis(500))
                .map(x -> x.get(x.size() - 1));
    }

    @PostMapping("clear")
    public void clear() {
        this.devices.clear();
    }
}
