package com.duoc.pagos.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

class PagoTest {

	@Test
	void gettersYSettersActualizanLosValores() {
		Pago pago = new Pago();
		pago.setId(10L);
		pago.setNombre("Entrada VIP");
		pago.setOrden("ORD-1001");
		pago.setMonto(new BigDecimal("45000"));
		pago.setEstado("APROBADO");

		assertEquals(10L, pago.getId());
		assertEquals("Entrada VIP", pago.getNombre());
		assertEquals("ORD-1001", pago.getOrden());
		assertEquals(new BigDecimal("45000"), pago.getMonto());
		assertEquals("APROBADO", pago.getEstado());
	}

	@Test
	void constructorConArgumentosAsignaLosValoresYNoseElId() {
		Pago pago = new Pago("Entrada VIP", "ORD-1001", new BigDecimal("45000"), "PENDIENTE");

		assertNull(pago.getId());
		assertEquals("Entrada VIP", pago.getNombre());
		assertEquals("ORD-1001", pago.getOrden());
		assertEquals(new BigDecimal("45000"), pago.getMonto());
		assertEquals("PENDIENTE", pago.getEstado());
	}

}