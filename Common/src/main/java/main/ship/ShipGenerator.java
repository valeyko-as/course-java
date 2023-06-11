package main.ship;

import main.constants.*;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.Random;

public class ShipGenerator {
    public Ship generateShip() {
        return new Ship(generateArriveDateTime(), generateName(), generateCargo());
    }

    public LocalDateTime generateArriveDateTime() {
        int day = 1 + random.nextInt(Month.APRIL.maxLength()),
                hour = random.nextInt(24), minute = random.nextInt(60);

        return LocalDateTime.of(2021, Month.APRIL, day, hour, minute);
    }

    public String generateName() {
        return "Titanic #" + (id++);
    }

    public Cargo generateCargo() {
        int randomType = 1 + random.nextInt(3);

        return switch (randomType) {
            case 1 -> new Cargo(Cargo.CargoType.BULK, 1000 + random.nextInt(Constants.MAX_BULK_SHIP_WEIGHT));
            case 2 -> new Cargo(Cargo.CargoType.LIQUID, 1000 + random.nextInt(Constants.MAX_LIQUID_SHIP_WEIGHT));
            default -> new Cargo(Cargo.CargoType.CONTAINER, 10 + random.nextInt(Constants.MAX_CONTAINER_SHIP_WEIGHT));
        };
    }

    private final Random random = new Random();
    private static int id = 1;
}
