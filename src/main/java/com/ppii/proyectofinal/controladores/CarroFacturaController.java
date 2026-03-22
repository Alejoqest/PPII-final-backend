package com.ppii.proyectofinal.controladores;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ppii.proyectofinal.dto.CarroComprasDTO;
import com.ppii.proyectofinal.dto.DetalleFacturaDTO;
import com.ppii.proyectofinal.dto.ElementoCarroDTO;
import com.ppii.proyectofinal.dto.FacturaDTO;
import com.ppii.proyectofinal.entidades.CarroCompras;
import com.ppii.proyectofinal.entidades.Factura;
import com.ppii.proyectofinal.entidades.Usuario;
import com.ppii.proyectofinal.mappers.CarroFacturaMapper;
import com.ppii.proyectofinal.servicios.CarroFacturaService;
import com.ppii.proyectofinal.servicios.UsuarioService;

@RestController
@RequestMapping("api/v1/")
public class CarroFacturaController {
	@Autowired
	private CarroFacturaService carServicio;
	@Autowired
	private CarroFacturaMapper mapper;
	@Autowired
	private UsuarioService userServicio;
	
	@GetMapping(value = "carro/")
	public CarroComprasDTO getCarroPorUsuario(Principal principal) {
		Usuario usuario = userServicio.cargarUsuarioPorEmail(principal.getName());
		return mapper.aCarroComprasDTO(usuario.getCarro());
	}
	
	@GetMapping(value = "carro/elementos") 
	public List<ElementoCarroDTO> getCarroElementos(Principal principal) {
		Usuario usuario = userServicio.cargarUsuarioPorEmail(principal.getName());
		return usuario.getCarro().getElementos().stream()
				.map(e -> mapper.aElementoCarroDTO(e))
				.toList();
	}
	
	@GetMapping(value = "facturas/{numPag}")
	public List<FacturaDTO> getFacturasPorUsuario(Principal principal, @PathVariable Integer numPag) {
		return carServicio.obtenerPaginaFacturas(principal.getName(), numPag).stream()
				.map(f -> mapper.aFacturaDTO(f))
				.toList();
	}
	
	@GetMapping(value = "facturas/{id}/detalles")
	public List<DetalleFacturaDTO> getDetallesFactura(@PathVariable Long id) {
		return carServicio.obtenerDetallesFacturas(id).stream()
				.map(d -> mapper.aDetalleFacturaDTO(d))
				.toList();
	}
	
	@PutMapping(value = "carro/actualizar")
	public ResponseEntity<CarroComprasDTO> setCarroElementos(Principal principal, @RequestBody ElementoCarroDTO elemento) {
		Usuario usuario = userServicio.cargarUsuarioPorEmail(principal.getName());
		CarroCompras carro = carServicio.modificarCarro(usuario.getCarro(), mapper.DTOaElementoCarro(elemento));
		return ResponseEntity.ok(mapper.aCarroComprasDTO(carro));
	}
	
	@PutMapping(value = "carro/vaciar")
	public ResponseEntity<CarroComprasDTO> SetCarroEmpty(Principal principal) {
		Usuario usuario = userServicio.cargarUsuarioPorEmail(principal.getName());
		CarroCompras carro = carServicio.vaciarCarro(usuario.getCarro());
		return ResponseEntity.ok(mapper.aCarroComprasDTO(carro));
	}
	
	@PostMapping(value = "carro/factura")
	public ResponseEntity<CarroComprasDTO> postFacturaCarro(Principal principal) {
		Usuario usuario = userServicio.cargarUsuarioPorEmail(principal.getName());
		CarroCompras carro = usuario.getCarro();
		carro = carServicio.guardarFacturaDesdeCarro(mapper.aFactura(carro), carro, usuario);
		return ResponseEntity.status(HttpStatus.CREATED).body(mapper.aCarroComprasDTO(carro));
	}
	
	@PostMapping(value = "factura/nueva")
	public ResponseEntity<FacturaDTO> postFactura(Principal principal, @RequestBody Factura factura) {
		factura = carServicio.guardarFactura(factura, userServicio.cargarUsuarioPorEmail(principal.getName()));
		return ResponseEntity.status(HttpStatus.CREATED).body(mapper.aFacturaDTO(factura));
	}
	
	/*@GetMapping(value = "v1/carro/mostrar/{id}")
	public CarroCompras getCarroComprasPorId() {
		return null;
	}
	
	@GetMapping(value = "v1/carro/elementos/{id}")
	public List<ElementoCarro> getElementoCarroPorCarro() {
		return List.of(ElementoCarro.builder().build());
	}
	
	@GetMapping(value = "v1/factura/usuario/{id}")
	public List<Factura> getFacturasPorUsuarioId(Long id) {
		return null;
	}
	
	@PutMapping(value = "v1/carro/cambiar/{id}")
	public void setCarroComprasElementos(@PathVariable Long id, @RequestBody ElementoCarro elemento) {
		carServicio.modificarCarro(elemento, id);
	}
	
	@PostMapping(value = "carro/factura")
	public void setNuevaFactura(CarroCompras carro) {
		carServicio.guardarFactura(this.mapper.aFactura(carro));
	}
	
	@PostMapping(value = "factura/nueva")
	public void postNuevaFactura(Factura factura) {
		carServicio.guardarFactura(factura);
	}*/
	
}
