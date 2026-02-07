package project.shopping.domain.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;


public record SignupRequest(
        @Email @NotBlank String email,
        @NotBlank String password,
        @Pattern(regexp = "CUSTOMER|SELLER") String role
) {}