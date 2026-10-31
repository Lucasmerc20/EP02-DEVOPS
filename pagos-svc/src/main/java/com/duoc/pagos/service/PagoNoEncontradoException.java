package com.duoc.pagos.service;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class PagoNoEncontradoException extends RuntimeException {

	public PagoNoEncontradoException(Long id) {
		super("Pago no encontrado con id " + id);
	}

}