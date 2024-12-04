package com.yieldstreet.accreditation.dto;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DocumentDTO {

    @Valid

    @NotBlank(message = "name is a mandatory field")
    @NotNull(message = "name is a mandatory field")
    @NotEmpty(message = "name is a mandatory field")
    private String name;
    @NotBlank(message = "mimeType is a mandatory field")
    @NotNull(message = "mimeType is a mandatory field")
    @NotEmpty(message = "mimeType is a mandatory field")
    @JsonProperty("mime_type")
    private String mimeType;
    @NotBlank(message = "content is a mandatory field")
    @NotNull(message = "content is a mandatory field")
    @NotEmpty(message = "content is a mandatory field")
    private String content;
}
