package io.brightskies.loyalty.user.DTO;

import io.brightskies.loyalty.constants.ValidationMessages;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class UserDTO {
    @Schema(requiredProperties = {
            ValidationMessages.NOT_BLANK_SCHEMA })
    @NotBlank(message = "name"
            + ValidationMessages.NOT_BLANK)
    private String name;

    @Schema(requiredProperties = {
            ValidationMessages.NOT_BLANK_SCHEMA })
    @NotBlank(message = "email"
            + ValidationMessages.NOT_BLANK)
    @Email(message = ValidationMessages.INVALID_EMAIL, regexp = ValidationMessages.EMAIL_REGEX)
    private String email;

    @Schema(requiredProperties = {
            ValidationMessages.NOT_BLANK_SCHEMA })
    @NotBlank(message = "password"
            + ValidationMessages.NOT_BLANK)
    private String password;
}
