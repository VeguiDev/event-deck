package dev.vegui.eventdeck.validation;

import dev.vegui.eventdeck.exceptions.ValidationException;

public class StringFluentValidator extends FluentValidator<String> {
    public StringFluentValidator(String field, String value) {
        super(field, value);
    }

    @Override
    public StringFluentValidator notEmpty() {
        super.notEmpty();
        if(this.getValue().isEmpty()) {
            throw new ValidationException(this.getField() + " cannot be empty");
        }

        return this;
    }

    public StringFluentValidator maxLength(int maxLength) {

        if(this.getValue().length() > maxLength) {
            throw new ValidationException(this.getField() + " cannot be longer than " + maxLength + " characters");
        }

        return this;
    }

    public StringFluentValidator minLength(int minLength) {
        if(this.getValue().length() < minLength) {
            throw new ValidationException(this.getField() + " cannot be less than " + minLength + " characters");
        }
        return this;
    }

    public StringFluentValidator matchesRegex(String regex) {
        if(!this.getValue().matches(regex)) {
            throw new ValidationException(this.getField() + " does not match the required format");
        }
        return this;
    }
}
