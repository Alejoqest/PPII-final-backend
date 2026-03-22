package com.ppii.proyectofinal.mappers;

import org.springframework.stereotype.Component;

import com.ppii.proyectofinal.dto.PeliculaBusquedaDTO;
import com.ppii.proyectofinal.dto.PeliculaCreacionDTO;
import com.ppii.proyectofinal.dto.PeliculaDetallesDTO;
import com.ppii.proyectofinal.entidades.Pelicula;
/*
 * Mapper de objetos que se usa para poder convertir filas de datos en DTOs y viceversa
 */
@Component
public class PeliculaMapper {
	public Pelicula toEntity(PeliculaCreacionDTO entity) {
		Pelicula pelicula = Pelicula.builder()
				.id(entity.getId())
				.nombre(entity.getNombre())
				.stock(entity.getStock())
				.precio(entity.getPrecio())
				.año(entity.getAno())
				.descripcion(entity.getDescripcion())
				.formato(entity.getFormato())
				.categorias(entity.getCategorias())
				.build();
		return pelicula;
	}
	
	public PeliculaBusquedaDTO toBusqueda(Pelicula entity) {
		PeliculaBusquedaDTO pelicula = PeliculaBusquedaDTO.builder()
				.id(entity.getId())
				.nombre(entity.getNombre())
				.precio(entity.getPrecio())
				.formato(entity.getFormato())
				.ano(entity.getAño())
				.build();
		return pelicula;
	}
	
	public PeliculaDetallesDTO toDetalles(Pelicula entity) {
		PeliculaDetallesDTO pelicula = PeliculaDetallesDTO.builder()
				.id(entity.getId())
				.nombre(entity.getNombre())
				.formato(entity.getFormato())
				.descripcion(entity.getDescripcion())
				.precio(entity.getPrecio())
				.ano(entity.getAño())
				.stock(entity.getStock())
				.build();
		return pelicula;
	}
	
}
