package com.eg.blpsreports.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record AvailableDatesRequest(
        @NotNull @Email String email,
        @NotNull List<ListingInfo> listings
) {}