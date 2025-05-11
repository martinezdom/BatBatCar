package com.batbatcar.exceptions;

public class ViajeNotCancelableException extends Exception{

    public ViajeNotCancelableException(String codViaje) {
        super("El viaje " + codViaje + " no permite ser cancelado");
    }
}
