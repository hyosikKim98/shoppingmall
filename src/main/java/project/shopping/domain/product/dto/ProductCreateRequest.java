package project.shopping.domain.product.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record ProductCreateRequest(
        @NotBlank String name,
        @Min(0) long price,
        @Min(0) int stock
) {}
