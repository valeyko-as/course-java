package main.crane;

import main.ship.Cargo;
import main.model.Port;

import java.util.concurrent.atomic.AtomicInteger;

public class CraneForContainer extends Crane {
    public CraneForContainer(Port port) {
        super(port, Cargo.CargoType.CONTAINER);
    }

    public static AtomicInteger getCranesCount() {
        return cranesCount;
    }

    public static AtomicInteger getFreeCranesCount() {
        return freeCranesCount;
    }

    public static AtomicInteger getFine() {
        return fine;
    }

    private static final AtomicInteger cranesCount = new AtomicInteger(1);
    private static final AtomicInteger freeCranesCount = new AtomicInteger();
    private static final AtomicInteger fine = new AtomicInteger();
}
