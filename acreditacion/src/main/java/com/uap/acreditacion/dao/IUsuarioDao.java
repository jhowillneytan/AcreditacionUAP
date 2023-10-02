package com.uap.acreditacion.dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;


import com.uap.acreditacion.entity.Usuario;

public interface IUsuarioDao extends CrudRepository<Usuario, Long>{
@Query("SELECT u FROM Usuario u WHERE u.username = ?1 AND u.password = ?2 AND u.estado = 'A'")
	public Usuario findByUsernamePassword(String username, String password);
	
}
