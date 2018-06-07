package com.example.algamoney.api.security;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.algamoney.api.model.Usuario;
import com.example.algamoney.api.repository.UsuarioRepository;

/**
 * Classe Componente de Servico do framework Spring.
 * @author SEMPR
 *
 */
@Service
public class AppUserDetailsService implements UserDetailsService {

	@Autowired
	private UsuarioRepository usuarioRepository;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		/** Na hora que logar na Aplicacao, tem que informar o e-mail. **/
		Optional<Usuario> usuarioOptional = this.usuarioRepository.findByEmail(username);
		Usuario usuario = usuarioOptional.orElseThrow(() -> new UsernameNotFoundException("Usuario e/ou senha incorretos."));
		/** authorities sao as permissoes. **/
		/** return new User(username, usuario.getSenha(), getPermissoes(usuario)); **/
		return new UsuarioSistema(usuario, getPermissoes(usuario));
	}

	private Collection<? extends GrantedAuthority> getPermissoes(Usuario usuario) {
		/** Conjunto de Permissoes **/
		Set<SimpleGrantedAuthority> authorities = new HashSet<>();
		
		/** Carregar as permissoes do Usuario. **/
		/** Para cada uma dessas permissoes, vai adicionando dentro do conjunto authorities **/
		usuario.getPermissoes().forEach(p -> authorities.add(new SimpleGrantedAuthority(p.getDescricao().toUpperCase())));
		
		return authorities;
	}

}
