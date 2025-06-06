package com.nexbus.frontendNex.controller;

import com.nexbus.frontendNex.dto.BusDTO;
import com.nexbus.frontendNex.service.BusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;

@Controller
public class BusController {
    private final BusService busService;

    @Autowired
    public BusController(BusService busService) {
        this.busService = busService;
    }

    @GetMapping("/api/buses")
    public String showBuses(Model model, HttpSession session) {
        String authToken = (String) session.getAttribute("authToken");
        if (authToken == null) {
            return "redirect:/login";
        }
        model.addAttribute("pageTitle", "Buses");
        model.addAttribute("adminName", session.getAttribute("adminName"));
        model.addAttribute("adminRole", session.getAttribute("adminRole"));
        model.addAttribute("authToken", authToken);
        try {
            model.addAttribute("buses", busService.getAllBuses());
        } catch (Exception e) {
            model.addAttribute("error", "Failed to fetch buses: " + e.getMessage());
        }
        return "buses";
    }

    @PostMapping("/api/buses")
    public String addBus(@ModelAttribute BusDTO busDTO, Model model, HttpSession session) {
        if (session.getAttribute("authToken") == null) {
            return "redirect:/login";
        }
        try {
            busService.createBus(busDTO);
            return "redirect:/api/buses";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to add bus: " + e.getMessage());
            return "buses";
        }
    }

    @PostMapping("/api/buses/{id}/delete")
    public String deleteBus(@PathVariable Integer id, Model model, HttpSession session) {
        if (session.getAttribute("authToken") == null) {
            return "redirect:/login";
        }
        try {
            busService.deleteBus(id);
            return "redirect:/api/buses";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to delete bus: " + e.getMessage());
            return "buses";
        }
    }
}