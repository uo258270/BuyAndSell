package com.validators;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.*;

import com.entity.UserEntity;
import com.service.UserService;

@Component
public class SignUpFormValidator implements Validator {
	@Autowired
	private UserService usersService;

	@Override
	public boolean supports(Class<?> aClass) {
		return UserEntity.class.equals(aClass);
	}

	@Override
	public void validate(Object target, Errors errors) {
		UserEntity user = (UserEntity) target;
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "email", "error.empty");
		try {
			if (usersService.findByEmail(user.getEmail()) != null) {
				errors.rejectValue("email", "error.signup.email.duplicate");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (user.getName().length() < 5 || user.getName().length() > 24) {
			errors.rejectValue("name", "error.signup.name.length");
		}
		if (user.getLastName().length() < 5 || user.getLastName().length() > 24) {
			errors.rejectValue("lastName", "error.signup.lastName.length");
		}
		if (user.getPassword().length() < 5 || user.getPassword().length() > 24) {
			errors.rejectValue("password", "error.signup.password.length");
		}
//		if (!user.getPasswordConfirm().equals(user.getPassword())) {
//			errors.rejectValue("passwordConfirm", "error.signup.passwordConfirm.coincidence");
//		}
		if (!user.getEmail().matches(".+@.+\\..+")) {
			errors.rejectValue("email", "error.signup.email.format");
		}
	}
}