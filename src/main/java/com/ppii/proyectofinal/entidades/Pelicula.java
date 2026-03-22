package com.ppii.proyectofinal.entidades;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
//@JsonIgnoreProperties({"hibernateLazyInitializer"})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pelicula {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String nombre;
	
	private int stock;
	
	private double precio;
	
	private int año;
	
	@Column(columnDefinition = "TEXT")
	private String descripcion;
	
	@Enumerated
	private FormatoPelicula formato;
	
	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "portada_id", referencedColumnName = "id")
	private PortadaPelicula portada;
	
	@ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
	@JoinTable(
			name = "pelicula_categoria",
			joinColumns = @JoinColumn(name = "pelicula_id"),
			inverseJoinColumns = @JoinColumn(name = "categoria_id")
	)
	@Builder.Default
	private List<Categoria> categorias = new ArrayList<>();
	
}
