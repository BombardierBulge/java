package com.mycompany.myapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class RentalOfficeTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    public static RentalOffice getRentalOfficeSample1() {
        return new RentalOffice().id(1L).officeName("officeName1").city("city1");
    }

    public static RentalOffice getRentalOfficeSample2() {
        return new RentalOffice().id(2L).officeName("officeName2").city("city2");
    }

    public static RentalOffice getRentalOfficeRandomSampleGenerator() {
        return new RentalOffice()
            .id(longCount.incrementAndGet())
            .officeName(UUID.randomUUID().toString())
            .city(UUID.randomUUID().toString());
    }
}
