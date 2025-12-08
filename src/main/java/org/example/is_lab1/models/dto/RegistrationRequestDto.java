package org.example.is_lab1.models.dto;

import org.example.is_lab1.models.entity.Role;

public record RegistrationRequestDto(String username, String password, Role role) {}
