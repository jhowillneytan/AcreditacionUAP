package com.uap.acreditacion.service;

import java.util.List;

import com.uap.acreditacion.entity.Usuario;

public interface IUsuarioService {
    public List<Usuario> findAll();

	public void save(Usuario endidad);

	public Usuario findOne(Long id);

	public void delete(Long id);
	
    public Usuario findByUsernamePassword(String username, String password);
}
