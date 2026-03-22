package com.ppii.proyectofinal.repositorios;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ppii.proyectofinal.entidades.Factura;

import jakarta.transaction.Transactional;

@Repository
@Transactional
public interface FacturaRepository extends JpaRepository<Factura, Long>{
	
	List<Factura> findAllByUsuarioId(Long UsuarioId, Pageable pageable);
	
	List<Factura> findAllByUsuarioEmail(String email, Pageable pageable);
	
	Optional<Factura> findTopByUsuarioEmailOrderByIdDesc(String email);
}
