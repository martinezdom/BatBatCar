package com.batbatcar.controllers;

import com.batbatcar.exceptions.ViajeAlreadyExistsException;
import com.batbatcar.exceptions.ViajeNotCancelableException;
import com.batbatcar.exceptions.ViajeNotFoundException;
import com.batbatcar.modelo.dto.viaje.Viaje;
import com.batbatcar.modelo.dto.Reserva;
import com.batbatcar.modelo.repositories.ViajesRepository;
import com.batbatcar.utils.Validator;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ViajesController {

	@Autowired
	private ViajesRepository viajesRepository;

	/**
	 * Endpoint que muestra el listado de todos los viajes disponibles
	 * 
	 * @throws ViajeNotFoundException
	 *
	 */
	@GetMapping("/")
	public String getViajesAction(@RequestParam(value = "destino", required = false) String destino, Model model)
			throws ViajeNotFoundException {
		Set<Viaje> viajes;
		if (destino != null) {
			viajes = viajesRepository.getViajesByCity(destino);
			model.addAttribute("destino", destino);
		} else {
			viajes = viajesRepository.findAll();
		}
		for (Viaje viaje : viajes) {
			int plazasReservadas = viajesRepository.getPlazasReservadasByViaje(viaje.getCodViaje());
			int plazasDisponibles = viaje.getPlazasOfertadas() - plazasReservadas;
			viaje.setPlazasDisponibles(plazasDisponibles);

			int numeroReservas = viajesRepository.getNumeroReservasByViaje(viaje);
			viaje.setNumReservas(numeroReservas);
		}
		if (viajes.isEmpty()) {
			model.addAttribute("sinResultados", "No se han encontrado viajes");
		}
		model.addAttribute("viajes", viajes);
		model.addAttribute("titulo", "Listado de viajes");
		return "viaje/listado";
	}

	@GetMapping("/viaje/add")
	public String viajeFormActionView() {
		return "viaje/viaje_form";
	}

	@PostMapping(value = "/viaje/add")
	public String insertarViaje(@RequestParam Map<String, String> params, RedirectAttributes redirectAttributes) {
		HashMap<String, String> errores = new HashMap<>();
		String ruta = params.get("ruta");
		String plazasOfertadasString = params.get("plazasOfertadas");
		int plazasOfertadas = Integer.parseInt(plazasOfertadasString);
		String propietario = params.get("propietario");
		String precioString = params.get("precio");
		float precio = Float.parseFloat(precioString);
		String duracionString = params.get("duracion");
		int duracion = Integer.parseInt(duracionString);
		String fechaString = params.get("fecha");
		String horaString = params.get("hora");
		String fechaYHoraString = fechaString + ' ' + horaString;
		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalDate fecha = LocalDate.parse(fechaString, dateFormatter);
		DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
		LocalTime hora = LocalTime.parse(horaString, timeFormatter);
		LocalDateTime fechaYHora = LocalDateTime.of(fecha, hora);
		int codViaje = viajesRepository.getNextCodViaje();

		if (!Validator.isValidRuta(ruta)) {
			errores.put("Ruta",
					"La ruta debe tener el formato 'Origen-Destino' o 'Origen-Destino1-Destino2', sin espacios y con un solo guion entre cada parte.");
		}

		if (!Validator.isValidPlazasOfertadas(plazasOfertadas)) {
			errores.put("Plazas", "Las plazas ofertadas deben ser un número entre 1 y 6.");
		}

		if (!Validator.isValidPropietario(propietario)) {
			errores.put("Propietario",
					"El nombre del propietario debe tener el formato 'Nombre Apellido', con cada palabra comenzando con una letra mayúscula y separadas por un espacio.");
		}

		if (!Validator.isValidPrecio(precio)) {
			errores.put("Precio", "El precio debe ser un valor mayor a 0 y puede incluir decimales.");
		}

		if (!Validator.isValidDuracion(duracion)) {
			errores.put("Duracion", "La duración debe ser un valor mayor a 0.");
		}

		if (!Validator.isValidDate(fechaString)) {
			errores.put("Fecha", "La fecha debe tener el formato 'yyyy-MM-dd'.");
		}
		if (!Validator.isValidTime(horaString)) {
			errores.put("Hora", "La hora debe tener el formato 'HH:mm'.");
		}

		if (!Validator.isValidDateTime(fechaYHoraString)) {
			errores.put("Fecha completa", "La fecha y hora deben tener el formato 'yyyy-MM-dd HH:mm'.");
		}

		if (!Validator.isBeforeDateTime(fechaYHora)) {
			errores.put("Fecha salida", "El viaje no debe ser anterior al día de hoy.");
		}

		if (!errores.isEmpty()) {
			redirectAttributes.addFlashAttribute("errores", errores);
			return "redirect:/viaje/add";
		}

		try {
			Viaje viaje = new Viaje(codViaje, propietario, ruta, fechaYHora, duracion, precio, plazasOfertadas);
			viajesRepository.save(viaje);
			redirectAttributes.addFlashAttribute("infoMensaje", "Viaje añadido con éxito");
			return "redirect:/";
		} catch (ViajeAlreadyExistsException | ViajeNotFoundException ex) {
			errores.put("codigo", ex.getMessage());
			redirectAttributes.addFlashAttribute("errores", errores);
			return "redirect:/";
		}
	}

	@GetMapping("/viaje")
	public String detalleViaje(@RequestParam int codViaje, Model model, RedirectAttributes redirectAttributes) {
		try {
			Viaje viaje = viajesRepository.getViajeByCodigo(codViaje);
			List<Reserva> reservas = viajesRepository.findAllByTravel(viaje);
			model.addAttribute("viaje", viaje);
			model.addAttribute("reservas", reservas);
			return "viaje/viaje_detalle";
		} catch (ViajeNotFoundException ex) {
			redirectAttributes.addFlashAttribute("errores", "No se ha encontrado el viaje con código " + codViaje);
			return "redirect:/";
		}
	}

	@GetMapping("/viaje/cancel")
	public String cancelarViaje(@RequestParam int codViaje, RedirectAttributes redirectAttributes) {
		try {
			viajesRepository.cancelarViaje(codViaje);
			redirectAttributes.addFlashAttribute("infoMensaje", "El viaje ha sido cancelado exitosamente.");
		} catch (ViajeNotFoundException ex) {
			redirectAttributes.addFlashAttribute("errores", "El viaje no se ha encontrado.");
			return "redirect:/";
		} catch (ViajeNotCancelableException ex) {
			redirectAttributes.addFlashAttribute("errores", "El viaje no se puede cancelar.");
			return "redirect:/";
		}
		return "redirect:/";
	}

}
