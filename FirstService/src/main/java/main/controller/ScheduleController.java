package main.controller;

import main.ship.Ship;
import main.schedule.ScheduleGenerator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/firstservice")
public class ScheduleController {
    @GetMapping("/getschedule")
    public ResponseEntity<List<Ship>> getSchedule() {
        List<Ship> shipsList = new ScheduleGenerator().generateSchedule(300);
        return new ResponseEntity<>(shipsList, HttpStatus.OK);
    }
}
