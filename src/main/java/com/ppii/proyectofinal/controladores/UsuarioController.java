package com.ppii.proyectofinal.controladores;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ppii.proyectofinal.dto.UsuarioBusquedaDTO;
import com.ppii.proyectofinal.dto.UsuarioDatosDTO;
import com.ppii.proyectofinal.entidades.RolUsuario;
import com.ppii.proyectofinal.entidades.Usuario;
import com.ppii.proyectofinal.entidades.UsuarioFoto;
import com.ppii.proyectofinal.mappers.UsuarioMapper;
import com.ppii.proyectofinal.servicios.UsuarioService;

@RestController
@PreAuthorize("hasRole('ADMIN') OR hasRole('EMPLEADO')")
@RequestMapping("api/v1/usuario/")
public class UsuarioController {
	
	@Autowired
	private UsuarioService service;
	
	@Autowired
	private UsuarioMapper mapper;
	
	@GetMapping("mostrar/pagina/{numPag}")
	public List<UsuarioBusquedaDTO> getUsuariosBusqueda(int numPag) {
		List<Usuario> usuarios = service.cargarUsuarios(numPag);
		return usuarios.stream().map(u -> mapper.aBusqueda(u)).toList();
	}
	
	@GetMapping("mostrar/pagina/")
	public List<UsuarioBusquedaDTO> getUsuarioBusquedaParam(
			@RequestParam(required = false, defaultValue = "1") int numPag, 
			@RequestParam(required = false, defaultValue = "") String string, 
			@RequestParam(required = false) RolUsuario rol,
			@RequestParam(required = false) boolean ordenCreacion) {
		List<Usuario> usuarios = service.cargarPaginaUsuario(numPag, string, rol, ordenCreacion);
		return usuarios.stream().map(u -> mapper.aBusqueda(u)).toList();
	}
	
	@GetMapping("mostrar/contar/")
	public ResponseEntity<Cantidad> getUsuarioCount(
			@RequestParam(required = false, defaultValue = "") String string, 
			@RequestParam(required = false) RolUsuario rol) {
		return ResponseEntity.ok(new Cantidad(service.contarUsuarios(string, rol)));
	}
	
	@GetMapping("mostrar/{id}")
	public UsuarioDatosDTO getUsuarioDetalles(@PathVariable Long id) {
		Usuario usuario = service.cargarUsuarioPorId(id);
		return mapper.aDatosSinContraseña(usuario);
	}
	
	@GetMapping("mostrar/foto/{id}")
	public ResponseEntity<UsuarioFoto> getUsuarioFoto(@PathVariable Long id) {
		UsuarioFoto foto = service.cargarUsuarioPorId(id).getFoto();
		return ResponseEntity.ok(foto);
	}
	
	@PutMapping("actualizar/")
	public ResponseEntity<UsuarioBusquedaDTO> putUsuario(@RequestBody UsuarioDatosDTO intento) {
		Usuario usuario = service.actualizarUsuario(mapper.deDatosAUsuario(intento));
		return ResponseEntity.ok(mapper.aBusqueda(usuario));
	}
	
	@PutMapping("actualizar/foto/{id}")
	public ResponseEntity<UsuarioFoto> putUsuarioFoto(@PathVariable Long id, @RequestPart MultipartFile foto) {
		Usuario usuario = service.cargarUsuarioPorId(id);
		UsuarioFoto imagen = service.actulizarFoto(usuario, foto);
		return ResponseEntity.ok(imagen);
	}
	
	@PreAuthorize("hasRole('ADMIN')")
	@DeleteMapping("eliminar/{id}")
	public ResponseEntity<Void> deleteUsuario(@PathVariable Long id) {
		service.eliminarUsuario(id);
		return ResponseEntity.noContent().build();
	}
	
	public record Cantidad(long cantidad) {}
	
}
