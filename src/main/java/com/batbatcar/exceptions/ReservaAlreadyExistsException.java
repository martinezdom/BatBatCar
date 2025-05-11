package com.batbatcar.exceptions;

import com.batbatcar.modelo.dto.Reserva;

public class ReservaAlreadyExistsException extends Exception{
	public ReservaAlreadyExistsException(Reserva reserva) {
		super("La reserva con código " + reserva.getCodigoReserva() + " ya existe");
	}

}
