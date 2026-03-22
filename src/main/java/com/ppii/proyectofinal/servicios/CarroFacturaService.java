package com.ppii.proyectofinal.servicios;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.ppii.proyectofinal.entidades.CarroCompras;
import com.ppii.proyectofinal.entidades.DetalleFactura;
import com.ppii.proyectofinal.entidades.ElementoCarro;
import com.ppii.proyectofinal.entidades.Factura;
import com.ppii.proyectofinal.entidades.Pelicula;
import com.ppii.proyectofinal.entidades.Usuario;
import com.ppii.proyectofinal.excepcion.RecursoInsuficienteExcepcion;
import com.ppii.proyectofinal.excepcion.RecursoNoEncontradoExcepcion;
import com.ppii.proyectofinal.repositorios.CarroRepository;
import com.ppii.proyectofinal.repositorios.FacturaRepository;
import com.ppii.proyectofinal.repositorios.PeliculaRepository;

@Service
public class CarroFacturaService {
	
	@Autowired
	private CarroRepository carroRepository;
	@Autowired
	private FacturaRepository facturaRepositorio;
	@Autowired
	private PeliculaRepository peliculaRepositorio;
	
	/*public CarroCompras obtenerCarro(Long usuarioId) {
		CarroCompras carro = carroRepository.findByUsuarioId(usuarioId);
		
		return carro;
	}
	
	public List<ElementoCarro> obtenerElementosCarro(Long carroId) {
		List<ElementoCarro> elementos = carroRepository.findById(carroId).get().getElementos();
		
		return elementos;
	}*/
	
	public List<Factura> obtenerPaginaFacturas(String usuarioEmail, int pagina) {
		Pageable paginaMuestra = PageRequest.of((pagina - 1), 16);
		List<Factura> facturas = facturaRepositorio.findAllByUsuarioEmail(usuarioEmail, paginaMuestra);
		
		return facturas;
	}
	
	public List<DetalleFactura> obtenerDetallesFacturas(Long facturaId) {
		List<DetalleFactura> detalles = facturaRepositorio.findById(facturaId).get().getDetalles();
		
		return detalles;
	}
	
	public CarroCompras modificarCarro(CarroCompras carro, ElementoCarro elemento) {
		if (!carro.getElementos().isEmpty()) {
			Long id = carro.getElementos().stream()
					.filter(e -> e.getPelicula().getId() == elemento.getPelicula().getId())
					.findAny()
					.map(e -> e.getId())
					.orElse(null);
			
			if (id != null) carro.getElementos().removeIf(e -> e.getId() == id); 
			
			elemento.setId(id);
		}
		
		if (elemento.getUnidades() != 0) carro.getElementos().add(elemento);
		
		carro.setPrecioTotal(this.setTotal(carro.getElementos()));
		carroRepository.save(carro);
		return carroRepository.findById(carro.getId()).orElseThrow(() -> new RecursoNoEncontradoExcepcion("Carro", "Id", Long.toString(carro.getId())));
	}
	
	private double setTotal(List<ElementoCarro> elementos) {
		double precio = (elementos.isEmpty())? 0 : elementos.stream()
				.mapToDouble(e -> e.getSubTotal())
				.sum();
		return precio;
	}
	
	public CarroCompras vaciarCarro(CarroCompras carro) {
		carro.getElementos().clear();
		carro.setPrecioTotal(0);
		
		carroRepository.save(carro);
		return carroRepository.findById(carro.getId()).orElseThrow(() -> new RecursoNoEncontradoExcepcion("Carro", "Id", Long.toString(carro.getId())));
	}
	
	public Factura guardarFactura(Factura factura, Usuario usuario) {
		if (factura.getDetalles().isEmpty()) throw new RecursoNoEncontradoExcepcion("Factura", "Detalles", "No vacio");
		
		List<DetalleFactura> hayError = factura.getDetalles().stream()
				.filter(d -> !peliculaRepositorio.existsByIdAndStockGreaterThanEqual(d.getPelicula().getId(), d.getUnidades()))
				.toList();
		
		if (!hayError.isEmpty()) throw new RecursoInsuficienteExcepcion("Unidades", "mayor");
		
		List<Pelicula> peliculasActualizadas = factura.getDetalles().stream()
				.map(d -> {
					Pelicula pelicula = peliculaRepositorio.findById(d.getPelicula().getId()).orElseThrow(() -> new RecursoNoEncontradoExcepcion("Pelicula", "Id", Long.toString(d.getPelicula().getId())));
					pelicula.setStock((pelicula.getStock() - d.getUnidades()));
					return pelicula;
				})
				.collect(Collectors.toList());
		
		if (usuario.getDinero() < factura.getPrecioTotal()) throw new RecursoInsuficienteExcepcion("Dinero", Double.toString(usuario.getDinero()), "mayor", Double.toString(factura.getPrecioTotal()));
		
		usuario.setDinero((usuario.getDinero() - factura.getPrecioTotal()));
		
		factura.setUsuario(usuario);
		
		peliculaRepositorio.saveAll(peliculasActualizadas);	
		facturaRepositorio.save(factura);
		return facturaRepositorio.findTopByUsuarioEmailOrderByIdDesc(usuario.getEmail()).orElseThrow(() -> new NullPointerException());
	}
	
	/*List<Pelicula> peliculasActualizadas = factura.getDetalles().stream()
				.map(d -> Pelicula.builder()
						.id(d.getPelicula().getId())
						.stock((peliculaRepositorio.findById(d.getPelicula().getId()).get().getStock() - d.getUnidades()))
						.build()
					)
				.collect(Collectors.toList());*/
	
	public CarroCompras guardarFacturaDesdeCarro(Factura factura, CarroCompras carro, Usuario usuario) {
		this.guardarFactura(factura, usuario);
		
		carro = this.vaciarCarro(carro);
		
		return carro;
	}
	
/*CarroCompras carro = carroRepository.findById(idCarro).orElseThrow(() -> 
			new RecursoNoEncontradoExcepcion("Carro", "id", Long.toString(idCarro))
		)
	public boolean modificarCarro(ElementoCarro nuevoElemento, CarroCompras carro) {
		if (!carro.getElementos().isEmpty()) {			
			ElementoCarro elementoCarro = carro.getElementos().stream()
				.filter(elemento -> elemento.getPelicula().getId() == nuevoElemento.getPelicula().getId())
				.findAny()
				.orElse(null);
		
			if (elementoCarro != null) {
				nuevoElemento.setId(elementoCarro.getId());
				carro.getElementos().remove(elementoCarro);
				//elemCarroRepositorio.deleteById(elementoCarro.getId());
			}
		}
		
		if (nuevoElemento.getUnidades() != 0) carro.getElementos().add(nuevoElemento);
		
		carro.setPrecioTotal((carro.getElementos().isEmpty())? 0 : 
			carro.getElementos().stream()
				.mapToDouble(elemento -> elemento.getSubTotal())
				.sum()
		);
		
		carroRepository.save(carro);	
		return true;
	}
			 
  	No es necesario debido a que ya me fijo el elemento individual que se añade tiene 0 unidades:
			List<ElementoCarro> elementosIndeseables = carro.getElementos().stream()
				.filter(elemento -> elemento.getUnidades() == 0)
				.toList();
		
			carro.getElementos().removeAll(elementosIndeseables);
		
			elemCarroRepositorio.deleteAll(elementosIndeseables);
			*/
}
