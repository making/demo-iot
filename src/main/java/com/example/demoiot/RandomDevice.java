package com.example.demoiot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class RandomDevice {
    private final BlockingDeque<Integer> deque;
    private static final Logger log = LoggerFactory.getLogger(RandomDevice.class);

    public RandomDevice(int maxDevice) {
        List<Integer> random = IntStream.rangeClosed(1, maxDevice).boxed()
                .collect(Collectors.toList());
        Collections.shuffle(random);
        this.deque = new LinkedBlockingDeque<>(random);
    }

    public Mono<Integer> takeId() {
        return Mono.fromCallable(() -> {
            try {
                Integer take = this.deque.take();
                log.info("take {}", take);
                return take;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return null;
            }
        }).publishOn(Schedulers.elastic());
    }

    public Mono<Void> putId(Integer id) {
        return Mono.fromRunnable(() -> {
            try {
                log.info("put {}", id);
                this.deque.put(id);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).publishOn(Schedulers.elastic()).then();
    }
}
