 package org.bz.app.mspeople.validations;

import org.bz.app.mspeople.dtos.UserDTO;
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
         return UserDTO.class.isAssignableFrom(clazz);
     }

     @Override
     public void validate(Object target, Errors errors) {
         UserDTO userDTO = (UserDTO)target;
         if (!userDTO.getPassword().matches(regexp)) {
             errors.rejectValue("password", "badpattern.userDTO.password", "Expected Pattern: ".concat(regexp));
         }
     }

 }
