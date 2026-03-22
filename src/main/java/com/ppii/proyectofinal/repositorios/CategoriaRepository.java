package com.ppii.proyectofinal.repositorios;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.ppii.proyectofinal.entidades.Categoria;

/**
 * 
 */
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
	
	boolean existsByNombreIgnoreCase(String nombre);
	
	Categoria findByNombreIgnoreCase(String nombre);

	List<Categoria> findAllByNombreIgnoreCaseContaining(String nombre, Pageable pageable);
	
	List<Categoria> findAllByNombreContainingAndNombreNotIn(String nombre, List<String> nombres, Pageable pageable);
}
