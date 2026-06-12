package dev.vegui.eventdeck.validation;


import dev.vegui.eventdeck.exceptions.ValidationException;

public class FluentValidator<T> {
    private String field;
    private T value;

    public FluentValidator(String field, T value) {
        this.field = field;
        this.value = value;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public T getValue() {
        return value;
    }

    public FluentValidator<T> notEmpty() {

        if (this.value == null) {
            throw new ValidationException(this.field + " cannot be null");
        }

        return this;
    }

}
