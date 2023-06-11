package main.model;

import main.ship.Cargo;
import main.ship.Ship;
import main.constants.Constants;
import main.crane.CraneForBulk;
import main.crane.CraneForContainer;
import main.crane.CraneForLiquid;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class Port {
    public Port() {
        bulkShipsList =  new LinkedList<>();
        liquidShipsList = new LinkedList<>();
        containerShipsList = new LinkedList<>();
        scheduleList = new ArrayList<>();
        cancelled = new AtomicBoolean();

        service = Executors.newCachedThreadPool();
        service.execute(new ShipsArriveGenerator(this));

        for (int i = 0; i < CraneForBulk.getCranesCount().get(); i++) {
            service.execute(new CraneForBulk(this));
        }
        for (int i = 0; i < CraneForLiquid.getCranesCount().get(); i++) {
            service.execute(new CraneForLiquid(this));
        }
        for (int i = 0; i < CraneForContainer.getCranesCount().get(); i++) {
            service.execute(new CraneForContainer(this));
        }
    }

    protected synchronized void add(Ship ship) {
        switch (ship.getCargo().getType()) {
            case BULK -> bulkShipsList.add(ship);
            case LIQUID -> liquidShipsList.add(ship);
            case CONTAINER -> containerShipsList.add(ship);
        }
        notifyAll();
    }

    public synchronized Ship get(Cargo.CargoType type) {
        LinkedList<Ship> list = switch (type) {
            case BULK -> bulkShipsList;
            case LIQUID -> liquidShipsList;
            case CONTAINER -> containerShipsList;
        };

        while (list.isEmpty()) {
            try {
                if (cancelled.get()) {
                    return null;
                }
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Ship ship = list.peekFirst();
        ship.incrementCranesCount();

        if (ship.getCranesCount() == 1) {
            int newParkingTime = ship.getParkingTime() + new Random().nextInt(1441),
                    fine = (newParkingTime - ship.getParkingTime()) * Constants.PORT_HOUR_FINE / 60;

            AtomicInteger freeCranes = switch (type) {
                case BULK -> CraneForBulk.getFreeCranesCount();
                case LIQUID -> CraneForLiquid.getFreeCranesCount();
                case CONTAINER -> CraneForContainer.getFreeCranesCount();
            };

            if (freeCranes.get() > 1) {
                ship.setParkingTime(newParkingTime / 2);
                changeFine(type,fine / 2);
                return ship;
            }
            else {
                ship.setParkingTime(newParkingTime);
                changeFine(type, fine);
            }
            ship.setQueueCount(list.size());
        }
        list.removeFirst();
        return ship;
    }

    public synchronized void changeFine(Cargo.CargoType type, int amount) {
        AtomicInteger fine = switch (type) {
            case BULK -> CraneForBulk.getFine();
            case LIQUID -> CraneForLiquid.getFine();
            case CONTAINER -> CraneForContainer.getFine();
        };
        fine.addAndGet(amount);

        if (fine.get() >= Constants.CRANE_COST) {
            setCancelled();
        }
    }

    public synchronized void addToSchedule(Ship ship) {
        if (!scheduleList.contains(ship)) {
            scheduleList.add(ship);
        }
    }

    protected synchronized void setCancelled() {
        cancelled.set(true);
        notifyAll();
    }

    public ExecutorService getService() {
        return service;
    }

    public List<Ship> getSchedule() {
        return scheduleList;
    }

    public AtomicBoolean isCancelled() {
        return cancelled;
    }

    private final LinkedList<Ship> bulkShipsList;
    private final LinkedList<Ship> liquidShipsList;
    private final LinkedList<Ship> containerShipsList;
    private final List<Ship> scheduleList;

    private final AtomicBoolean cancelled;
    private final ExecutorService service;
}
