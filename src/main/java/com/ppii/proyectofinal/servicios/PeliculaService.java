package com.ppii.proyectofinal.servicios;

//import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
import java.util.List;
//import java.util.Optional;
//import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
//import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.ppii.proyectofinal.entidades.Categoria;
import com.ppii.proyectofinal.entidades.FormatoPelicula;
import com.ppii.proyectofinal.entidades.Pelicula;
import com.ppii.proyectofinal.repositorios.PeliculaRepository;

/**
 * Capa service del servicio que se encarga solamente de realizar la logica
 * No usa el mapper, en otras palabras no se utiliza DTOs en esta capa
 */
@Service
public class PeliculaService {
	@Autowired
	private PeliculaRepository pRepo;
	
	/**
	 * 
	 * @param id
	 * @return
	 */
	public Pelicula cargarPelicula(Long id) {
		return this.pRepo.findById(id).get();
	}
	
	public List<Pelicula> cargarPeliculas() {
		List<Pelicula> peliculas = pRepo.findAll();
		return peliculas;
	}
	
	public Page<Pelicula> cargarPaginaPelicula(int pagina) {
		Pageable paginaMuestra = PageRequest.of(pagina - 1, 16);
		return pRepo.findAll(paginaMuestra);
	}
	
	public List<Pelicula> cargarUltimasPeliculas() {
		return pRepo.findTop3ByOrderByIdDesc();
	}
	
	//DESC es mas nuevo
	public List<Pelicula> obtenerPaginaPeliculasParametrada(int numPag, String nombre, FormatoPelicula formato,
			 Long categoriaId, boolean sinStock, boolean ordenId, boolean ordenAcend) {
		/*Pageable paginaMuestra = PageRequest.of((numPag - 1), 16, (ordenId)?
				Sort.by("id").descending().and(Sort.by("precio").ascending()) : 
					(ordenAcend)? 
					Sort.by("precio").ascending() : 
					Sort.by("precio").descending()
		)*/
		
		Sort precioSort = (ordenAcend)? Sort.by("precio").ascending() : Sort.by("precio").descending();
		
		Pageable paginaMuestra = PageRequest.of((numPag - 1), 12, (!ordenId)?
				Sort.by("id").descending().and(precioSort) : precioSort);
						
		int stock = (sinStock)? 0 : 1;
		
		boolean param = (formato != null && categoriaId != null);
		
		if (param) return pRepo.findAllByStockGreaterThanEqualAndNombreIgnoreCaseContainingAndFormatoAndCategoriasId(stock, nombre, formato, categoriaId, paginaMuestra);
		
		return (formato == null && categoriaId == null)? pRepo.findAllByStockGreaterThanEqualAndNombreIgnoreCaseContaining(stock, nombre, paginaMuestra) : 
			(formato != null && categoriaId == null)? pRepo.findAllByStockGreaterThanEqualAndNombreIgnoreCaseContainingAndFormato(stock, nombre, formato, paginaMuestra) : 
				pRepo.findAllByStockGreaterThanEqualAndNombreIgnoreCaseContainingAndCategoriasId(stock, nombre, categoriaId, paginaMuestra);
		
		/*if (param) {
			List <Pelicula> peliculas = pRepo.findAllByNombreContainingAndFormatoAndCategoriasIdAndStockGreaterThanEqual(nombre, formato, categoriaId, stock, paginaMuestra);
			return peliculas;
		}
		
		List<Pelicula> peliculas = (formato != null && categoriaId == null) ? 
				pRepo.findAllByNombreContainingAndFormatoAndStockGreaterThanEqual(nombre, formato, stock, paginaMuestra) : 
					(formato == null && categoriaId != null) ? 
						pRepo.findAllByNombreContainingAndCategoriasIdAndStockGreaterThanEqual(nombre, categoriaId, stock, paginaMuestra) : 
							pRepo.findAllByNombreContainingAndStockGreaterThanEqual(nombre, stock, paginaMuestra);
		*/
	}
	
	public long contarResultados(String nombre, FormatoPelicula formato,
			 Long categoriaId, boolean sinStock) {
		int stock = (sinStock)? 0 : 1;
		
		boolean param = (formato != null && categoriaId != null);

		if (param) return pRepo.countByStockGreaterThanEqualAndNombreIgnoreCaseContainingAndFormatoAndCategoriasId(stock, nombre, formato, categoriaId);
		
		return (formato == null && categoriaId == null)? pRepo.countByStockGreaterThanEqualAndNombreIgnoreCaseContaining(stock, nombre) : 
			(formato != null && categoriaId == null)? pRepo.countByStockGreaterThanEqualAndNombreIgnoreCaseContainingAndFormato(stock, nombre, formato) :
				pRepo.countByStockGreaterThanEqualAndNombreIgnoreCaseContainingAndCategoriasId(stock, nombre, categoriaId);
	}
	
