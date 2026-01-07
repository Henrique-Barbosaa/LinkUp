package com.linkup.dto;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;

public record UrlRequest(
    @NotBlank(message = "A URL original é obrigatória.")
    @URL(message = "O formato da URL é inválido. Certifique-se de incluir http:// ou https://")
    String originalUrl
) {}