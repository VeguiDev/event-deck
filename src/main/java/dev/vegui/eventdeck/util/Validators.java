package dev.vegui.eventdeck.util;

import dev.vegui.eventdeck.validation.FluentValidator;
import dev.vegui.eventdeck.validation.StringFluentValidator;

public class Validators {

    static public StringFluentValidator field(String field, String value) {
        return new StringFluentValidator(field, value);
    }

    static public <T> FluentValidator<T> field(String field, T value) {
        return  new FluentValidator<>(field, value);
    }

}
