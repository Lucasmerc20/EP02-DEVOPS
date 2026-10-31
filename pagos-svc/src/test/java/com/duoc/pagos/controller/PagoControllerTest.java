package com.duoc.pagos.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.hasSize;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.duoc.pagos.model.Pago;
import com.duoc.pagos.repository.PagoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
class PagoControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private PagoRepository repository;

	@Autowired
	private ObjectMapper objectMapper;

	@BeforeEach
	void limpiarBaseDeDatos() {
		repository.deleteAll();
	}

	@Test
	void listarCuandoNoHayPagosDevuelveListaVacia() throws Exception {
		mockMvc.perform(get("/api/pagos"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(0)));
	}

	@Test
	void crearPagoElCreaYQuedaDisponibleEnElListado() throws Exception {
		String body = objectMapper.writeValueAsString(
				new Pago("Entrada VIP", "ORD-1001", new BigDecimal("45000"), "APROBADO"));

		mockMvc.perform(post("/api/pagos")
						.contentType(MediaType.APPLICATION_JSON)
						.content(body))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id").isNumber())
				.andExpect(jsonPath("$.nombre").value("Entrada VIP"))
				.andExpect(jsonPath("$.orden").value("ORD-1001"))
				.andExpect(jsonPath("$.monto").value(45000))
				.andExpect(jsonPath("$.estado").value("APROBADO"));

		mockMvc.perform(get("/api/pagos"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(1)));
	}

	@Test
	void crearPagoInvalidoDevuelveBadRequest() throws Exception {
		String body = "{\"nombre\":\"\",\"orden\":\"ORD-1\",\"monto\":45000,\"estado\":\"APROBADO\"}";

		mockMvc.perform(post("/api/pagos")
						.contentType(MediaType.APPLICATION_JSON)
						.content(body))
				.andExpect(status().isBadRequest());
	}

	@Test
	void obtenerPagoPorIdDevuelveElPago() throws Exception {
		Pago guardado = repository.save(
				new Pago("Pago B", "ORD-2", new BigDecimal("1000"), "PENDIENTE"));

		mockMvc.perform(get("/api/pagos/{id}", guardado.getId()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(guardado.getId().intValue()))
				.andExpect(jsonPath("$.nombre").value("Pago B"))
				.andExpect(jsonPath("$.estado").value("PENDIENTE"));
	}

	@Test
	void obtenerPagoInexistenteDevuelveNotFound() throws Exception {
		mockMvc.perform(get("/api/pagos/999"))
				.andExpect(status().isNotFound());
	}

	@Test
	void eliminarPagoLoBorraDelSistema() throws Exception {
		Pago guardado = repository.save(
				new Pago("Pago C", "ORD-3", new BigDecimal("2000"), "APROBADO"));

		mockMvc.perform(delete("/api/pagos/{id}", guardado.getId()))
				.andExpect(status().isNoContent());

		mockMvc.perform(get("/api/pagos"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(0)));
	}

	@Test
	void eliminarPagoInexistenteDevuelveNotFound() throws Exception {
		mockMvc.perform(delete("/api/pagos/999"))
				.andExpect(status().isNotFound());
	}

}