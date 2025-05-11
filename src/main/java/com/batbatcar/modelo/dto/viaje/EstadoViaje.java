package com.batbatcar.modelo.dto.viaje;

import com.batbatcar.exceptions.InvalidEstadoViajeException;

public enum EstadoViaje {

    ABIERTO, CERRADO, CANCELADO;

    public static EstadoViaje parse(String estadoViaje) {
        if (estadoViaje.equals("ABIERTO")) {
            return ABIERTO;
        } else if (estadoViaje.equals("CERRADO")) {
            return CERRADO;
        } else if (estadoViaje.equals("CANCELADO")) {
            return CANCELADO;
        } else {
            throw new InvalidEstadoViajeException();
        }
     }
}
