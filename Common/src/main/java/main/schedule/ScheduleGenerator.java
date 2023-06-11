package main.schedule;

import main.ship.Cargo;
import main.ship.Ship;
import main.ship.ShipGenerator;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ScheduleGenerator {
    public List<Ship> generateSchedule(int shipsAmount) {
        shipsList = new ArrayList<>(shipsAmount);

        for (int i = 0; i < shipsAmount; ++i) {
            shipsList.add(new ShipGenerator().generateShip());
        }
        shipsList.sort(Comparator.comparing(Ship::getArriveDateTime));

        return shipsList;
    }

    public List<Ship> getSchedule() {
        return shipsList;
    }

    public static List<Ship> getCloneSchedule(List<Ship> shipsList) {
        List<Ship> cloneList = new ArrayList<>();

        for (Ship ship : shipsList) {
            try {
                cloneList.add((Ship) ship.clone());
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
        }
        return cloneList;
    }

    public static void printSchedule(List<Ship> shipsList) {
        shipsList.forEach(ship -> System.out.println(
                "Arrive date/time: " + ship.getArriveDateTime() + "\n" +
                        "Name: " + ship.getName() + "\n" +
                        "Cargo type: " + ship.getCargo().getType().getStringValue() + "\n" +
                        "Cargo weight: " + ship.getCargo().getWeight() +
                        ((ship.getCargo().getType().equals(Cargo.CargoType.CONTAINER)) ?
                                " containers" : " tons") + "\n" +
                        "Parking time: " + ship.getParkingTime() + " minutes" + "\n\n"));
    }

    private List<Ship> shipsList;
}
