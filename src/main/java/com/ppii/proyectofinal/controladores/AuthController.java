package com.ppii.proyectofinal.controladores;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ppii.proyectofinal.dto.UsuarioBusquedaDTO;
import com.ppii.proyectofinal.dto.UsuarioContraseñaDTO;
import com.ppii.proyectofinal.dto.UsuarioDatosDTO;
import com.ppii.proyectofinal.entidades.Jwt;
import com.ppii.proyectofinal.entidades.Usuario;
import com.ppii.proyectofinal.entidades.UsuarioFoto;
import com.ppii.proyectofinal.mappers.UsuarioMapper;
import com.ppii.proyectofinal.seguridad.JwtService;
import com.ppii.proyectofinal.servicios.UsuarioService;

@RestController
@RequestMapping("auth/")
public class AuthController {
	
	@Autowired
	private AuthenticationManager authManager;
	
	@Autowired
	private UsuarioService service;
	
	@Autowired
	private JwtService jwtService;
	
	@Autowired
	private UsuarioMapper mapper;

	@PostMapping("register")
	public ResponseEntity<Jwt> regitrarse(@RequestBody UsuarioDatosDTO intento) {
		service.registrarUsuario(this.mapper.deDatosAUsuario(intento));
		Authentication auth = authManager.authenticate(new UsernamePasswordAuthenticationToken(intento.getEmail(), intento.getContrasena()));
		
		Jwt jwt = new Jwt(
				(auth.isAuthenticated())? jwtService.generarToken(service.cargarUsuarioPorEmail(intento.getEmail())) : ""
					);
		
		return ResponseEntity.status(HttpStatus.CREATED).body(jwt);
	}
	
	@PostMapping("login")
	public ResponseEntity<Jwt> iniciarUsuario(@RequestBody UsuarioDatosDTO intento) {
		service.iniciarSesion(this.mapper.deDatosAUsuario(intento));
		Authentication auth = authManager.authenticate(new UsernamePasswordAuthenticationToken(intento.getEmail(), intento.getContrasena()));
		
		Jwt jwt = new Jwt(
				(auth.isAuthenticated())? jwtService.generarToken(service.cargarUsuarioPorEmail(intento.getEmail())) : ""
					);
		
		return ResponseEntity.status(HttpStatus.OK).body(jwt);
	}
	
	@PreAuthorize("isAuthenticated()") 
	@GetMapping("cuenta")
	public ResponseEntity<UsuarioBusquedaDTO> getUsuarioPrincipalBusqueda(Principal principal) {
		Usuario usuario = service.cargarUsuarioPorEmail(principal.getName());
		return ResponseEntity.ok(mapper.aBusqueda(usuario));
	}
	
	@PreAuthorize("isAuthenticated()") 
	@GetMapping("cuenta/detalles")
	public ResponseEntity<UsuarioDatosDTO> getUsuarioPrincipalDetalles(Principal principal) {
		Usuario usuario = service.cargarUsuarioPorEmail(principal.getName());
		return ResponseEntity.ok(mapper.aDatosSinContraseña(usuario));
	}
	
	@PreAuthorize("isAuthenticated()") 
	@GetMapping("cuenta/foto")
	public ResponseEntity<UsuarioFoto> getFoto(Principal principal) {
		Usuario usuario = service.cargarUsuarioPorEmail(principal.getName());
		return ResponseEntity.ok(usuario.getFoto());
	}
	
	@PreAuthorize("isAuthenticated()") 
	@PutMapping("cuenta/actualizar")
	public ResponseEntity<Jwt> putUsuario(@RequestBody UsuarioDatosDTO datos) {
		Usuario usuario = service.actualizarUsuario(this.mapper.deDatosAUsuario(datos));
		//Authentication auth = authManager.authenticate(new UsernamePasswordAuthenticationToken(usuario.getEmail(), usuario.getContraseña()));
		
		Jwt jwt = new Jwt(jwtService.generarToken(usuario));
		
		return ResponseEntity.ok(jwt);
	}
	
	@PreAuthorize("isAuthenticated()") 
	@PutMapping("cuenta/contraseña")
	public ResponseEntity<Jwt> putContraseña(@RequestBody UsuarioContraseñaDTO datos) {
		Usuario usuario = service.cambiarContraseña(datos.getId(), datos.getViejaContrasena(), datos.getContrasena());
		//Authentication auth = authManager.authenticate(new UsernamePasswordAuthenticationToken(usuario.getEmail(), usuario.getContraseña()));
		
		Jwt jwt = new Jwt(jwtService.generarToken(usuario));
		
		return ResponseEntity.ok(jwt);
	}
	
	@PreAuthorize("isAuthenticated()") 
	@PutMapping("cuenta/dinero")
	public ResponseEntity<UsuarioDatosDTO> putDinero(@RequestBody UsuarioDatosDTO data) {
		Usuario user = service.ModificarDinero(data.getId(), data.getDinero());
		return ResponseEntity.ok(mapper.aDatosSinContraseña(user));
	}
	
	@PreAuthorize("isAuthenticated()")
	@PutMapping("cuenta/foto")
	public ResponseEntity<UsuarioFoto> putFoto(@RequestPart MultipartFile imagen, Principal principal) {
		Usuario usuario = service.cargarUsuarioPorEmail(principal.getName());
		UsuarioFoto foto = service.actulizarFoto(usuario, imagen);
		return ResponseEntity.ok(foto);
	}
	
	@PreAuthorize("isAuthenticated()") 
	@DeleteMapping("cuenta/eliminar")
	public ResponseEntity<Void> deleteUsuario(Principal principal) {
		Usuario usuario = service.cargarUsuarioPorEmail(principal.getName());
		service.eliminarUsuario(usuario.getId());
		return ResponseEntity.noContent().build();
	}
}
