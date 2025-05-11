package com.batbatcar.exceptions;

public class ViajeAlreadyExistsException extends Exception{
	
	public ViajeAlreadyExistsException(int codViaje) {
        super("El viaje con " + codViaje + " ya existe");
   }
}
