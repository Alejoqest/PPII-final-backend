package com.ppii.proyectofinal.controladores;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ppii.proyectofinal.dto.PeliculaBusquedaDTO;
import com.ppii.proyectofinal.dto.PeliculaDetallesDTO;
import com.ppii.proyectofinal.entidades.Categoria;
import com.ppii.proyectofinal.entidades.FormatoPelicula;
import com.ppii.proyectofinal.entidades.Pelicula;
import com.ppii.proyectofinal.entidades.PortadaPelicula;
import com.ppii.proyectofinal.mappers.PeliculaMapper;
import com.ppii.proyectofinal.servicios.PeliculaService;

@RestController
@RequestMapping("api/v1/pelicula/mostrar/")
public class PeliculaController {
	@Autowired
	private PeliculaMapper mapper;
	
	@Autowired
	private PeliculaService service;
	
	@GetMapping("lista")
	public List<PeliculaBusquedaDTO> getAllPeliculas() {
		return service.cargarPeliculas().stream()
				.map(p -> mapper.toBusqueda(p))
				.collect(Collectors.toList());
	}
	
	/*@GetMapping("/pagina/{numero}")
	public List<PeliculaBusquedaDTO> getPaginaPeliculas(@PathVariable int numero) {
		if (numero == 0) {
			return List.of();
		}
		Page<PeliculaBusquedaDTO> peliculas = service.cargarPaginaPelicula(numero).map(p -> mapper.toBusqueda(p));
		return peliculas.get().toList();
	}*/
	
	@GetMapping
	public List<PeliculaBusquedaDTO> getPeliculasSegunParam(
			@RequestParam int numPag,
			@RequestParam(required = false, defaultValue = "") String nombre,
			@RequestParam(required = false) FormatoPelicula formato,
			@RequestParam(required = false) Long categoriaId,
			@RequestParam(required = false) boolean sinStock,
			@RequestParam(required = false) boolean ordenTiempo,
			@RequestParam(required = false) boolean ordenPrecio) {
		return service.obtenerPaginaPeliculasParametrada(
				numPag, 
				nombre, 
				formato, 
				categoriaId, 
				sinStock, 
				ordenTiempo, 
				ordenPrecio).stream()
					.map(p -> this.mapper.toBusqueda(p))
					.toList();
	}
	
	@GetMapping("contar/")
	public ResponseEntity<Cantidad> getPeliculaCount(
			@RequestParam(required = false, defaultValue = "") String nombre, 
			@RequestParam(required = false) FormatoPelicula formato, 
			@RequestParam(required = false) Long categoriaId, 
			@RequestParam(required = false) boolean sinStock) {
		long count = service.contarResultados(nombre, formato, categoriaId, sinStock);
		return ResponseEntity.ok(new Cantidad(count));
	}
	
	@GetMapping("ultimas")
	public ResponseEntity<List<PeliculaBusquedaDTO>> getPeliculaUltimas() {
		List<Pelicula> peliculas = service.cargarUltimasPeliculas();
		return ResponseEntity.ok(peliculas.stream()
				.map(p -> mapper.toBusqueda(p))
				.toList());
	}
	
	@GetMapping("{id}")
	public PeliculaDetallesDTO getPeliculaDatos(@PathVariable Long id) {
		Pelicula pelicula = this.service.cargarPelicula(id);
		return this.mapper.toDetalles(pelicula);
	}
	
	@GetMapping("{id}/categorias")
	public List<Categoria> getCategorias(@PathVariable Long id) {
		return service.cargarPelicula(id).getCategorias();
	}
	
	@GetMapping("{id}/portada")
	public PortadaPelicula getPortada(@PathVariable Long id) {
		return this.service.cargarPelicula(id).getPortada();
	}
	
	public record Cantidad(long cantidad) {}
	/*@GetMapping({mostrarURL+"/pagina/{numPag}/{nombre}/{formato}/{categoriaId}/{sinStock}/{ordenAcend}",
		mostrarURL+"/pagina/{numPag}/{nombre}/{formato}/{sinStock}/{ordenAcend}",
		mostrarURL+"/pagina/{numPag}/{nombre}/{categoriaId}/{sinStock}/{ordenAcend}",
		mostrarURL+"/pagina/{numPag}/{nombre}/{formato}/{ordenAcend}",
		mostrarURL+"/pagina/{numPag}/{nombre}/{categoriaId}/{ordenAcend}",
		mostrarURL+"/pagina/{numPag}/{nombre}/{formato}/",
		mostrarURL+"/pagina/{numPag}/{nombre}/{categoriaId}/",
		mostrarURL+"/pagina/{numPag}/{nombre}/{sinStock}/{ordenId}"})
	public List<PeliculaBusquedaDTO> getPeliculasSegunParam(@PathVariable int numPag,
			@PathVariable String nombre,
			@PathVariable(required = false) FormatoPelicula formato,
			@PathVariable(required = false) Long categoriaId,
			@PathVariable(required = false) boolean sinStock,
			@PathVariable(required = false) boolean ordenId,
			@PathVariable(required = false) boolean ordenAcend) {
		return service.obtenerPaginaPeliculasParametrada(
				numPag, 
				nombre, 
				formato, 
				categoriaId, 
				sinStock, 
				ordenId, 
				ordenAcend).stream()
			.map(p -> this.mapper.toBusqueda(p))
			.toList();
	}*/
	

	
	/*@GetMapping(mostrarURL+"{id}/catepeli")
	public List<PeliculaBusquedaDTO> getCatePeli(@PathVariable Long id) {
		List <Pelicula> peliculas = (this.service.cargarPelicula(id).getCategorias().get(0).getPeliculas());
		return peliculas.stream().map(e -> this.mapper.toBusqueda(e)).toList();
	}
	
	@GetMapping(mostrarURL+"{id}/portapeli")
	public PeliculaBusquedaDTO getPortaPeli(@PathVariable Long id) {
		Pelicula pelicula = this.service.cargarPelicula(id).getPortada().getPelicula();
		return this.mapper.toBusqueda(pelicula);
	}*/
	
	/*@PreAuthorize("hasRole('ADMIN') or hasRole('ROLE_ADMIN')")
	@PostMapping(value = "/publicar", produces = "application/json")
	public ResponseEntity<String> publicarPelicula(
			@RequestPart(name = "data", required = true) PeliculaCreacionDTO pelicula, 
			@RequestPart(name = "image", required = false) MultipartFile portada
			) throws IOException {
		boolean respuesta = service.registrarPelicula(mapper.toEntity(pelicula), portada);
		return ResponseEntity.status((respuesta)? 201 : 200)
				.body((respuesta)? 
						"Se subio correctamente la pelicula "+ pelicula.getNombre() + "-" + pelicula.getFormato() :
						"Ya existe una entrada con el nombre "+ pelicula.getNombre() + "-" + pelicula.getFormato()
		);
	}
	
	@PreAuthorize("hasAuthority('ADMIN')")
	@PutMapping(value = "/actualizar", produces = "application/json")
	public ResponseEntity<String> actualizarPelicula(
			@RequestPart(name = "data", required = true) PeliculaCreacionDTO pelicula, 
			@RequestPart(name = "image", required = false) MultipartFile portada
			) throws IOException {
		String respuesta = service.cambiarPelicula(mapper.toEntity(pelicula), portada);
		return ResponseEntity.ok(respuesta);
	}
	
	@PreAuthorize("hasAuthority('ADMIN')")
	@DeleteMapping(value = "/eleminar/{id}")
	public void eliminarPelicula(@PathVariable Long id) {
		service.eleminarPelicula(id);
	}*/
}