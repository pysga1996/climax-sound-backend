package com.lambda.validator;

import com.lambda.model.forms.PasswordDto;
import com.lambda.validator.constraints.RepeatedPasswordConstraint;

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
