package com.eg.blpsreports.controller;
import com.eg.blpsreports.dto.AccountReminderRequest;
import com.eg.blpsreports.dto.BookingInfoRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/info")
@RequiredArgsConstructor
public class InfoController {

    @PostMapping("/booking")
    public String renderBookingHtml(@RequestBody BookingInfoRequest data, Model model) {
        model.addAttribute("booking", data);
        return "booking";
    }

    @PostMapping("/account-reminder")
    public String renderAccountReminderHtml(@RequestBody AccountReminderRequest data, Model model) {
        model.addAttribute("email", data.email());
        return "account-reminder";
    }
}
