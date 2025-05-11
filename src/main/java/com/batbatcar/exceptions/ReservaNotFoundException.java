package com.batbatcar.exceptions;

public class ReservaNotFoundException extends Exception {

    public ReservaNotFoundException(String codReserva) {
        super("La reserva con código " + codReserva + " no ha sido encontrada");
    }

}
