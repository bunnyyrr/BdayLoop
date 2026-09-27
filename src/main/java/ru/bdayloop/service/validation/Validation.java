package ru.bdayloop.service.validation;

import ru.bdayloop.exception.BadRequestException;

public class Validation {
    public static void requireText(String value, String message){
        if(value == null || value.isBlank()){
            throw new BadRequestException(message);
        }
    }

    public static void requireNotNull(Object value, String message){
        if(value== null){
            throw new BadRequestException(message);
        }
    }
}
