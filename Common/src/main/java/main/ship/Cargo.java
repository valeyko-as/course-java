package main.ship;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Cargo {
    public enum CargoType {
        BULK("Bulk"),
        LIQUID("Liquid"),
        CONTAINER("Container");

        CargoType(String stringValue) {
            this.stringValue = stringValue;
        }

        public String getStringValue() {
            return stringValue;
        }

        private final String stringValue;
    }

    public Cargo() {
        type = null;
        weight = 0;
    }

    public Cargo(CargoType type, int weight) {
        this.type = type;
        this.weight = weight;
    }

    public void setType(CargoType type) {
        this.type = type;
    }

    @JsonProperty("TYPE")
    public CargoType getType() {
        return type;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    @JsonProperty("WEIGHT")
    public int getWeight() {
        return weight;
    }

    private CargoType type;
    private int weight;
}
