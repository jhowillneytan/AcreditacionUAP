package com.uap.acreditacion.service;

import java.util.List;

import com.uap.acreditacion.entity.Usuario;

public interface IUsuarioService {
    public List<Usuario> findAll();

	public void save(Usuario endidad);

	public Usuario findOne(Long id);

	public void delete(Long id);
	
    public Usuario findByUsernamePassword(String username, String password);

	public Usuario findByUsername(String username);

	public Usuario usuarioPorIdPersona(Long idPersona);

	List<Usuario> listaUsuarioPorIdCarpeta(Long idCarpeta);

	List<Usuario> listaUsuarios();

	List<Usuario> listaUsuarioPorIdCarrera(Long idCarrera);
}
