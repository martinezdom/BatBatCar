package com.batbatcar.exceptions;

public class InvalidEstadoViajeException extends RuntimeException {
    public InvalidEstadoViajeException() {
        super("El estado de viaje asignado es incorrecto");
    }
}
