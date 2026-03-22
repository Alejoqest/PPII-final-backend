package com.ppii.proyectofinal.dto;

import java.util.List;

import com.ppii.proyectofinal.entidades.Categoria;
import com.ppii.proyectofinal.entidades.FormatoPelicula;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PeliculaCreacionDTO {
	private long id;
	private String nombre;
	private int stock;
	private double precio;
	private int ano;
	private String descripcion;
	private FormatoPelicula formato;
	private List<Categoria> categorias;
}