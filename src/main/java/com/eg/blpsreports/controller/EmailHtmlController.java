package com.eg.blpsreports.controller;

import com.eg.blpsreports.dto.AccountReminderRequest;
import com.eg.blpsreports.service.EmailTemplateService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/email")
public class EmailHtmlController {

    private final EmailTemplateService emailTemplateService;


    @PostMapping("/account-reminder")
    public ResponseEntity<Map<String, String>> getAccountReminderHtml(@Valid @RequestBody AccountReminderRequest data) {
        Map<String, Object> vars = Map.of("email", data.email());
        String html = emailTemplateService.renderTemplate("account-reminder", vars);
        return ResponseEntity.ok(Map.of(
                "email", data.email(),
                "title", "Скорее возвращайтесь!",
                "message", html
        ));
    }
}
