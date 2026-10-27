package com.luciano.workshop.dto;

import jakarta.validation.constraints.*;

public record UserUpdateRequest(
    @NotBlank @Size(max = 120) String name,
    @NotBlank @Email @Size(max = 254) String email,
    @NotBlank @Size(max = 30) String phone
) {}
