package com.example.ticketbooking.controller;

import com.example.ticketbooking.service.FlightService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final FlightService flightService;

    @GetMapping({"/", "/home"})
    public String home(Model model) {
        model.addAttribute("featuredFlights",
                flightService.findAll(PageRequest.of(0, 4)).getContent());
        return "index";
    }

    @GetMapping("/access-denied")
    public String accessDenied() {
        return "error/access-denied";
    }
}
