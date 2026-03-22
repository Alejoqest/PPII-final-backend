package com.ppii.proyectofinal.dto;

import com.ppii.proyectofinal.entidades.FormatoPelicula;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DetalleFacturaDTO {
	private Long id;
	private Long peliculaId;
	private String peliculaNombre;
	private FormatoPelicula peliculaFormato;
	private int unidades;
	private double subTotal;
}
