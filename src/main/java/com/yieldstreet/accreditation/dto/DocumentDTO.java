package com.yieldstreet.accreditation.dto;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class DocumentDTO {

    @Valid

    @NotBlank(message = "name is a mandatory field")
    @NotNull(message = "name is a mandatory field")
    @NotEmpty(message = "name is a mandatory field")
    @Pattern(regexp = "[0-9]+\\.[A-Za-z]+", message = "name must be in the format '{year}.{document type}'")
    private String name;

    @NotBlank(message = "mimeType is a mandatory field")
    @NotNull(message = "mimeType is a mandatory field")
    @NotEmpty(message = "mimeType is a mandatory field")
    @JsonProperty("mime_type")
    @Pattern(regexp = "[A-Za-z]+/[A-Za-z]+", message = "mimeType must be in the format '{some text}/{document type}'" )
    private String mimeType;

    @NotBlank(message = "content is a mandatory field")
    @NotNull(message = "content is a mandatory field")
    @NotEmpty(message = "content is a mandatory field")
    @Pattern(regexp = "^(?:[A-Za-z0-9+/]{4})*(?:[A-Za-z0-9+/]{2}==|[A-Za-z0-9+/]{3}=)?$", message = "content must be base64 encoded")
    private String content;
}
