package com.duoc.pagos.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.duoc.pagos.model.Pago;
import com.duoc.pagos.repository.PagoRepository;

@Service
public class PagoService {

	private final PagoRepository repository;

	public PagoService(PagoRepository repository) {
		this.repository = repository;
	}

	public List<Pago> listar() {
		return repository.findAll();
	}

	public Pago crear(Pago pago) {
		return repository.save(pago);
	}

	public Pago obtenerPorId(Long id) {
		return repository.findById(id)
				.orElseThrow(() -> new PagoNoEncontradoException(id));
	}

	public void eliminar(Long id) {
		if (!repository.existsById(id)) {
			throw new PagoNoEncontradoException(id);
		}
		repository.deleteById(id);
	}

}