package com.ppii.proyectofinal.repositorios;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
//import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import com.ppii.proyectofinal.entidades.RolUsuario;
import com.ppii.proyectofinal.entidades.Usuario;

import jakarta.transaction.Transactional;

@Transactional
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
	boolean existsByNombre(String nombre);
	boolean existsByIdOrEmail(@Nullable Long id, @Nullable String email);
	Optional<Usuario> findByNombre(String nombre);
	Optional<Usuario> findByEmail(String nombre);
	boolean existsByEmail(String email);
	void deleteByIdOrEmail(@Nullable Long id, @Nullable String email);
	
	List<Usuario> findAllByNombreIgnoreCaseContainingOrApellidoIgnoreCaseContainingOrEmailIgnoreCaseContainingOrApodoIgnoreCaseContaining(
			String nombre,
			String apellido,
			String email,
			String apodo,
			Pageable pageable
	);
	
	List<Usuario> findAllByNombreIgnoreCaseContainingOrApellidoIgnoreCaseContainingOrEmailIgnoreCaseContainingOrApodoIgnoreCaseContainingAndRol(
			String nombre, 
			String apellido, 
			String email, 
			String apodo, 
			RolUsuario rol, 
			Pageable pageable
	);
	
	long countByNombreIgnoreCaseContainingOrApellidoIgnoreCaseContainingOrEmailIgnoreCaseContainingOrApodoIgnoreCaseContaining(
			String nombre,
			String apellido,
			String email,
			String apodo);
	
	long countByNombreIgnoreCaseContainingOrApellidoIgnoreCaseContainingOrEmailIgnoreCaseContainingOrApodoIgnoreCaseContainingAndRol(
			String nombre, 
			String apellido, 
			String email, 
			String apodo, 
			RolUsuario rol);
}
