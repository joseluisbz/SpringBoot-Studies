package org.bz.app.mspeople.components.validations;

import lombok.RequiredArgsConstructor;
import org.bz.app.mspeople.dtos.UserRequestDTO;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Locale;
import java.util.Objects;

@RequiredArgsConstructor
@Component
public class UserPasswordValidator implements Validator {

    @Value("${format.password}")
    private String regexp;

    @Qualifier("customMessageSource")
    private final MessageSource messageSource;

    @Override
    public boolean supports(Class<?> clazz) {
        return UserRequestDTO.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        UserRequestDTO userRequestDTO = (UserRequestDTO) target;
        /*
        if (userRequestDTO.getPassword() == null) {
            errors.rejectValue("password", "missing.user.password", "The password must be non-null.");
        } else if (!userRequestDTO.getPassword().matches(regexp)) {
            errors.rejectValue("password", "badpattern.user.password", "Expected Pattern: ".concat(regexp));
        }
        */

        if (userRequestDTO.getPassword() == null) {
            errors.rejectValue("password", null, Objects.requireNonNull(
                    messageSource.getMessage("missing.user.password", null, "The password must be non-null.", Locale.getDefault())
            ));
        } else if (!userRequestDTO.getPassword().matches(regexp)) {
            errors.rejectValue("password", null, Objects.requireNonNull(
                    messageSource.getMessage("badpattern.user.password", null, "Expected Pattern: ".concat(regexp), Locale.getDefault())
            ));
        }
    }

}
