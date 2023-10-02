package com.uap.acreditacion.service;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;/*
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;*/
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uap.acreditacion.dao.IPersonaDao;
import com.uap.acreditacion.entity.TipoPersona;

@Service
@Transactional
public class UserDetailsServiceImpl /*implements UserDetailsService*/ {

	@Autowired
    IPersonaDao userRepository;

	/*
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		com.uap.acreditacion.entity.Persona persona = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("Login Username Invalido."));
		System.out.println("nom="+persona.getNombre());
		Set<GrantedAuthority> grantList = new HashSet<GrantedAuthority>(); 
		for (TipoPersona tpersona: persona.getTipoPerson()) {
			GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(tpersona.getNom_tipo_persona());
            grantList.add(grantedAuthority);
		}
		UserDetails user = (UserDetails) new User(username,persona.getPassword(),grantList);
		
		return user;
	}*/
	

}
