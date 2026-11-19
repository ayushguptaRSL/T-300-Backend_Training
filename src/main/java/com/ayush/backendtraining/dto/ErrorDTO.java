package com.ayush.backendtraining.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ErrorDTO {

    private LocalDateTime timestamp;
    private String error;
    private int statusCode;
    private String message;
    private String path;
}
