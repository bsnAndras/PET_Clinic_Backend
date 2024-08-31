package com.greenfox.dramacsoport.petclinicbackend.dtos.update;

import com.greenfox.dramacsoport.petclinicbackend.errors.AppServiceErrors;
import jakarta.validation.constraints.*;

public record EditUserRequestDTO(
        @Email(message = AppServiceErrors.EMAIL_FIELD_NOT_VALID)
        @NotNull(message = "Please enter a valid email address!")
        @NotBlank(message = "Please enter a valid email address!")
        String email,

        @NotNull(message = "Please enter your original password!")
        @NotEmpty(message = "Please enter your original password!")
        String originalPassword,

        @NotNull(message = "Please enter a valid password!")
        @NotEmpty(message = "Please enter a valid password!")
        @Size(min = 3, message = AppServiceErrors.SHORT_PASSWORD)
        String password,

        @NotNull(message = "Please enter your display name!")
        @NotBlank(message = "Please enter your display name!")
        @Pattern(regexp = "^[a-zA-Z0-9_-]*$", message = "Display name can only contain alphanumeric characters")
        @Size(max = 20, message = "Display name could not be longer than 20 characters")
        String username) {

}
