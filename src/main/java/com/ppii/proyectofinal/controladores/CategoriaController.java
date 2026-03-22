package com.ppii.proyectofinal.controladores;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ppii.proyectofinal.entidades.Categoria;
import com.ppii.proyectofinal.servicios.CategoriaService;

@RestController
@RequestMapping("api/v1/categorias")
public class CategoriaController {
	
	@Autowired
	private CategoriaService service;
	
	@GetMapping("/describir/lista")
	public List<Categoria> getAllCategorias() {
		return service.obtenerCategorias();
	}
	
	@GetMapping("/buscar/{categorias}")
	public List<Categoria> getCategoriasBusqueda(@PathVariable String categorias) {
		return service.buscarCategorias(categorias, 1);
	}
	
	@DeleteMapping("/eliminar/{id}")
	public void deleteCategoria(@PathVariable Long id) {
		service.eliminarCategorias(id);
	}
	
}
