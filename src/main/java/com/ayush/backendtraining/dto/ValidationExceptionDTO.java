package com.ayush.backendtraining.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ValidationExceptionDTO {

    private LocalDateTime timestamp;
    private String error;
    private int statusCode;
    private Map<String, String> fieldErrors;
    private String path;
}
