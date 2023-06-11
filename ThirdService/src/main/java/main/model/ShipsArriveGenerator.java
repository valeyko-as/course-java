package main.model;

import main.ship.Ship;

import java.time.Duration;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class ShipsArriveGenerator implements Runnable {
    public ShipsArriveGenerator(Port port) {
        this.port = port;
        duration = Duration.ZERO;
    }

    @Override
    public void run() {
        int i = 0;

        for (Ship ship : shipsList) {
            if (port.isCancelled().get()) {
                return;
            }
            port.add(ship);

            if (i < shipsList.size() - 1) {
                duration = Duration.between(
                        shipsList.get(i).getArriveDateTime(),
                        shipsList.get((i++) + 1).getArriveDateTime());
            }

            try {
                Thread.sleep(duration.toHours());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        port.setCancelled();
    }

    public static void setShipsList(List<Ship> shipsList) {
        ShipsArriveGenerator.shipsList = getArriveList(shipsList);
    }

    private static List<Ship> getArriveList(List<Ship> shipsList) {
        shipsList.forEach(ship -> ship.setArriveDateTime(
                ship.getArriveDateTime().plusDays(new Random().nextInt(15) - 7)));

        shipsList.sort(Comparator.comparing(Ship::getArriveDateTime));

        return shipsList;
    }

    private final Port port;
    private Duration duration;
    private static List<Ship> shipsList;
}
