package com.batbatcar.modelo.dao.interfaces;

import com.batbatcar.exceptions.ViajeAlreadyExistsException;
import com.batbatcar.exceptions.ViajeNotFoundException;
import com.batbatcar.modelo.dto.viaje.EstadoViaje;
import com.batbatcar.modelo.dto.viaje.Viaje;

import java.util.Set;

public interface ViajeDAO {

    /**
     * Obtiene todos los viajes
     *
     * @return
     */
    Set<Viaje> findAll();

    /**
     * Obtiene todos los viajes con destino a @city
     *
     * @return
     */
    Set<Viaje> findAll(String city);

    /**
     * Obtiene todos los viajes con el estado @estadoViaje
     *
     * @return
     */
    Set<Viaje> findAll(EstadoViaje estadoViaje);

    /**
     * Obtiene todos los viajes de @Class
     *
     * @return
     */
    Set<Viaje> findAll(Class<? extends Viaje> viajeClass);

    /**
     * Obtiene el viaje cuyo codigo es @codViaje
     *
     * @return Viaje or null
     */
    Viaje findById(int codViaje);

    /**
     * Obtiene el viaje cuyo codigo es @codViaje
     *
     * @return Viaje or throws and Exception
     */
    Viaje getById(int codViaje) throws ViajeNotFoundException;

    /**
     * Inserta un nuevo @viaje si éste no existe
     *
     * @param viaje
     * @throws si el viaje ya existe
     * @return 
     */
    void add(Viaje viaje) throws ViajeAlreadyExistsException;
    
    /**
     * Actualiza un viaje determinado por el código de @viaje al resto de datos que contenga @viaje
     * @param viaje
     * @throws ViajeNotFoundException
     */
    void update(Viaje viaje) throws ViajeNotFoundException;

    /**
     * Elimina el viaje @viaje
     *
     * @param viaje
     * @throws ViajeNotFoundException
     * @return 
     */
    void remove(Viaje viaje) throws ViajeNotFoundException;

}
