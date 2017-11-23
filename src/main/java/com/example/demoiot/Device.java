package com.example.demoiot;

import java.security.SecureRandom;
import java.time.Instant;

public class Device {
    private Integer id;
    private Double power;
    private Instant timestamp;
    private String color;
    private static SecureRandom random = new SecureRandom();


    public Device() {
        this.color = "rgba(" + random.nextInt(256) + "," + random.nextInt(256) + "," + random.nextInt(256) + ",1)";
    }

    public Device(Integer id, Double power, Instant timestamp) {
        this();
        this.id = id;
        this.power = power;
        this.timestamp = timestamp;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Double getPower() {
        return power;
    }

    public void setPower(Double power) {
        this.power = power;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getColor() {
        return color;
    }
}
