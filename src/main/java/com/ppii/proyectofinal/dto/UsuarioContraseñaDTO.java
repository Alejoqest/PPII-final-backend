package com.ppii.proyectofinal.dto;

import lombok.Data;

@Data
public class UsuarioContraseñaDTO {
	private Long id;
	private String contrasena;
	private String viejaContrasena;
}
