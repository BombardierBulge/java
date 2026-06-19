package com.mycompany.myapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class DriverTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    public static Driver getDriverSample1() {
        return new Driver().id(1L).firstName("firstName1").lastName("lastName1");
    }

    public static Driver getDriverSample2() {
        return new Driver().id(2L).firstName("firstName2").lastName("lastName2");
    }

    public static Driver getDriverRandomSampleGenerator() {
        return new Driver().id(longCount.incrementAndGet()).firstName(UUID.randomUUID().toString()).lastName(UUID.randomUUID().toString());
    }
}
