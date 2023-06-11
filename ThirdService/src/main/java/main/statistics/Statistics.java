package main.statistics;

import main.ship.Cargo;
import main.ship.Ship;
import main.crane.CraneForBulk;
import main.crane.CraneForContainer;
import main.crane.CraneForLiquid;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

public class Statistics {
    public Statistics(List<Ship> originalSchedule, List<Ship> portSchedule) {
        this.originalSchedule = originalSchedule.stream().sorted(Comparator.comparing(Ship::getName))
                .collect(Collectors.toList());
        this.portSchedule = portSchedule.stream().sorted(Comparator.comparing(Ship::getName))
                .collect(Collectors.toList());
    }

    public LinkedHashMap<String, Object> generateStatistics() {
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();

        map.put("TOTAL_UNLOAD", getUnloadCount());
        map.put("TOTAL_FINE", getTotalFine());
        map.put("CRANES_FOR_BULK_COUNT", getCranesForBulkCount());
        map.put("CRANES_FOR_LIQUID_COUNT", getCranesForLiquidCount());
        map.put("CRANES_FOR_CONTAINER_COUNT", getCranesForContainerCount());
        map.put("AVERAGE_BULK_QUEUE_SIZE", getAverageQueueCount(Cargo.CargoType.BULK));
        map.put("AVERAGE_LIQUID_QUEUE_SIZE", getAverageQueueCount(Cargo.CargoType.LIQUID));
        map.put("AVERAGE_CONTAINER_QUEUE_SIZE", getAverageQueueCount(Cargo.CargoType.CONTAINER));
        map.put("AVERAGE_AWAITING_TIME", getAverageAwaitingTime().toString());
        map.put("AVERAGE_UNLOAD_DELAY", getAverageUnloadDelay().toString());
        map.put("MAX_UNLOAD_DELAY", getMaxUnloadDelay().toString());

        return map;
    }

    public int getUnloadCount() {
        return portSchedule.size();
    }

    public int getCranesForBulkCount() {
        return CraneForBulk.getCranesCount().get();
    }

    public int getCranesForLiquidCount() {
        return CraneForLiquid.getCranesCount().get();
    }

    public int getCranesForContainerCount() {
        return CraneForContainer.getCranesCount().get();
    }

    public int getTotalFine() {
        return CraneForBulk.getFine().get() +
                CraneForLiquid.getFine().get() +
                CraneForContainer.getFine().get();
    }

    public double getAverageQueueCount(Cargo.CargoType type) {
        double queueCount = 0, shipsCount = 0;

        for (Ship ship : portSchedule) {
            if (ship.getCargo().getType().equals(type)) {
                queueCount += ship.getQueueCount();
                shipsCount++;
            }
        }
        return new BigDecimal(queueCount / shipsCount).setScale(1, RoundingMode.CEILING).doubleValue();
    }

    public Duration getAverageAwaitingTime() {
        double minutes = portSchedule.stream().mapToInt(ship -> (int) ship
                .getAwaitingUnloadTime().toMinutes()).sum();

        return Duration.ofMinutes((long) minutes / portSchedule.size());
    }

    public Duration getAverageUnloadDelay() {
        double minutes = 0;

        for (int i = 0; i < portSchedule.size(); i++) {
            minutes += portSchedule.get(i).getParkingTime() - originalSchedule.get(i).getParkingTime();
        }
        return Duration.ofMinutes((long) minutes / portSchedule.size());
    }

    public Duration getMaxUnloadDelay() {
        int max = 0;

        for (int i = 0; i < portSchedule.size(); i++) {
            int delay = portSchedule.get(i).getParkingTime() - originalSchedule.get(i).getParkingTime();

            if (delay > max) {
                max = delay;
            }
        }
        return Duration.ofMinutes(max);
    }

    private final List<Ship> portSchedule;
    private final List<Ship> originalSchedule;
}
