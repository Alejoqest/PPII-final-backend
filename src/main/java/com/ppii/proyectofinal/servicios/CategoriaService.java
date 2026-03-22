package com.ppii.proyectofinal.servicios;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.ppii.proyectofinal.entidades.Categoria;
import com.ppii.proyectofinal.excepcion.RecursoNoEncontradoExcepcion;
import com.ppii.proyectofinal.repositorios.CategoriaRepository;

@Service
public class CategoriaService {
	
	@Autowired
	private CategoriaRepository repository;
	
	public List<Categoria> obtenerCategorias() {
		return repository.findAll();
	}
	
	public List<Categoria> buscarCategorias(String lista, int numPag) {
		Pageable paginaMuestra = PageRequest.of((numPag - 1), 10);
		
		List<String> excepciones = Stream.of(lista.split(",", -1)).collect(Collectors.toList());
		
		String categoria = excepciones.get((excepciones.size() - 1));
		
		excepciones.remove(categoria);
		
		return repository.findAllByNombreContainingAndNombreNotIn(categoria, excepciones, paginaMuestra);
	}
	
	public void eliminarCategorias(Long id) {
		if (repository.existsById(id)) throw new RecursoNoEncontradoExcepcion("Categoria", "Id", Long.toString(id));
		
		repository.deleteById(id);
	}
}
