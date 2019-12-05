package com.lambda.validators;

import com.lambda.models.forms.PasswordDto;
import com.lambda.validators.constraints.RepeatedPasswordConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class RepeatedPasswordValidator implements ConstraintValidator<RepeatedPasswordConstraint, PasswordDto> {
    @Override
    public void initialize (RepeatedPasswordConstraint constraintAnnotation) {
    }

    @Override
    public boolean isValid (PasswordDto passwordDto, ConstraintValidatorContext context) {
        if (passwordDto.getNewPassword() == null || passwordDto.getRepeatedNewPassword() == null) {
            return false;
        } else {
            return passwordDto.getNewPassword().equals(passwordDto.getRepeatedNewPassword());
        }
    }
}
