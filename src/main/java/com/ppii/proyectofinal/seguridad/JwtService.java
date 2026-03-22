package com.ppii.proyectofinal.seguridad;

import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.ppii.proyectofinal.entidades.Usuario;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

	@Value("${jwt.secret}")
	private static String LLAVE;
	
	private static final long VALIDEZ = TimeUnit.MINUTES.toMillis(30);
	
	public String generarToken(Usuario usuario) {
		
		Map<String, String> claims = new HashMap<>();
		
		claims.put("apodo", usuario.getApodo());
		claims.put("rol", "ROLE_"+ usuario.getRol().name());
		
		return Jwts.builder()
				.claims(claims)
				.subject(usuario.getEmail())
				.issuedAt(Date.from(Instant.now()))
				.expiration(Date.from(Instant.now().plusMillis(VALIDEZ)))
				.signWith(this.generarLlave())
				.compact();
	}
	
	private SecretKey generarLlave() {
		byte[] decodedKey = Base64.getDecoder().decode(LLAVE);
		return Keys.hmacShaKeyFor(decodedKey);
	}
	
	public String extraerNombre(String jwt) {
		Claims claims = Jwts.parser()
			.verifyWith(this.generarLlave())
			.build()
			.parseSignedClaims(jwt)
			.getPayload();
		return claims.getSubject();
	}
	
	public Claims obtenerClaims(String jwt) {
		return Jwts.parser()
			.verifyWith(this.generarLlave())
			.build()
			.parseSignedClaims(jwt)
			.getPayload();
	}
	
	public boolean esValido(String jwt) {
		Claims claims = this.obtenerClaims(jwt);
		return claims.getExpiration().after(Date.from(Instant.now()));
	}
}
