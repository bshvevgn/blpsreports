package com.eg.blpsreports.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record AccountReminderRequest(
        @NotNull @Email String email
) {}
