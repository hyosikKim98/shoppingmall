package project.shopping.domain.product.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;

public record ProductUpdateRequest(
        String name,
        @Min(0) Long price,
        @Min(0) Integer stock,
        @Pattern(regexp = "ACTIVE|INACTIVE") String status
) {}