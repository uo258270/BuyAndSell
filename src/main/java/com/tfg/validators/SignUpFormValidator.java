package com.tfg.validators;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.tfg.entity.UserEntity;
import com.tfg.service.UserService;

@Component
public class SignUpFormValidator implements Validator {
	@Autowired
	public UserService usersService;

	@Override
	public boolean supports(Class<?> aClass) {
		return UserEntity.class.equals(aClass);
	}

	@Override
	public void validate(Object target, Errors errors) {
		UserEntity user = (UserEntity) target;
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "email", "error.empty");

		try {
			UserEntity userEnt = usersService.findByEmail(user.getEmail());
			if (userEnt != null) {
				// el correo y esta en base de datos y por tanto esta repetido
				errors.rejectValue("email", "error.signup.email.duplicate");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (user.getName().length() < 2 || user.getName().length() > 24) {
			errors.rejectValue("name", "error.signup.name.length");
		}
		if (user.getLastName().length() < 2 || user.getLastName().length() > 40) {
			errors.rejectValue("lastName", "error.signup.lastName.length");
		}
		if (user.getPassword().length() < 5 || user.getPassword().length() > 24) {
			errors.rejectValue("password", "error.signup.password.length");
		}
		if (!user.getPasswordConfirm().equals(user.getPassword())) {
			errors.rejectValue("passwordConfirm", "error.signup.passwordConfirm.coincidence");
		}
		if (!user.getEmail().matches(".+@.+\\..+")) {
			errors.rejectValue("email", "error.signup.email.format");
		}
	}
}