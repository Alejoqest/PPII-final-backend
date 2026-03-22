package com.ppii.proyectofinal.seguridad;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.ppii.proyectofinal.entidades.Usuario;
import com.ppii.proyectofinal.repositorios.UsuarioRepository;

@Service
public class UserDetailsServiceImp  implements UserDetailsService{

	@Autowired
	private UsuarioRepository repo;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Usuario user = repo.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException(username));
		return User.builder()
				.username(user.getEmail())
				.password(user.getContraseña())
				.roles(user.getRol().name())
				.build();
	}

}
