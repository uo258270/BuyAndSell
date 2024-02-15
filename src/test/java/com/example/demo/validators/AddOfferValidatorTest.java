package com.example.demo.validators;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;

import com.entity.ProductEntity;
import com.validators.AddOfferValidator;

class AddOfferValidatorTest {

    private AddOfferValidator validator;
    private Errors errors;
    private ProductEntity product;

    @BeforeEach
    void setUp() {
        validator = new AddOfferValidator();
        product = new ProductEntity();
        errors = new BeanPropertyBindingResult(product, "product");
    }

    @Test
    void supports() {
        assertTrue(validator.supports(ProductEntity.class));
        assertFalse(validator.supports(Object.class));
    }

    @Test
    void validate_emptyNameAndDetail_shouldReturnErrors() {
    	product.setName("a"); 
        product.setDetail("d"); 
        validator.validate(product, errors);
        assertTrue(errors.hasErrors());
        assertTrue(errors.hasFieldErrors("name"));
        assertTrue(errors.hasFieldErrors("detail"));
    }

    @Test
    void validate_invalidNameLength_shouldReturnError() {
        product.setName("a"); 
        product.setDetail("details"); 
        product.setPrice(10.0);
        validator.validate(product, errors);
        assertTrue(errors.hasFieldErrors("name"));
    }

    @Test
    void validate_invalidDetailLength_shouldReturnError() {
    	product.setName("Valid Name");
        product.setDetail(""); 
        product.setPrice(10.0);

        validator.validate(product, errors);
        assertTrue(errors.hasFieldErrors("detail"));
    }


    @Test
    void validate_negativeOrZeroPrice_shouldReturnError() {
        product.setName("Valid Name");
        product.setDetail("Valid Detail");

      
        product.setPrice(null);
        validator.validate(product, errors);
        assertTrue(errors.hasFieldErrors("price"));

       
        product.setPrice(0.0);
        validator.validate(product, errors);
        assertTrue(errors.hasFieldErrors("price"));

       
        product.setPrice(-10.0);
        validator.validate(product, errors);
        assertTrue(errors.hasFieldErrors("price"));
    }

    @Test
    void validate_validProduct_shouldNotReturnErrors() {
        product.setName("Valid Name");
        product.setDetail("Valid Detail");
        product.setPrice(10.0);
        validator.validate(product, errors);
        assertFalse(errors.hasErrors());
    }
}
