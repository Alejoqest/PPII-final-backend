package com.ppii.proyectofinal.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CarroComprasDTO {
	private Long id;
	private double precioTotal;
	private int cantidadDeElementos;
}
