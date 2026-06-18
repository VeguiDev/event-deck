package dev.vegui.eventdeck.validation;

import dev.vegui.eventdeck.exceptions.ValidationException;

public class StringFluentValidator extends FluentValidator<String> {
    private static final String EMAIL_REGEX =
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";

    public static final String PERSON_NAME =
            "^[\\p{L}' -]{2,80}$";

    public StringFluentValidator(String field, String value) {
        super(field, value);
    }

    @Override
    public StringFluentValidator notEmpty() {
        super.notEmpty();
        if (this.getValue().isEmpty()) {
            throw new ValidationException(this.getField() + " cannot be empty");
        }

        return this;
    }

    public StringFluentValidator maxLength(int maxLength) {

        if (this.getValue().length() > maxLength) {
            throw new ValidationException(this.getField() + " cannot be longer than " + maxLength + " characters");
        }

        return this;
    }

    public StringFluentValidator email() {

        this.matchesRegex(EMAIL_REGEX);

        return this;
    }

    public StringFluentValidator personName() {
        this.matchesRegex(PERSON_NAME);
        return this;
    }

    public StringFluentValidator minLength(int minLength) {
        if (this.getValue().length() < minLength) {
            throw new ValidationException(this.getField() + " cannot be less than " + minLength + " characters");
        }
        return this;
    }

    public StringFluentValidator matchesRegex(String regex) {
        if (!this.getValue().matches(regex)) {
            throw new ValidationException(this.getField() + " does not match the required format");
        }
        return this;
    }
}
