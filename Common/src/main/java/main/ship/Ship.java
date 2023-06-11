package main.ship;

import main.constants.*;
import com.fasterxml.jackson.annotation.*;

import java.time.Duration;
import java.time.LocalDateTime;

@JsonPropertyOrder({"NAME", "ARRIVE_DATE_TIME", "CARGO", "PARKING_TIME",
 "ARRIVE_UNLOAD_TIME", "DEPARTURE_UNLOAD_TIME", "AWAITING_UNLOAD_TIME"})
public class Ship implements Cloneable{
    public Ship() {
        name = null;
        cargo = null;
        arriveDateTime = null;
        arriveUnloadTime = null;
        awaitingUnloadTime = null;
        parkingTime = 0;
        queueCount = 0;
    }

    public Ship(LocalDateTime arriveDateTime, String name, Cargo cargo) {
        this.arriveDateTime = arriveDateTime;
        this.name = name;
        this.cargo = cargo;

        double performance = switch (cargo.getType()) {
            case BULK -> Constants.BULK_CRANE_PERFORMANCE;
            case LIQUID -> Constants.LIQUID_CRANE_PERFORMANCE;
            case CONTAINER -> Constants.CONTAINER_CRANE_PERFORMANCE;
        };
        parkingTime = (int) (cargo.getWeight() / performance * 60);
    }

    public void setArriveDateTime(LocalDateTime arriveDateTime) {
        this.arriveDateTime = arriveDateTime;
    }

    public void setParkingTime(int parkingTime) {
        this.parkingTime = parkingTime;
    }

    @JsonProperty("ARRIVE_DATE_TIME")
    public LocalDateTime getArriveDateTime() {
        return arriveDateTime;
    }

    @JsonProperty("NAME")
    public String getName() {
        return name;
    }

    @JsonProperty("CARGO")
    public Cargo getCargo() {
        return cargo;
    }

    @JsonProperty("PARKING_TIME")
    public int getParkingTime() {
        return parkingTime;
    }

    public void setArriveUnloadTime(LocalDateTime arriveUnloadTime) {
        this.arriveUnloadTime = arriveUnloadTime;
    }

    public void setDepartureUnloadTime(LocalDateTime departureUnloadTime) {
        this.departureUnloadTime = departureUnloadTime;
    }

    public void setAwaitingUnloadTime() {
        if (arriveUnloadTime != null) {
            awaitingUnloadTime = Duration.between(arriveDateTime, arriveUnloadTime);
        }
    }

    public void incrementCranesCount() {
        ++cranesCount;
    }

    public void setQueueCount(int queueCount) {
        this.queueCount = queueCount;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("ARRIVE_UNLOAD_TIME")
    public LocalDateTime getArriveUnloadTime() {
        return arriveUnloadTime;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("DEPARTURE_UNLOAD_TIME")
    public LocalDateTime getDepartureUnloadTime() {
        return departureUnloadTime;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("AWAITING_UNLOAD_TIME")
    public Duration getAwaitingUnloadTime() {
        return awaitingUnloadTime;
    }

    @JsonIgnore
    public int getCranesCount() {
        return cranesCount;
    }

    @JsonIgnore
    public int getQueueCount() {
        return queueCount;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    private LocalDateTime arriveDateTime;
    private final String name;
    private final Cargo cargo;
    private int parkingTime;
    private int cranesCount;
    private int queueCount;

    private LocalDateTime arriveUnloadTime;
    private LocalDateTime departureUnloadTime;
    private Duration awaitingUnloadTime;
}
