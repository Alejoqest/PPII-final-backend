package com.ppii.proyectofinal.mappers;

import org.springframework.stereotype.Component;

import com.ppii.proyectofinal.dto.UsuarioBusquedaDTO;
import com.ppii.proyectofinal.dto.UsuarioDatosDTO;
import com.ppii.proyectofinal.entidades.Usuario;

@Component
public class UsuarioMapper {
	public Usuario deDatosAUsuario(UsuarioDatosDTO d) {
		Usuario usuario = Usuario.builder()
				.id(d.getId())
				.nombre(d.getNombre())
				.apellido(d.getApellido())
				.apodo(d.getApodo())
				.email(d.getEmail())
				.telefono(d.getTelefono())
				.contraseña(d.getContrasena())
				.rol(d.getRol())
				.dinero(d.getDinero())
				.build();
		return usuario;
	}
	
	public Usuario deDatosDineroAUsuario(UsuarioDatosDTO d) {
		return Usuario.builder()
				.id(d.getId())
				.dinero(d.getDinero())
				.build();
	}
	
	public UsuarioBusquedaDTO aBusqueda(Usuario u) {
		return UsuarioBusquedaDTO.builder()
				.id(u.getId())
				.apodo(u.getApodo())
				.email(u.getEmail())
				.rol(u.getRol())
				.build();
	}

	public UsuarioDatosDTO aDatosSinContraseña(Usuario u) {
		return UsuarioDatosDTO.builder()
				.id(u.getId())
				.nombre(u.getNombre())
				.apellido(u.getApellido())
				.apodo(u.getApodo())
				.email(u.getEmail())
				.rol(u.getRol())
				.telefono(u.getTelefono())
				.dinero(u.getDinero())
				.build();
	}
}
