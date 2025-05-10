package com.eg.blpsreports.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record BookingInfoRequest(
        @NotNull String listing,
        @NotNull @Email String email,
        @NotNull String address,
        @NotNull String startDate,
        @NotNull String endDate,
        @NotNull double totalPrice
) {}
