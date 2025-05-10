package com.eg.blpsreports.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record ListingInfo(
    @NotBlank String title,
    @NotNull List<String> dates
) {}