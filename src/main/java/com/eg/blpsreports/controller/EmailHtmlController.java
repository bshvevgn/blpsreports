package com.eg.blpsreports.controller;

import com.eg.blpsreports.dto.AccountReminderRequest;
import com.eg.blpsreports.dto.AvailableDatesRequest;
import com.eg.blpsreports.dto.BookingInfoRequest;
import com.eg.blpsreports.dto.ListingInfo;
import com.eg.blpsreports.service.EmailTemplateService;
import com.eg.blpsreports.service.QrCodeService;
import com.eg.blpsreports.utils.CalendarUtils;
import com.eg.blpsreports.utils.ImageUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/email")
public class EmailHtmlController {

    private final EmailTemplateService emailTemplateService;
    private final QrCodeService qrCodeService;
    private final ImageUtils imageUtils;

    @PostMapping("/booking")
    public ResponseEntity<Map<String, String>> getBookingHtml(@Valid @RequestBody BookingInfoRequest data) {
        SecureRandom random = new SecureRandom();
        long timePart = System.currentTimeMillis() % 1_000_000L;
        int randomPart = random.nextInt(1000);
        String numericCode = String.format("%05d%03d", timePart, randomPart);

        String qrCodeBase64 = qrCodeService.generateQrCodeBase64(
                "booking-id-" + numericCode, 200, 200
        );


        String logoBase64 = "data:image/png;base64," + imageUtils.encodeImageToBase64("static/img/logo.png");
        String triangleBase64 = "data:image/png;base64," + imageUtils.encodeImageToBase64("static/img/triangle.png");
        String checkmarkBase64 = "data:image/png;base64," + imageUtils.encodeImageToBase64("static/img/checkmark.png");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM yyyy 'г.'", new Locale("ru"));
        LocalDate start = LocalDate.parse(data.startDate(), DateTimeFormatter.ISO_LOCAL_DATE);
        LocalDate end = LocalDate.parse(data.endDate(), DateTimeFormatter.ISO_LOCAL_DATE);

        String formattedStartDate = start.format(formatter);
        String formattedEndDate = end.format(formatter);

        Map<String, Object> vars = Map.of(
                "booking", data,
                "formattedStartDate", formattedStartDate,
                "formattedEndDate", formattedEndDate,
                "id", numericCode,
                "qrCode", qrCodeBase64,
                "logoBase64", logoBase64,
                "triangleBase64", triangleBase64,
                "checkmarkBase64", checkmarkBase64
        );

        String html = emailTemplateService.renderTemplate("booking", vars);
        return ResponseEntity.ok(Map.of(
                "email", data.email(),
                "title", "Успешное бронирование",
                "message", html
        ));
    }

    @PostMapping("/available-dates")
    public ResponseEntity<Map<String, String>> getAvailableDatesHtml(@Valid @RequestBody AvailableDatesRequest data) {
        StringBuilder combinedHtml = new StringBuilder();

        for (ListingInfo listing : data.listings()) {
            List<LocalDate> dates = listing.dates().stream()
                    .map(LocalDate::parse)
                    .toList();

            String calendarHtml = CalendarUtils.generateCalendarHtml(dates);

            combinedHtml.append("<div style='margin-bottom: 40px;'>")
                    .append("<h2 class='subtitle'>")
                    .append(escapeHtml(listing.title()))
                    .append("</h2>")
                    .append(calendarHtml)
                    .append("</div>");
        }

        String logoBase64 = "data:image/png;base64," + imageUtils.encodeImageToBase64("static/img/logo.png");

        Map<String, Object> vars = Map.of(
                "id", UUID.randomUUID(),
                "logoBase64", logoBase64,
                "calendarHtml", combinedHtml.toString()
        );

        String html = emailTemplateService.renderTemplate("available-dates", vars);

        return ResponseEntity.ok(Map.of(
                "email", data.email(),
                "title", "Доступные для бронирования даты в этом месяце",
                "message", html
        ));
    }


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

    public String escapeHtml(String input) {
        return input
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}
