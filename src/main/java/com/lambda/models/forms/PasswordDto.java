package com.lambda.models.forms;

import com.lambda.validators.constraints.RepeatedPasswordConstraint;
import lombok.Data;
import org.springframework.lang.Nullable;

import javax.validation.constraints.NotBlank;

@Data
@RepeatedPasswordConstraint
public class PasswordDto {
    @Nullable
    private String oldPassword;

    @NotBlank
    private String newPassword;

    @NotBlank
    private String repeatedNewPassword;
}
