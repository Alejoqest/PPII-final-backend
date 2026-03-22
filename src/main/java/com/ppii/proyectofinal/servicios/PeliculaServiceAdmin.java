package com.ppii.proyectofinal.servicios;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.ppii.proyectofinal.entidades.Categoria;
import com.ppii.proyectofinal.entidades.Pelicula;
import com.ppii.proyectofinal.entidades.PortadaPelicula;
import com.ppii.proyectofinal.excepcion.RecursoNoEncontradoExcepcion;
import com.ppii.proyectofinal.excepcion.RecursoYaExistenteExcepcion;
import com.ppii.proyectofinal.repositorios.CategoriaRepository;
import com.ppii.proyectofinal.repositorios.PeliculaRepository;

@Service
public class PeliculaServiceAdmin implements ServiceInterface {
	
	@Value("${dir.images.movie}")
	private static String DIR_PATH;
	
	private static final String NOT_FOUND = "NOTFOUND.jpg";
	
	@Autowired
	private PeliculaRepository pRepository;
	
	@Autowired
	private CategoriaRepository cRepository;
	
	public Pelicula generarPelicula(Pelicula pelicula, MultipartFile portada) {
		if (pelicula.getNombre() == null || pelicula.getFormato() == null) throw new NullPointerException("No hay parametros necesarios");
		if (pRepository.existsByNombreAndFormato(pelicula.getNombre(), pelicula.getFormato())) 
			throw new RecursoYaExistenteExcepcion("Pelicula", "nombre y formato", pelicula.getNombre()+"-"+pelicula.getFormato());
		/*if (pelicula.getNombre() == null || pelicula.getFormato() == null || 
				pRepository.existsByNombreAndFormato(pelicula.getNombre(), pelicula.getFormato())) {
			throw new RecursoYaExistenteExcepcion("Pelicula", "nombre y formato", pelicula.getNombre()+"-"+pelicula.getFormato());
		}*/
		
		String FILEPATH = this.agregarImagen((pelicula.getNombre()+"-"+pelicula.getFormato()), portada);
		
		pelicula.setCategorias(
				this.administrarCategorias(pelicula.getCategorias())
				);
		
		pelicula.setPortada(
				PortadaPelicula.builder()
				.URL(FILEPATH)
				.build()
		);
		
		pRepository.save(pelicula);
		
		return pRepository.findByNombreAndFormato(pelicula.getNombre(), pelicula.getFormato());
	}
	
	public Pelicula cambiarPrecio(Pelicula info) {
		Pelicula pelicula = pRepository.findById(info.getId()).orElseThrow(() -> new RecursoNoEncontradoExcepcion("Pelicula", "id", Long.toString(info.getId())));
		
		pelicula.setPrecio(info.getPrecio());
		
		pRepository.save(pelicula);
		
		return pelicula;
	}
	public Pelicula actualizarPelicula(Pelicula pelicula, MultipartFile portada) {
		if (pelicula.getId() == null) throw new NullPointerException();
		
		Pelicula peliculaOg = pRepository.findById(pelicula.getId()).orElseThrow(() -> new RecursoNoEncontradoExcepcion("Pelicula", "id", Long.toString(pelicula.getId())));
				
		boolean hayDiferencia = (!pelicula.getNombre().equals(peliculaOg.getNombre()) ||
				pelicula.getFormato() != peliculaOg.getFormato());
		
		if (hayDiferencia &&
				pRepository.existsByNombreAndFormato(pelicula.getNombre(), pelicula.getFormato())) {
			throw new RecursoYaExistenteExcepcion("Pelicula", "nombre y formato", pelicula.getNombre()+"-"+pelicula.getFormato());
		}
		
		String FILEPATH = this.agregarImagen((pelicula.getNombre()+"-"+pelicula.getFormato()), portada);
		
		if (hayDiferencia) this.eliminarImagen(peliculaOg.getPortada().getURL());
		
		PortadaPelicula portadaOg = peliculaOg.getPortada();

		if (!FILEPATH.contains(NOT_FOUND)) portadaOg.setURL(FILEPATH);
		
		pelicula.setPortada(portadaOg);
		pelicula.setCategorias(this.administrarCategorias(pelicula.getCategorias()));
		
		pRepository.save(pelicula);
		
		return pRepository.findByNombreAndFormato(pelicula.getNombre(), pelicula.getFormato());
	}
	
	public void eleminarPelicula(long id) {
		Pelicula pelicula = this.pRepository.findById(id).orElseThrow(() -> new RecursoNoEncontradoExcepcion("Pelicula", "id", Long.toString(id)));
		
		this.pRepository.deleteById(pelicula.getId());
		
		this.eliminarImagen(pelicula.getPortada().getURL());
	}

	private List<Categoria> administrarCategorias(List<Categoria> categorias) {
		if (categorias.isEmpty()) return categorias;
		
		List<Categoria> noExistentes = categorias.stream()
				.filter(c -> !this.cRepository.existsByNombreIgnoreCase(c.getNombre()))
				.collect(Collectors.toList());
		
		categorias.removeAll(noExistentes);
		
		categorias = categorias.stream()
			.map(c -> this.cRepository.findByNombreIgnoreCase(c.getNombre()))
			.collect(Collectors.toList());
		
		categorias.addAll(noExistentes);
		
		/*List<Categoria> sinId = categorias.stream()
				.filter(c -> c.getId() == null)
				.collect(Collectors.toList());
		
		categorias.removeAll(sinId);
		
		List<Categoria> encontrados = sinId.stream()
				.filter(c -> this.cRepository.existsByNombreIgnoreCase(c.getNombre()))
				.collect(Collectors.toList());
		
		sinId.removeAll(encontrados);
		
		categorias.addAll(encontrados);
			
		if (!categorias.isEmpty()) categorias = categorias.stream()
				.map(e -> cRepository.findByNombreIgnoreCase(e.getNombre()))
				.toList();
		
		categorias.addAll(sinId);*/
		
		return categorias;
	}

	@Override
	public String agregarImagen(String nombre, MultipartFile imagen) {
		if (imagen == null) return (DIR_PATH + NOT_FOUND);
		
		nombre = nombre.replace(" ", "_");
		
		String nombreArchivo = imagen.getOriginalFilename();
		
		String extension = Optional.ofNullable(nombreArchivo)
				.filter(n -> n.contains("."))
				.map(e -> e.substring(nombreArchivo.lastIndexOf(".") + 1))
				.get();
		String FILEPATH = DIR_PATH + nombre + "." + extension;
		
		try {
			Files.write(Paths.get(FILEPATH), imagen.getBytes());
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		return (nombre + "." + extension);
		
		//return FILEPATH;
	}

	@Override
	public void eliminarImagen(String imagenDir) {
		if (imagenDir.contains(NOT_FOUND)) return;
		
		Path OLDFILEPATH = Paths.get(DIR_PATH + imagenDir);
		
		try {
			Files.delete(OLDFILEPATH);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
