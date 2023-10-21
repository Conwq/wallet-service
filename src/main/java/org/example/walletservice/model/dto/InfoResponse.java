package org.example.walletservice.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record InfoResponse(int status, String message) {
}
