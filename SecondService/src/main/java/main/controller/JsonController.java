package main.controller;

import main.json.JsonParser;
import main.ship.Ship;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;

@Controller
@RequestMapping("/secondservice")
public class JsonController {
    @GetMapping("/write/originalschedule/{path}")
    public ResponseEntity<String> writeOriginalSchedule(@PathVariable(value = "path") String path) {
        try {
            new JsonParser().writeOriginalSchedule(path);
            return new ResponseEntity<>("OK", HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>("BAD", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/read/schedule/{path}")
    public ResponseEntity<List<Ship>> getScheduleFromJson(@PathVariable(value = "path") String path) {
        try {
            List<Ship> shipsList = new JsonParser().getScheduleFromJson(path);
            return new ResponseEntity<>(shipsList, HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/write/schedule/{path}")
    public ResponseEntity<String> writePortSchedule(@PathVariable(value = "path") String path,
                                                      @RequestBody List<Ship> shipsList) {
        try {
            new JsonParser().writeSchedule(shipsList, path);
            return new ResponseEntity<>("OK", HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>("BAD", HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/write/statistics/{path}")
    public ResponseEntity<String> writePortStatistics(@PathVariable(value = "path") String path,
                                                      @RequestBody LinkedHashMap<String, Object> map) {
        try {
            new JsonParser().writeStatistics(map, path);
            return new ResponseEntity<>("OK", HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>("BAD", HttpStatus.BAD_REQUEST);
        }
    }
}
