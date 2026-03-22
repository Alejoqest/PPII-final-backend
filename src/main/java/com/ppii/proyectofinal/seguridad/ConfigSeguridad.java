package com.ppii.proyectofinal.seguridad;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Configuracion (con {@link Configuration}) de seguridad de la api del proyecto
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class ConfigSeguridad {
	@Autowired
	private UserDetailsServiceImp service;
	@Autowired
	private JwtTokenVerificador verificadorJwt;
	
	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		return http.csrf(AbstractHttpConfigurer::disable)
				.cors((cors) -> cors.configurationSource(this.corsConfigurationSource()))
				.authorizeHttpRequests(auth -> 
						auth.requestMatchers(
						"/auth/register" , "/auth/login", "/api/v1/pelicula/mostrar/**", "/api/v1/categorias/describir/**", "/api/pelicula/mostrar/**"
						).permitAll()
						.anyRequest().authenticated()
						//auth.anyRequest().permitAll()
						)
				.sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				//.userDetailsService(service)
				.addFilterBefore(verificadorJwt, UsernamePasswordAuthenticationFilter.class)
				.build();
	}
	
	@Bean
	PasswordEncoder encoder() {
		return new BCryptPasswordEncoder(10);
	}
	
	@Bean
	AuthenticationProvider provider() {
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
		provider.setUserDetailsService(service);
		provider.setPasswordEncoder(this.encoder());
		return provider;
	}
	
	@Bean
	AuthenticationManager authenticationManager() {
		return new ProviderManager(this.provider());
	}
	
	@Bean
    CorsConfigurationSource corsConfigurationSource(){
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:4200"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization","content-type"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**",configuration);
        return source;
    }
	
	@Bean
	RoleHierarchy roleHierarchy() {
		return RoleHierarchyImpl.fromHierarchy("ROLE_ADMIN > ROLE_EMPLEADO \n ROLE_EMPLEADO > ROLE_CLIENTE");
	}
}
