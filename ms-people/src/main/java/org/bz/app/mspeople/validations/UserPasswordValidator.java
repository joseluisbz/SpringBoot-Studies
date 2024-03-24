package org.bz.app.mspeople.validations;

import org.bz.app.mspeople.entities.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class UserPasswordValidator implements Validator {

    @Value("${format.password}")
    private String regexp;

    @Override
    public boolean supports(Class<?> clazz) {
        return User.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        User user = (User) target;
        if (!user.getPassword().matches(regexp)) {
            errors.rejectValue("password", "badpattern.userDTO.password", "Expected Pattern: ".concat(regexp));
        }
    }

}
