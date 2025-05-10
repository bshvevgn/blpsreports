package com.eg.blpsreports.listener;

import com.eg.blpsreports.config.kafka.KafkaProperty;
import com.eg.blpsreports.dto.AvailableDatesRequest;
import com.eg.blpsreports.dto.BookingInfoRequest;
import com.eg.blpsreports.dto.ListingInfo;
import com.eg.blpsreports.service.EmailTemplateService;
import com.eg.blpsreports.service.KafkaService;
import com.eg.blpsreports.service.QrCodeService;
import com.eg.blpsreports.utils.CalendarUtils;
import com.eg.blpsreports.utils.ImageUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DigestReportListener {
    private final ObjectMapper objectMapper;
    private final KafkaProperty kafkaProperty;
    private final QrCodeService qrCodeService;
    private final KafkaService kafkaService;
    private final EmailTemplateService emailTemplateService;
    private final ImageUtils imageUtils;

    @SneakyThrows
    @KafkaListener(topics = "${spring.kafka.topics.digest-report.name}", containerFactory = "kafkaListenerManualCommitContainerFactory")
    public void digestReport(@Payload String request, Acknowledgment acknowledgment) {
        log.info("Consumed digest report: {}", request);
        AvailableDatesRequest data = objectMapper.readValue(request, AvailableDatesRequest.class);

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


        kafkaService.sendMessage(
                kafkaProperty.getTopics().getEmailSender().getName(),
                Map.of(
                        "email",
                        data.email(),
                        "title",
                        "Доступные для бронирования даты в этом месяце",
                        "message",
                        html
                )
        );
        acknowledgment.acknowledge();

    }

    @SneakyThrows
    @KafkaListener(topics = "${spring.kafka.topics.booking-report.name}", containerFactory = "kafkaListenerManualCommitContainerFactory")
    public void bookingReport(@Payload String request, Acknowledgment acknowledgment) {
        log.info("Consumed booking report: {}", request);
        BookingInfoRequest data = objectMapper.readValue(request, BookingInfoRequest.class);

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

        kafkaService.sendMessage(
                kafkaProperty.getTopics().getEmailSender().getName(),
                Map.of(
                        "email", data.email(),
                        "title", "Успешное бронирование",
                        "message", html
                )
        );
        acknowledgment.acknowledge();
    }

    private String escapeHtml(String input) {
        return input
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}
