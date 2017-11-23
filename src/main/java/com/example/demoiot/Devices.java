package com.example.demoiot;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.UnicastProcessor;

import java.time.Instant;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
public class Devices {
    private final ConcurrentMap<Integer, Device> devices = new ConcurrentHashMap<>();
    private final UnicastProcessor<ConcurrentMap<Integer, Device>> hotSource;
    private final Flux<ConcurrentMap<Integer, Device>> deviceStream;
    private final RandomDevice randomDevice;

    public Devices(RandomDevice randomDevice) {
        this.randomDevice = randomDevice;
        this.hotSource = UnicastProcessor.create();
        this.deviceStream = this.hotSource.publish().autoConnect().share();
    }

    private void publish() {
        this.hotSource.onNext(this.devices);
    }

    public Flux<ConcurrentMap<Integer, Device>> stream() {
        return this.deviceStream;
    }

    public Map<Integer, Device> data() {
        return Collections.unmodifiableMap(this.devices);
    }

    public Device update(Device device) {
        Instant now = Instant.now();
        device.setTimestamp(now);
        this.houseKeep(now);
        this.devices.put(device.getId(), device);
        this.publish();
        return device;
    }

    public Integer remove(Integer id) {
        this.devices.remove(id);
        this.publish();
        return id;
    }

    public void clear() {
        this.devices.clear();
        this.publish();
    }

    private void houseKeep(Instant now) {
        Instant expiration = now.minusSeconds(5);
        for (Integer k : this.devices.keySet()) {
            if (devices.get(k).getTimestamp().isBefore(expiration)) {
                devices.remove(k);
                this.randomDevice.putId(k).subscribe();
            }
        }
    }

    @Scheduled(initialDelay = 60_000, fixedDelay = 60_000)
    void houseKeep() {
        this.houseKeep(Instant.now());
        this.publish();
    }
}
