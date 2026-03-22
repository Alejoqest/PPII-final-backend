package com.ppii.proyectofinal.entidades;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class DetalleFactura {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	/*@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "factura_id", nullable = false, referencedColumnName = "id")
	private Factura factura;*/
	
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "pelicula_id", referencedColumnName = "id")
	private Pelicula pelicula;
	
	private double subTotal;
	
	private int unidades;
	
}
