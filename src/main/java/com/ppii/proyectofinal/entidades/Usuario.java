package com.ppii.proyectofinal.entidades;

import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Contiene los datos del usuario
 * <p>
 * Esta es la clase entidad de usuario que implementa {@link UserDetails}
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(uniqueConstraints = { 
		@UniqueConstraint(name = "EmailUnico" , columnNames = {"email"})
		})
public class Usuario {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String nombre;
	
	private String apellido;
	
	private String apodo;
	
	private String email;
	
	private String telefono;
	
	private String contraseña;
	
	private double dinero;
	
	@Enumerated(EnumType.STRING)
	@Builder.Default
	private RolUsuario rol = RolUsuario.CLIENTE;
	
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "carro_id", referencedColumnName = "id")
	private CarroCompras carro;
    
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "foto_id", referencedColumnName = "id")
    private UsuarioFoto foto;
}
