package com.duoc.pagos.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.duoc.pagos.model.Pago;
import com.duoc.pagos.service.PagoService;

@RestController
@RequestMapping("/api/pagos")
public class PagoController {

	private final PagoService service;

	public PagoController(PagoService service) {
		this.service = service;
	}

	@GetMapping
	public List<Pago> listar() {
		return service.listar();
	}

	@GetMapping("/{id}")
	public Pago obtener(@PathVariable Long id) {
		return service.obtenerPorId(id);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public Pago crear(@Valid @RequestBody Pago pago) {
		return service.crear(pago);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void eliminar(@PathVariable Long id) {
		service.eliminar(id);
	}

}