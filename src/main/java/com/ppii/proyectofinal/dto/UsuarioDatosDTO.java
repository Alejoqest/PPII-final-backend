package com.ppii.proyectofinal.dto;

import com.ppii.proyectofinal.entidades.RolUsuario;

import lombok.Builder;
import lombok.Data;
/**
 * 
 */
@Data
@Builder
public class UsuarioDatosDTO {
	private Long id;
	private String nombre;
	private String apellido;
	private String apodo;
	private String email;
	private String telefono;
	private String contrasena;
	private double dinero;
	private RolUsuario rol;
}
