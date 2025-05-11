package com.batbatcar.controllers;

import com.batbatcar.exceptions.ReservaAlreadyExistsException;
import com.batbatcar.exceptions.ReservaNoValidaException;
import com.batbatcar.exceptions.ReservaNotFoundException;
import com.batbatcar.exceptions.ViajeNotFoundException;
import com.batbatcar.modelo.dto.Reserva;
import com.batbatcar.modelo.dto.viaje.Viaje;
import com.batbatcar.modelo.repositories.ViajesRepository;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ReservaController {

	@Autowired
	private ViajesRepository viajesRepository;

	@GetMapping("/viaje/reserva/add")
	public String reservaFormActionView(@RequestParam(required = false) Integer codViaje, Model model,
			RedirectAttributes redirectAttributes) {
		if (codViaje == null) {
			return "redirect:/viajes";
		} else {
			try {
				viajesRepository.getViajeByCodigo(codViaje);
				model.addAttribute("codViaje", codViaje);
				return "reserva/reserva_form";
			} catch (ViajeNotFoundException e) {
				redirectAttributes.addFlashAttribute("errores", "El viaje no se ha encontrado.");
				return "redirect:/viajes";
			}
		}
	}

	@PostMapping("/viaje/reserva/add")
	public String insertarReserva(@RequestParam HashMap<String, String> params, RedirectAttributes redirectAttributes) {
		String usuario = params.get("usuario");
		String plazasSolicitadasString = params.get("plazasSolicitadas");
		int plazasSolicitadas = Integer.parseInt(plazasSolicitadasString);
		String codViajeString = params.get("codViaje");
		int codViaje = Integer.parseInt(codViajeString);
		try {
			Viaje viaje = viajesRepository.findViajeSiPermiteReserva(codViaje, usuario, plazasSolicitadas);
			String codReserva = viajesRepository.getNextCodReserva(viaje);
			Reserva reserva = new Reserva(codReserva, usuario, plazasSolicitadas, viaje);
			viajesRepository.save(reserva);
			redirectAttributes.addFlashAttribute("infoMensaje", "Reserva añadida con éxito");
			return "redirect:/viajes";
		} catch (ReservaAlreadyExistsException | ReservaNoValidaException | ViajeNotFoundException
				| ReservaNotFoundException ex) {
			HashMap<String, String> errores = new HashMap<>();
			errores.put("Error", ex.getMessage());
			redirectAttributes.addFlashAttribute("errores", errores);
			return "redirect:/viaje/reserva/add?codViaje=" + codViaje;
		}
	}

	@GetMapping("/viaje/reservas")
	public String reservasDeUnViaje(@RequestParam(required = false) Integer codViaje, Model model,
			RedirectAttributes redirectAttributes) {
		if (codViaje == null) {
			return "redirect:/viajes";
		} else {
			try {
				Viaje viaje = viajesRepository.getViajeByCodigo(codViaje);
				List<Reserva> reservas = viajesRepository.findReservasByViaje(viaje);
				model.addAttribute("reservas", reservas);
				model.addAttribute("codViaje", codViaje);
				return "reserva/listado";
			} catch (ViajeNotFoundException ex) {
				redirectAttributes.addFlashAttribute("errores", ex.getMessage());
				return "redirect:/viajes";
			}
		}
	}

	@GetMapping("/viaje/reserva")
	public String detalleReserva(@RequestParam String codReserva, Model model, RedirectAttributes redirectAttributes) {
		try {
			Reserva reserva = viajesRepository.getReservaById(codReserva);
			model.addAttribute("reserva", reserva);
			return "reserva/reserva_detalle";
		} catch (ReservaNotFoundException ex) {
			redirectAttributes.addFlashAttribute("errores", "La reserva no se ha encontrado.");
			return "redirect:/viajes";
		}
	}

	@GetMapping("/viaje/reserva/del")
	public String eliminarReserva(@RequestParam String codReserva, Model model, RedirectAttributes redirectAttributes) {
		try {
			Reserva reserva = viajesRepository.getReservaById(codReserva);
			viajesRepository.remove(reserva);
			redirectAttributes.addFlashAttribute("infoMensaje", "La reserva ha sido cancelada exitosamente.");
			return "redirect:/viaje?codViaje=" + reserva.getViaje().getCodViaje();
		} catch (ReservaNotFoundException ex) {
			redirectAttributes.addFlashAttribute("errores", "La reserva no se ha encontrado.");
			return "redirect:/viajes";
		}
	}

}
