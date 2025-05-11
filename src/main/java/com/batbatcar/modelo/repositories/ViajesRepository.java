package com.batbatcar.modelo.repositories;

import com.batbatcar.exceptions.ReservaAlreadyExistsException;
import com.batbatcar.exceptions.ReservaNoValidaException;
import com.batbatcar.exceptions.ReservaNotFoundException;
import com.batbatcar.exceptions.ViajeAlreadyExistsException;
import com.batbatcar.exceptions.ViajeNotCancelableException;
import com.batbatcar.exceptions.ViajeNotFoundException;
import com.batbatcar.modelo.dao.interfaces.ReservaDAO;
import com.batbatcar.modelo.dao.interfaces.ViajeDAO;
import com.batbatcar.modelo.dao.sqldao.SQLReservaDAO;
import com.batbatcar.modelo.dao.sqldao.SQLViajeDAO;
import com.batbatcar.modelo.dto.Reserva;
import com.batbatcar.modelo.dto.viaje.Viaje;
import com.batbatcar.utils.Validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public class ViajesRepository {

    private final ViajeDAO viajeDAO;
    private final ReservaDAO reservaDAO;

    public ViajesRepository(@Autowired SQLViajeDAO viajeDAO, @Autowired SQLReservaDAO reservaDAO) {
        this.viajeDAO = viajeDAO;
        this.reservaDAO = reservaDAO;
    }
    
    /** 
     * Obtiene un conjunto de todos los viajes
     * @return
     */
    public Set<Viaje> findAll() {
        Set<Viaje> viajes = viajeDAO.findAll();
        for (Viaje viaje : viajes) {
            if (this.reservaDAO.findAllByTravel(viaje).size() > 0) {
                viaje.setSeHanRealizadoReservas(true);
            }
        }
        return viajes;
    }
    
    /**
     * Obtiene el código del siguiente viaje
     * @return
     */
    public int getNextCodViaje() {
        return this.viajeDAO.findAll().size() + 1;
    }
    
    public String getNextCodReserva(Viaje viaje) {
        List<Reserva> reservas = findReservasByViaje(viaje);
        return String.format(viaje.getCodViaje() + "-" + "%d", reservas.size() + 1);
    }
    
    /**
     * Guarda el viaje (actualiza si ya existe o añade si no existe)
     * @param viaje
     * @throws ViajeAlreadyExistsException
     * @throws ViajeNotFoundException
     */
    public void save(Viaje viaje) throws ViajeAlreadyExistsException, ViajeNotFoundException {
        if (viajeDAO.findById(viaje.getCodViaje()) == null) {
            viajeDAO.add(viaje);
        } else {
            viajeDAO.update(viaje);
        }
    }
    
    /**
     * Encuentra todas las reservas de @viaje
     * @param viaje
     * @return
     */
    public List<Reserva> findReservasByViaje(Viaje viaje) {
        return reservaDAO.findAllByTravel(viaje);
    }
    
    /**
     * Guarda la reserva
     * @param reserva
     * @throws ReservaAlreadyExistsException
     * @throws ReservaNotFoundException
     */
    public void save(Reserva reserva) throws ReservaAlreadyExistsException, ReservaNotFoundException {
        if (reservaDAO.findById(reserva.getCodigoReserva()) == null) {
            reservaDAO.add(reserva);
        } else {
            reservaDAO.update(reserva);
        }
    }
    
    /**
     * Elimina la reserva
     * @param reserva
     * @throws ReservaNotFoundException
     */
    public void remove(Reserva reserva) throws ReservaNotFoundException {
        reservaDAO.remove(reserva);
    }
    
    public Viaje findViajeByCodigo(int codViaje) {
        return viajeDAO.findById(codViaje);
    }
    
    public Viaje getViajeByCodigo(int codViaje) throws ViajeNotFoundException {
        return viajeDAO.getById(codViaje);
    }
    
    public Set<Viaje> getViajesByCity(String destino) {
        return viajeDAO.findAll(destino);
    }
    
    public Viaje findViajeSiPermiteReserva(int codViaje, String usuario, int plazasSolicitadas) throws ViajeNotFoundException, ReservaNoValidaException {
        Viaje viaje = viajeDAO.getById(codViaje);
        if (viaje.getPropietario().equals(usuario)) {
            throw new ReservaNoValidaException("El usuario que reserva no puede ser el mismo que el propietario.");
        }
        
        if (viaje.isCerrado()) {
            throw new ReservaNoValidaException("El viaje ya ha salido o está cerrado.");
        }

        if (viaje.isCancelado()) {
            throw new ReservaNoValidaException("El viaje está cancelado.");
        }

        int plazasReservadas = reservaDAO.getNumPlazasReservadasEnViaje(viaje);
        if (plazasReservadas + plazasSolicitadas > viaje.getPlazasOfertadas()) {
            throw new ReservaNoValidaException("No hay suficiente plazas disponibles.");
        }
       
        if (!Validator.isValidPlazasOfertadas(plazasSolicitadas)) {
            throw new ReservaNoValidaException("Puedes reservar un mínimo de 1 plazas y un máximo de 6.");
        }
        
        Reserva reservaExistente = reservaDAO.findByUserInTravel(usuario, viaje);
        if (reservaExistente != null) {
            throw new ReservaNoValidaException("El usuario ya ha realizado una reserva en este viaje.");
        }
        
        return viaje;
    }

    public int getPlazasReservadasByViaje(int codViaje) throws ViajeNotFoundException {
        Viaje viaje = viajeDAO.getById(codViaje);
        List<Reserva> reservas = reservaDAO.findAllByTravel(viaje);
        int plazasReservadas = 0;
        for (Reserva reserva : reservas) {
            plazasReservadas += reserva.getPlazasSolicitadas();
        }
        return plazasReservadas;
    }

    public int getNumeroReservasByViaje(Viaje viaje) {
        List<Reserva> reservas = reservaDAO.findAllByTravel(viaje);
        return reservas.size();
    }
    
    public void cancelarViaje(int codViaje) throws ViajeNotFoundException, ViajeNotCancelableException {
        Viaje viaje = viajeDAO.findById(codViaje);
        if (viaje == null) {
            throw new ViajeNotFoundException("El viaje con código " + codViaje + " no se ha encuentrado.");
        }
        viaje.cancelar();
        viajeDAO.update(viaje);
    }
    
    public List<Reserva> findAllByTravel(Viaje viaje) {
    	return reservaDAO.findAllByTravel(viaje);
    }
    
    
    public Reserva getReservaById(String id) throws ReservaNotFoundException {
    	return reservaDAO.getById(id);
    }
}
