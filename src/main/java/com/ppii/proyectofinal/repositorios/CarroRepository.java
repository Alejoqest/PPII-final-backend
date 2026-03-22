package com.ppii.proyectofinal.repositorios;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ppii.proyectofinal.entidades.CarroCompras;

import jakarta.transaction.Transactional;

@Repository
@Transactional
public interface CarroRepository extends JpaRepository<CarroCompras, Long> {

	CarroCompras findByUsuarioId(Long UsuarioId);
}
