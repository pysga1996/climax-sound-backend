package com.lambda.model.forms;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
public class GetResetPasswordTokenForm {
    @NotBlank
    @Email
    String email;
}
