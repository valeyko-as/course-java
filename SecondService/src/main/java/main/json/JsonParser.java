package main.json;

import main.ship.Ship;
import main.ship.Cargo;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;

public class JsonParser {
    public JsonParser() {
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .disable(SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS);
        writer = mapper.writer(new DefaultPrettyPrinter());
    }

    public void writeOriginalSchedule(String path) throws IOException {
        String URL = "http://localhost:4000/firstservice/getschedule";

        ResponseEntity<List<Ship>> response = new RestTemplate().exchange(URL, HttpMethod.GET,
                null, new ParameterizedTypeReference<>() {});

        writer.writeValue(Paths.get(path).toFile(), response.getBody());
    }

    public void writeSchedule(List<Ship> shipsList, String path) throws IOException {
        writer.writeValue(Paths.get(path).toFile(), shipsList);
    }

    public void writeStatistics(LinkedHashMap<String, Object> map, String path) throws IOException {
        writer.writeValue(Paths.get(path).toFile(), map);
    }

    public List<Ship> getScheduleFromJson(String path) throws IOException {
        return mapper.readValue(Paths.get(path).toFile(), new TypeReference<>() {});
    }

    public void addShipToOriginalSchedule() throws IOException {
        List<Ship> shipsList = getScheduleFromJson("OriginalSchedule.json");

        Scanner scanner = new Scanner(System.in);

        System.out.print("Ships count to add: ");
        int count = scanner.nextInt();
        scanner.nextLine();

        while (count != 0) {
            System.out.print("Enter ships name: ");
            String name = scanner.nextLine();

            System.out.print("Enter ships cargo type: ");
            Cargo.CargoType type = Cargo.CargoType.valueOf(scanner.nextLine()
                    .toUpperCase(Locale.ROOT));

            System.out.print("Enter ships cargo weight: ");
            int weight = scanner.nextInt();

            System.out.print("Ships arrive date/time\n" + "Enter year: ");
            int year = scanner.nextInt();
            System.out.print("Enter month: ");
            int month = scanner.nextInt();
            System.out.print("Enter day: ");
            int day = scanner.nextInt();
            System.out.print("Enter hours: ");
            int hours = scanner.nextInt();
            System.out.print("Enter minutes: ");
            int minutes = scanner.nextInt();

            Ship ship = new Ship(LocalDateTime.of(year, month, day, hours, minutes),
                    name, new Cargo(type, weight));

            shipsList.add(ship);
            --count;
        }
        shipsList.sort(Comparator.comparing(Ship::getArriveDateTime));

        writer.writeValue(Paths.get("OriginalSchedule.json").toFile(), shipsList);
    }

    private final ObjectMapper mapper;
    private final ObjectWriter writer;
}
