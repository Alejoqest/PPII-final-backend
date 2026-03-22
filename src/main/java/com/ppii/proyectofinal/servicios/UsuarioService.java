package com.ppii.proyectofinal.servicios;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.ppii.proyectofinal.entidades.CarroCompras;
import com.ppii.proyectofinal.entidades.RolUsuario;
import com.ppii.proyectofinal.entidades.Usuario;
import com.ppii.proyectofinal.entidades.UsuarioFoto;
import com.ppii.proyectofinal.excepcion.ContraseñaEquivocadaExcepcion;
import com.ppii.proyectofinal.excepcion.RecursoNoEncontradoExcepcion;
import com.ppii.proyectofinal.excepcion.RecursoYaExistenteExcepcion;
import com.ppii.proyectofinal.repositorios.UsuarioRepository;

@Service
public class UsuarioService implements ServiceInterface {
	
	@Value("${dir.images.user}")
	private static String DIR_PATH;
	
	private static final String DEFAULT = "0.jpg";
	
	@Autowired
	private UsuarioRepository repo;
	
	@Autowired
	private PasswordEncoder passEncoder;
	
	public Usuario cargarUsuarioPorEmail(String email) {
		return repo.findByEmail(email).orElseThrow(() -> 
			new RecursoNoEncontradoExcepcion("Usuario", "email", email)
		);
	}
	
	public Usuario cargarUsuarioPorId(Long id) {
		return repo.findById(id).orElseThrow(() -> 
			new RecursoNoEncontradoExcepcion("Usuario", "id", Long.toString(id))
		);
	}
	
	public List<Usuario> cargarUsuarios(int numPag) {
		Pageable paginaMuestra = PageRequest.of((numPag - 1), 16);
		return repo.findAll(paginaMuestra).getContent();
	}
	
	public List<Usuario> cargarPaginaUsuario(int numPag, String string, RolUsuario rol, boolean ordenCreacion) {
		Pageable paginaMuestra = PageRequest.of((numPag - 1), 12, (ordenCreacion)? 
				 Sort.by("id").descending() : Sort.by("id").ascending());
		
		List<Usuario> pagina = (rol == null)? 
				repo.findAllByNombreIgnoreCaseContainingOrApellidoIgnoreCaseContainingOrEmailIgnoreCaseContainingOrApodoIgnoreCaseContaining(string, string, string, string, paginaMuestra) : 
				repo.findAllByNombreIgnoreCaseContainingOrApellidoIgnoreCaseContainingOrEmailIgnoreCaseContainingOrApodoIgnoreCaseContainingAndRol(string, string, string, string, rol, paginaMuestra);
		
		return pagina;
	} 
	
	public long contarUsuarios(String string, RolUsuario rol) {
		long cantidad = (rol == null)?
				repo.countByNombreIgnoreCaseContainingOrApellidoIgnoreCaseContainingOrEmailIgnoreCaseContainingOrApodoIgnoreCaseContaining(string, string, string, string) :
				repo.countByNombreIgnoreCaseContainingOrApellidoIgnoreCaseContainingOrEmailIgnoreCaseContainingOrApodoIgnoreCaseContainingAndRol(string, string, string, string, rol);
		return cantidad;
	}

	public void registrarUsuario(Usuario intento) {
		if (repo.existsByEmail(intento.getEmail())) throw new RecursoYaExistenteExcepcion("Usuario", "email", intento.getEmail());
		
		if (intento.getContraseña() == null) throw new NullPointerException("Falta contraseña");
		
		intento.setContraseña(this.cifrarContraseña(intento.getContraseña()));
		
		intento.setFoto(UsuarioFoto.builder()
				.nombreArchivo(DEFAULT)
				.build());
		
		intento.setCarro(CarroCompras.builder()
				.usuario(intento)
				.precioTotal(0)
				.build());
		
		intento.setDinero(0);
		
		repo.save(intento);
	}
	
	public Usuario actualizarUsuario(Usuario usuario) {
		if (usuario.getId() == null || !repo.existsById(usuario.getId())) 
			throw new RecursoNoEncontradoExcepcion("Usuario", "id", (usuario.getId() != null)? Long.toString(usuario.getId()) : "0");
		
		Usuario oldInfo = this.cargarUsuarioPorId(usuario.getId());
		
		if (usuario.getContraseña() != null) usuario.setContraseña(this.cifrarContraseña(usuario.getContraseña())); 
			else usuario.setContraseña(oldInfo.getContraseña()); 
		
		if (!oldInfo.getEmail().equals(usuario.getEmail()) && repo.existsByEmail(usuario.getEmail())) 
			throw new RecursoYaExistenteExcepcion("Usuario", "email", usuario.getEmail());
		
		repo.save(usuario);
		
		return this.cargarUsuarioPorEmail(usuario.getEmail());
	}
	
	public UsuarioFoto actulizarFoto(Usuario usuario, MultipartFile imagen) {
		String FILENAME = this.agregarImagen(Long.toString(usuario.getId()), imagen);
		
		UsuarioFoto foto = usuario.getFoto();
		
		foto.setNombreArchivo(FILENAME);
		
		usuario.setFoto(foto);
		
		repo.save(usuario);
		
		return foto;
	}
	
	public void iniciarSesion(Usuario intento) {
		if ((intento.getEmail() == null || intento.getContraseña() == null) || !repo.existsByEmail(intento.getEmail())) {
			throw new RecursoNoEncontradoExcepcion("Usuario", "email", intento.getEmail());
		}
		
		Usuario user = repo.findByEmail(intento.getEmail()).get();
		
		if (!passEncoder.matches(intento.getContraseña(), user.getContraseña())) 
			throw new ContraseñaEquivocadaExcepcion();
		
		intento.setContraseña(cifrarContraseña(intento.getContraseña()));
	}
	
	public Usuario cambiarContraseña(Long id, String vieja, String nueva) {
		if (id == null || vieja == null || nueva == null || !repo.existsById(id)) {
			throw new RecursoNoEncontradoExcepcion("Usuario", "id", Long.toString(id));
		}
		
		Usuario usuario = repo.findById(id).get();
		
		if (!passEncoder.matches(vieja, usuario.getContraseña())) throw new ContraseñaEquivocadaExcepcion();
		
		usuario.setContraseña(cifrarContraseña(nueva));
		
		repo.save(usuario);
		
		return this.cargarUsuarioPorEmail(usuario.getEmail());
	}
	
	public Usuario ModificarDinero(Long id, double dinero) {
		Usuario usuario = repo.findById(id).orElseThrow(() -> new RecursoNoEncontradoExcepcion("Usuario", "id", Long.toString(id)));
		
		usuario.setDinero(dinero);
		
		repo.save(usuario);
		
		return usuario;
	}
	
	public void eliminarUsuario(Long id) {
		Usuario usuario = repo.findById(id).orElseThrow(() -> new RecursoNoEncontradoExcepcion("Usuario", "id", Long.toString(id)));	
				
		UsuarioFoto foto = usuario.getFoto();
		
		eliminarImagen(foto.getNombreArchivo());
		
		repo.deleteById(id);
	}
	
	private String cifrarContraseña(String contraseña) {
		return passEncoder.encode(contraseña);
	}

	@Override
	public String agregarImagen(String nombre, MultipartFile imagen) {
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
	}

	@Override
	public void eliminarImagen(String imagenDir) {
		if (imagenDir.contains(DEFAULT)) return;
		
		Path OLDFILEPATH = Paths.get(DIR_PATH + imagenDir);
		
		try {
			Files.delete(OLDFILEPATH);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
