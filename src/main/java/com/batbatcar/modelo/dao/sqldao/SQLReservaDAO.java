package com.batbatcar.modelo.dao.sqldao;

import com.batbatcar.exceptions.DatabaseConnectionException;
import com.batbatcar.exceptions.ReservaAlreadyExistsException;
import com.batbatcar.exceptions.ReservaNotFoundException;
import com.batbatcar.modelo.dto.Reserva;
import com.batbatcar.modelo.dto.viaje.Viaje;
import com.batbatcar.modelo.services.MySQLConnection;
import com.batbatcar.modelo.dao.interfaces.ReservaDAO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Repository
public class SQLReservaDAO implements ReservaDAO {

	@Autowired
	private MySQLConnection mySQLConnection;

	@Override
	public Set<Reserva> findAll() {
		String sql = "SELECT * FROM reservas";
		try (Connection connection = mySQLConnection.getConnection();
				Statement st = connection.createStatement();
				ResultSet rs = st.executeQuery(sql)) {
			Set<Reserva> reservas = new HashSet<>();
			while (rs.next()) {
				reservas.add(mapToReserva(rs));
			}
			return reservas;
		} catch (SQLException ex) {
			throw new DatabaseConnectionException("Error al conectarse con la base de datos");
		}
	}

	@Override
	public Reserva findById(String id) {
		String sql = "SELECT * FROM reservas WHERE codigoReserva= ?";
		try (Connection connection = mySQLConnection.getConnection();
				PreparedStatement ps = connection.prepareStatement(sql)) {
			ps.setString(1, id);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				return mapToReserva(rs);
			} else {
				return null;
			}
		} catch (SQLException ex) {
			throw new DatabaseConnectionException("Error al conectarse con la base de datos");
		}
	}

	@Override
	public ArrayList<Reserva> findAllByUser(String user) {
		String sql = "SELECT * FROM reservas WHERE usuario = ?";
		try (Connection connection = mySQLConnection.getConnection();
				PreparedStatement ps = connection.prepareStatement(sql)) {
			ps.setString(1, user);
			ResultSet rs = ps.executeQuery();
			ArrayList<Reserva> reservas = new ArrayList<>();
			while (rs.next()) {
				reservas.add(mapToReserva(rs));
			}
			return reservas;

		} catch (SQLException ex) {
			throw new DatabaseConnectionException("Error al conectarse con la base de datos");
		}
	}

	@Override
	public List<Reserva> findAllByTravel(Viaje viaje) {
		String sql = "SELECT * FROM reservas WHERE viaje = ?";
		try (Connection connection = mySQLConnection.getConnection();
				PreparedStatement ps = connection.prepareStatement(sql)) {
			ps.setInt(1, viaje.getCodViaje());
			ResultSet rs = ps.executeQuery();
			ArrayList<Reserva> reservas = new ArrayList<>();
			while (rs.next()) {
				reservas.add(mapToReserva(rs));
			}
			return reservas;
		} catch (SQLException ex) {
			throw new DatabaseConnectionException("Error al conectarse con la base de datos");
		}
	}

	@Override
	public Reserva getById(String id) throws ReservaNotFoundException {
		Reserva reserva = findById(id);
		if (reserva == null) {
			throw new ReservaNotFoundException("Reserva no encontrada");
		}
		return reserva;
	}

	@Override
	public List<Reserva> findAllBySearchParams(Viaje viaje, String searchParams) {
		throw new RuntimeException("Not yet implemented");
	}

	@Override
	public void add(Reserva reserva) throws ReservaAlreadyExistsException {
		String sql = "INSERT INTO reservas (codigoReserva, usuario, plazasSolicitadas, fechaRealizacion, viaje) VALUES (?, ?, ?, ?, ?)";
		try (Connection connection = mySQLConnection.getConnection();
				PreparedStatement ps = connection.prepareStatement(sql)) {
			ps.setString(1, reserva.getCodigoReserva());
			ps.setString(2, reserva.getUsuario());
			ps.setInt(3, reserva.getPlazasSolicitadas());
			ps.setTimestamp(4, Timestamp.valueOf(reserva.getFechaRealizacion()));
			ps.setInt(5, reserva.getViaje().getCodViaje());
			ps.executeUpdate();
		} catch (SQLException ex) {
			throw new DatabaseConnectionException("Error al conectarse con la base de datos");
		}
	}

	@Override
	public void update(Reserva reserva) throws ReservaNotFoundException {
		String sql = "UPDATE reservas SET usuario = ?, plazasSolicitadas = ?, fechaRealizacion = ?, viaje = ? WHERE codigoReserva = ?";
		try (Connection connection = mySQLConnection.getConnection();
				PreparedStatement ps = connection.prepareStatement(sql)) {
			ps.setString(1, reserva.getUsuario());
			ps.setInt(2, reserva.getPlazasSolicitadas());
			ps.setTimestamp(3, Timestamp.valueOf(reserva.getFechaRealizacion()));
			ps.setInt(4, reserva.getViaje().getCodViaje());
			ps.setString(5, reserva.getCodigoReserva());
			int filasAfectadas = ps.executeUpdate();
			if (filasAfectadas == 0) {
				throw new ReservaNotFoundException(
						"La reserva con código: " + reserva.getCodigoReserva() + " no ha sido encontrada.");
			}
		} catch (SQLException ex) {
			throw new DatabaseConnectionException("Error al conectarse con la base de datos");
		}
	}

	@Override
	public void remove(Reserva reserva) throws ReservaNotFoundException {
		String sql = "DELETE FROM reservas WHERE codigoReserva = ?";
		try (Connection connection = mySQLConnection.getConnection();
				PreparedStatement ps = connection.prepareStatement(sql)) {
			ps.setString(1, reserva.getCodigoReserva());
			int filasAfectadas = ps.executeUpdate();
			if (filasAfectadas == 0) {
				throw new ReservaNotFoundException(
						"La reserva con código: " + reserva.getCodigoReserva() + " no ha sido encontrada.");
			}
		} catch (SQLException ex) {
			throw new DatabaseConnectionException("Error al conectarse con la base de datos");
		}
	}

	@Override
	public int getNumPlazasReservadasEnViaje(Viaje viaje) {
		String sql = "SELECT SUM(plazasSolicitadas) AS totalPlazasReservadas FROM reservas WHERE viaje = ?";
		try (Connection connection = mySQLConnection.getConnection();
				PreparedStatement ps = connection.prepareStatement(sql)) {
			ps.setInt(1, viaje.getCodViaje());
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getInt("totalPlazasReservadas");
			} else {
				return 0;
			}
		} catch (SQLException ex) {
			throw new DatabaseConnectionException("Error al conectarse con la base de datos");
		}
	}

	@Override
	public Reserva findByUserInTravel(String usuario, Viaje viaje) {
		String sql = "SELECT * FROM reservas WHERE usuario = ? AND viaje = ?";
		try (Connection connection = mySQLConnection.getConnection();
				PreparedStatement ps = connection.prepareStatement(sql)) {
			ps.setString(1, usuario);
			ps.setInt(2, viaje.getCodViaje());
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				return mapToReserva(rs);
			} else {
				return null;
			}
		} catch (SQLException ex) {
			throw new DatabaseConnectionException("Error al conectarse con la base de datos");
		}
	}

	private Reserva mapToReserva(ResultSet rs) throws SQLException {
	    String codigoReserva = rs.getString("codigoReserva");
	    String usuario = rs.getString("usuario");
	    int plazasSolicitadas = rs.getInt("plazasSolicitadas");
	    LocalDateTime fechaRealizacion = rs.getTimestamp("fechaRealizacion").toLocalDateTime();
	    int codViaje = rs.getInt("viaje");
	    Viaje viaje = new Viaje(codViaje);
	    return new Reserva(codigoReserva, usuario, plazasSolicitadas, fechaRealizacion, viaje);
	}

}
