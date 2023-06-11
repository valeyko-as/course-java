package main;

import main.model.Simulation;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.client.HttpClientErrorException;

@SpringBootApplication
public class ThirdServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ThirdServiceApplication.class, args);

        try {
            new Simulation().startSimulation();
        } catch (HttpClientErrorException e) {
            e.printStackTrace();
        }
    }
}
