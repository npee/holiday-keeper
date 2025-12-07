package io.npee.holidaykeeper.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/holidays")
public class HolidayViewController {

    @GetMapping
    public String holidays() {
        return "holidays-view";
    }
}
