package com.ppii.proyectofinal.dto;

import com.ppii.proyectofinal.entidades.RolUsuario;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UsuarioBusquedaDTO {
	private Long id;
	private String apodo;
	private String email;
	private RolUsuario rol;
}
