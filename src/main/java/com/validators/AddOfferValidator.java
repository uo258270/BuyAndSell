package com.validators;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.entity.ProductEntity;



@Component
public class AddOfferValidator implements Validator {

	
	@Override
	public boolean supports(Class<?> aClass) {
		return ProductEntity.class.equals(aClass);
	}

	@Override
	public void validate(Object target, Errors errors) {
	    ProductEntity product = (ProductEntity) target;
	    ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", "Error.empty");
	    ValidationUtils.rejectIfEmptyOrWhitespace(errors, "detail", "Error.empty");
	    
	    if (product.getName().length() < 3 || product.getName().length() > 20) {
	        errors.rejectValue("name", "Error.addOffer.title.length");
	    }
	    
	    if (product.getDetail().length() < 3 || product.getDetail().length() > 40) {
	        errors.rejectValue("detail", "Error.addOffer.details.length");
	    }
	    
	    if (product.getPrice() == null) {
	        errors.rejectValue("price", "Error.empty");
	    } else if (product.getPrice() <= 0) {
	        errors.rejectValue("price", "Error.addOffer.price");
	    }
	}

}
	
	
