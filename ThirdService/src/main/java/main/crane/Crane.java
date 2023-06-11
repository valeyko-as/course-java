package main.crane;

import main.ship.Cargo;
import main.ship.Ship;
import main.model.Port;

import java.time.Duration;
import java.time.LocalDateTime;

public class Crane implements Runnable {
    public Crane(Port port, Cargo.CargoType type) {
        this.port = port;
        this.type = type;
        lastDepartureTime = LocalDateTime.MIN;
    }

    @Override
    public void run() {
        while (true) {
            incrementFreeCranesCount();
            ship = port.get(type);
            decrementFreeCranesCount();

            if (port.isCancelled().get() && ship == null) {
                break;
            }
            setArriveUnloadTime();

            try {
                Thread.sleep(ship.getParkingTime() / 60);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            ship.setDepartureUnloadTime(ship.getArriveUnloadTime().plusMinutes(ship.getParkingTime()));
            lastDepartureTime = ship.getDepartureUnloadTime();

            port.addToSchedule(ship);
        }
    }

    private void incrementFreeCranesCount() {
        switch (type) {
            case BULK -> CraneForBulk.getFreeCranesCount().incrementAndGet();
            case LIQUID -> CraneForLiquid.getFreeCranesCount().incrementAndGet();
            case CONTAINER -> CraneForContainer.getFreeCranesCount().incrementAndGet();
        }
    }

    private void decrementFreeCranesCount() {
        switch (type) {
            case BULK -> CraneForBulk.getFreeCranesCount().decrementAndGet();
            case LIQUID -> CraneForLiquid.getFreeCranesCount().decrementAndGet();
            case CONTAINER -> CraneForContainer.getFreeCranesCount().decrementAndGet();
        }
    }

    private void setArriveUnloadTime() {
        if (ship.getArriveDateTime().isBefore(lastDepartureTime)) {
            int amount = (int) Duration.between(ship.getArriveDateTime(), lastDepartureTime).toHours();

            port.changeFine(type, amount * 100);
            ship.setArriveUnloadTime(lastDepartureTime.plusMinutes(5));
        } else {
            ship.setArriveUnloadTime(ship.getArriveDateTime().plusMinutes(5));
        }
        ship.setAwaitingUnloadTime();
    }

    private final Port port;
    private final Cargo.CargoType type;
    private Ship ship;
    private LocalDateTime lastDepartureTime;
}