	public Pelicula cargarPeliculaPorNombre(String nombre, FormatoPelicula formato) {
		return pRepo.findByNombreIgnoreCaseAndFormato(nombre, formato);
	}

	public long contarPeliculas() {
		return pRepo.count();
	}
	
	public List<Categoria> obtenerCategoriasPelicula(Long id) {
		return pRepo.findById(id).get().getCategorias();
	}
	
	
	/*public boolean registrarPelicula(Pelicula pelicula, MultipartFile portada) throws IOException {
		if ((pelicula.getNombre() == null && pelicula.getFormato() == null) || 
				pRepo.existsByNombreAndFormato(pelicula.getNombre(), pelicula.getFormato())) {
			return false;
		}
		
		String FILEPATH = (portada == null)? 
				DIR_PATH + NOT_FOUND :
				this.cargarPortada(
					pelicula.getNombre() + " " + pelicula.getFormato() + ".", 
					portada.getOriginalFilename(), 
					portada.getBytes()
				);
		
		pelicula.setCategorias(
				this.administrarCategorias(pelicula.getCategorias())
				);
				
		pelicula.setPortada(
				PortadaPelicula.builder()
				.URL(FILEPATH)
				.build()
		);
		
		pRepo.save(pelicula);
		return true;
	}
	
	public String cambiarPelicula(Pelicula pelicula, MultipartFile portada) throws IOException {
		if (pelicula.getId() == null) {
			return ("La informacion no tiene id.");
		}
				
		Optional<Pelicula> peliculaOg = pRepo.findById(pelicula.getId());

		if (peliculaOg.isEmpty()) {
			return ("No existe una pelicula con ese id");
		}
		
		boolean hayDiferencia = (!pelicula.getNombre().equals(peliculaOg.get().getNombre()) ||
				pelicula.getFormato() == peliculaOg.get().getFormato());
		
		if (hayDiferencia &&
				pRepo.existsByNombreAndFormato(pelicula.getNombre(), pelicula.getFormato())) {
			return ("Ya hay una pelicula con ese nombre y formato");
		}
		
		PortadaPelicula portadaOg = peliculaOg.get().getPortada();
		
		if (portada != null) {
			String FILEPATH = this.cargarPortada(
					pelicula.getNombre() + " " + pelicula.getFormato() + ".", 
					portada.getOriginalFilename(), 
					portada.getBytes()
				);
			
			if (hayDiferencia && !portadaOg.getURL().contains(NOT_FOUND)) {
				this.eliminarImagen(portadaOg.getURL());
			}
			
			portadaOg.setURL(FILEPATH);
		}
		
		pelicula.setPortada(portadaOg);
		pelicula.setCategorias(this.administrarCategorias(pelicula.getCategorias()));
		
		pRepo.save(pelicula);
		
		return ("La pelicula ID:" + pelicula.getId() +" se actualizo");
	}
	
	public boolean eleminarPelicula(long id) {
		if (!pRepo.existsById(id)) {
			return false;
		}
		
		pRepo.deleteById(id);;
		return true;
	}
	
	private String cargarPortada(String NEWFILENAME, 
			String OLDNAME, 
			byte[] BYTES) throws IOException {
		String FILEEXT = Optional.ofNullable(OLDNAME)
				.filter(n -> n.contains("."))
				.map(e -> e.substring(OLDNAME.lastIndexOf(".") + 1))
				.get();
		String FILEPATH = DIR_PATH + NEWFILENAME + FILEEXT;
		Files.write(Paths.get(FILEPATH), BYTES);
		return FILEPATH;
	}
	
	private void eliminarImagen(String oldURL) throws IOException {
		Path OLDFILEPATH = Paths.get(oldURL);
		Files.delete(OLDFILEPATH);
	}
	
	private List<Categoria> administrarCategorias(List<Categoria> categorias) {
		if (categorias.isEmpty()) {
			List<Categoria> sinId = categorias.stream()
				.filter(c -> c.getId() == null)
				.collect(Collectors.toList());
		
			categorias.removeAll(sinId.stream()
				.filter(c -> this.cRepo.existsByNombreIgnoreCase(c.getNombre()))
				.toList());
		
		}
		return categorias;
	}*/

}