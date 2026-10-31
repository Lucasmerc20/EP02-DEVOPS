package com.duoc.pagos.model;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Entity
@Table(name = "pagos")
public class Pago {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank(message = "El nombre es obligatorio")
	@Column(nullable = false)
	private String nombre;

	@NotBlank(message = "La orden es obligatoria")
	@Column(nullable = false)
	private String orden;

	@NotNull(message = "El monto es obligatorio")
	@Positive(message = "El monto debe ser positivo")
	@Column(nullable = false)
	private BigDecimal monto;

	@NotBlank(message = "El estado es obligatorio")
	@Column(nullable = false)
	private String estado;

	public Pago() {
	}

	public Pago(String nombre, String orden, BigDecimal monto, String estado) {
		this.nombre = nombre;
		this.orden = orden;
		this.monto = monto;
		this.estado = estado;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getOrden() {
		return orden;
	}

	public void setOrden(String orden) {
		this.orden = orden;
	}

	public BigDecimal getMonto() {
		return monto;
	}

	public void setMonto(BigDecimal monto) {
		this.monto = monto;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

}