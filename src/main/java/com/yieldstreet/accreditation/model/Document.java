package com.yieldstreet.accreditation.model;
import jakarta.validation.constraints.NotBlank;

public class Document {
    @NotBlank
    private String name;
    @NotBlank
    private String mimeType;
    @NotBlank
    private String contentBase64;
}
