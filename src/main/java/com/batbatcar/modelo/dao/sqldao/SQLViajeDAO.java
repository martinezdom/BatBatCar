package com.batbatcar.modelo.dao.sqldao;

import com.batbatcar.exceptions.DatabaseConnectionException;
import com.batbatcar.exceptions.ViajeNotFoundException;
import com.batbatcar.modelo.services.MySQLConnection;
import com.batbatcar.modelo.dao.interfaces.ViajeDAO;
import com.batbatcar.modelo.dto.viaje.Viaje;
import com.batbatcar.modelo.dto.viaje.EstadoViaje;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.HashSet;

@Repository
public class SQLViajeDAO implements ViajeDAO {

	@Autowired
	private MySQLConnection mySQLConnection;

	@Override
	public Set<Viaje> findAll() {
		String sql = "SELECT * FROM viajes";
		try (Connection connection = mySQLConnection.getConnection();
				Statement st = connection.createStatement();
				ResultSet rs = st.executeQuery(sql)) {
			Set<Viaje> viajes = new HashSet<>();
			while (rs.next()) {
				viajes.add(mapToViaje(rs));
			}
			return viajes;
		} catch (SQLException ex) {
			throw new DatabaseConnectionException("Error al conectarse con la base de datos");
		}
	}

	@Override
	public Set<Viaje> findAll(String city) {
		String sql = "SELECT * FROM viajes WHERE ruta LIKE ?";
		try (Connection connection = mySQLConnection.getConnection();
				PreparedStatement ps = connection.prepareStatement(sql)) {
			ps.setString(1, "%-" + city + "%");
			ResultSet rs = ps.executeQuery();
			Set<Viaje> viajes = new HashSet<>();
			while (rs.next()) {
				viajes.add(mapToViaje(rs));
			}
			return viajes;

		} catch (SQLException ex) {
			throw new DatabaseConnectionException("Error al conectarse con la base de datos");
		}
	}

	@Override
	public Set<Viaje> findAll(EstadoViaje estadoViaje) {
		String sql = "SELECT * FROM viajes WHERE estadoViaje = ?";
		try (Connection connection = mySQLConnection.getConnection();
				PreparedStatement ps = connection.prepareStatement(sql)) {
			String estadoViajeString = estadoViaje.toString();
			ps.setString(1, estadoViajeString);
			ResultSet rs = ps.executeQuery();
			Set<Viaje> viajes = new HashSet<>();
			while (rs.next()) {
				viajes.add(mapToViaje(rs));
			}
			return viajes;

		} catch (SQLException ex) {
			throw new DatabaseConnectionException("Error al conectarse con la base de datos");
		}
	}

	@Override
	public Set<Viaje> findAll(Class<? extends Viaje> viajeClass) {
		throw new RuntimeException("Not yet implemented");
	}

	@Override
	public Viaje findById(int codViaje) {
		String sql = "SELECT * FROM viajes WHERE codViaje = ?";
		try (Connection connection = mySQLConnection.getConnection();
				PreparedStatement ps = connection.prepareStatement(sql)) {
			ps.setInt(1, codViaje);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				return mapToViaje(rs);
			} else {
				return null;
			}
		} catch (SQLException ex) {
			throw new DatabaseConnectionException("Error al conectarse con la base de datos");
		}
	}

	@Override
	public Viaje getById(int codViaje) throws ViajeNotFoundException {
		Viaje viaje = findById(codViaje);
		if (viaje == null) {
			throw new ViajeNotFoundException("Viaje no encontrado");
		}
		return viaje;
	}

	@Override
	public void add(Viaje viaje) {
		String sql = "INSERT INTO viajes (propietario, ruta, fechaSalida, duracion, precio, plazasOfertadas, estadoViaje) VALUES (?, ?, ?, ?, ?, ?, ?)";
		try (Connection connection = mySQLConnection.getConnection();
				PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			ps.setString(1, viaje.getPropietario());
			ps.setString(2, viaje.getRuta());
			ps.setTimestamp(3, Timestamp.valueOf(viaje.getFechaSalida()));
			ps.setLong(4, viaje.getDuracion());
			ps.setFloat(5, viaje.getPrecio());
			ps.setInt(6, viaje.getPlazasOfertadas());
			ps.setString(7, viaje.getEstado().toString());
			ps.executeUpdate();
			ResultSet rs = ps.getGeneratedKeys();
			if (rs.next()) {
				int autoIncremental = rs.getInt(1);
				viaje.setCodViaje(autoIncremental);
			}
		} catch (SQLException ex) {
			throw new DatabaseConnectionException("Error al conectarse con la base de datos");
		}
	}

	@Override
	public void update(Viaje viaje) throws ViajeNotFoundException {
		String sql = "UPDATE viajes SET propietario = ?, ruta = ?, fechaSalida = ?, duracion = ?, precio = ?, plazasOfertadas = ?, estadoViaje = ? WHERE codViaje = ?";
		try (Connection connection = mySQLConnection.getConnection();
				PreparedStatement ps = connection.prepareStatement(sql)) {
			ps.setString(1, viaje.getPropietario());
			ps.setString(2, viaje.getRuta());
			ps.setTimestamp(3, Timestamp.valueOf(viaje.getFechaSalida()));
			ps.setLong(4, viaje.getDuracion());
			ps.setFloat(5, viaje.getPrecio());
			ps.setInt(6, viaje.getPlazasOfertadas());
			ps.setString(7, viaje.getEstado().toString());
			ps.setInt(8, viaje.getCodViaje());
			int filasAfectadas = ps.executeUpdate();
			if (filasAfectadas == 0) {
				throw new ViajeNotFoundException("El viaje con id: " + viaje.getCodViaje() + " no ha sido encontrado.");
			}
		} catch (SQLException ex) {
			throw new DatabaseConnectionException("Error al conectarse con la base de datos");
		}
	}

	@Override
	public void remove(Viaje viaje) throws ViajeNotFoundException {
		String sql = "DELETE FROM viajes WHERE codViaje = ?";
		try (Connection connection = mySQLConnection.getConnection();
				PreparedStatement ps = connection.prepareStatement(sql)) {
			ps.setInt(1, viaje.getCodViaje());
			int filasAfectadas = ps.executeUpdate();
			if (filasAfectadas == 0) {
				throw new ViajeNotFoundException("El viaje con id: " + viaje.getCodViaje() + " no ha sido encontrado.");
			}
		} catch (SQLException ex) {
			throw new DatabaseConnectionException("Error al conectarse con la base de datos");
		}
	}

	private Viaje mapToViaje(ResultSet rs) throws SQLException {
		int cod = rs.getInt("codViaje");
		String propietario = rs.getString("propietario");
		String ruta = rs.getString("ruta");
		LocalDateTime fechaSalida = rs.getTimestamp("fechaSalida").toLocalDateTime();
		int duracion = rs.getInt("duracion");
		Float precio = rs.getFloat("precio");
		int plazasOfertadas = rs.getInt("plazasOfertadas");
		String estadoViajeString = rs.getString("estadoViaje");
		EstadoViaje estadoViaje = EstadoViaje.valueOf(estadoViajeString);
		Viaje viaje = new Viaje(cod, propietario, ruta, fechaSalida, duracion, precio, plazasOfertadas, estadoViaje);
		return viaje;
	}
}
