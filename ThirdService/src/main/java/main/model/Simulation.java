package main.model;

import main.constants.Constants;
import main.schedule.ScheduleGenerator;
import main.crane.CraneForBulk;
import main.crane.CraneForContainer;
import main.crane.CraneForLiquid;
import main.ship.Ship;
import main.statistics.Statistics;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class Simulation {
    public void startSimulation() {
        System.out.println("Start simulation");

        //WRITE ORIGINAL SCHEDULE
        String URL = "http://localhost:4001/secondservice/write/originalschedule/OriginalSchedule.json";

        ResponseEntity<String> stringResponse = new RestTemplate().getForEntity(URL, String.class);

        if (stringResponse.getStatusCode().equals(HttpStatus.BAD_REQUEST)) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST);
        }
        //READ ORIGINAL SCHEDULE FROM JSON
        URL = "http://localhost:4001/secondservice/read/schedule/OriginalSchedule.json";

        ResponseEntity<List<Ship>> listResponse = new RestTemplate().exchange(URL, HttpMethod.GET,
                null, new ParameterizedTypeReference<>() {});

        if (listResponse.getStatusCode().equals(HttpStatus.BAD_REQUEST)) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST);
        }
        //PORT MODELING
        ShipsArriveGenerator.setShipsList(ScheduleGenerator.getCloneSchedule(listResponse.getBody()));

        Port port = simulatePort();

        //SAVE PORT SCHEDULE TO JSON
        URL = "http://localhost:4001/secondservice/write/schedule/PortSchedule.json";

        stringResponse = new RestTemplate().postForEntity(URL, port.getSchedule(), String.class);

        if (stringResponse.getStatusCode().equals(HttpStatus.BAD_REQUEST)) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST);
        }
        //SAVE PORT STATISTICS TO JSON
        URL = "http://localhost:4001/secondservice/write/statistics/Statistics.json";

        stringResponse = new RestTemplate().postForEntity(URL, new Statistics(listResponse.getBody(),
                port.getSchedule()).generateStatistics(), String.class);

        if (stringResponse.getStatusCode().equals(HttpStatus.BAD_REQUEST)) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST);
        }
        System.out.println("End simulation");
    }

    private Port simulatePort() {
        while (true) {
            Port port = new Port();

            port.getService().shutdown();

            try {
                port.getService().awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (CraneForBulk.getFine().get() > Constants.CRANE_COST) {
                CraneForBulk.getCranesCount().incrementAndGet();
            }
            else if (CraneForLiquid.getFine().get() > Constants.CRANE_COST) {
                CraneForLiquid.getCranesCount().incrementAndGet();
            }
            else if (CraneForContainer.getFine().get() > Constants.CRANE_COST) {
                CraneForContainer.getCranesCount().incrementAndGet();
            }
            else {
                return port;
            }
            port.getSchedule().clear();
            CraneForBulk.getFine().set(0);
            CraneForLiquid.getFine().set(0);
            CraneForContainer.getFine().set(0);
            CraneForBulk.getFreeCranesCount().set(0);
            CraneForLiquid.getFreeCranesCount().set(0);
            CraneForContainer.getFreeCranesCount().set(0);
        }
    }
}
