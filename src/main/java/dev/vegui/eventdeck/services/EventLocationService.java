package dev.vegui.eventdeck.services;

import dev.vegui.eventdeck.model.EventLocation;
import dev.vegui.eventdeck.util.Validators;

public class EventLocationService {

    public static void validateEventLocation(
            EventLocation location
    ) {
        Validators.field("venueName",  location.getVenueName()).notEmpty().maxLength(255);
        Validators.field("country",  location.getCountry()).notEmpty().maxLength(255);
        Validators.field("province",  location.getProvince()).notEmpty().maxLength(255);
        Validators.field("city",  location.getCity()).notEmpty().maxLength(255);
        Validators.field("street",  location.getStreet()).notEmpty().maxLength(255);
    }

}
