package com.ppii.proyectofinal.dto;

import java.time.LocalDate;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FacturaDTO {
	private Long id;
	private double precioTotal;
	private LocalDate fechaFactura;
	private int cantidadDeDetalles;
}
