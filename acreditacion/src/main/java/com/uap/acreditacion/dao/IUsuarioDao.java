package com.uap.acreditacion.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.uap.acreditacion.entity.Usuario;

public interface IUsuarioDao extends CrudRepository<Usuario, Long> {
	@Query("SELECT u FROM Usuario u WHERE u.username = ?1 AND u.password = ?2 AND u.estado = 'A'")
	public Usuario findByUsernamePassword(String username, String password);

	@Query(value = "SELECT u FROM Usuario u WHERE u.username = ?1  AND u.estado = 'A'")
	public Usuario findByUsername(String username);

	// @Query(value = "SELECT u FROM Usuario u WHERE u.username = ?1 AND u.estado =
	// 'A'")
	// public List<Usuario> listarUsuarioAsignadoACarpetas(Long idCarpte);

	@Query(value = "select u from Usuario u where u.persona.id_persona = ?1 AND u.estado != 'X'")
	public Usuario usuarioPorIdPersona(Long idPersona);

	// @Query(value = "SELECT * FROM usuario u " +
	// "LEFT JOIN usuario_carpeta uc ON uc.id_usuario = u.id_usuario " +
	// "LEFT JOIN acre_carpeta c ON c.id_carpeta = uc.id_carpeta " +
	// "WHERE c.id_carpeta = ?1 AND u.estado != 'X'", nativeQuery = true)
	// List<Usuario> listaUsuarioPorIdCarpeta(Long idCarpeta);

	@Query("SELECT u FROM Usuario u " +
			"JOIN u.carpetas c " +
			"WHERE c.id_carpeta = ?1 AND u.estado != 'X'")
	List<Usuario> listaUsuarioPorIdCarpeta(Long idCarpeta);

	@Query("select u from Usuario u where u.estado != 'X'")
	List<Usuario> listaUsuarios();

	@Query(value = "SELECT * FROM usuario u " +
			"LEFT JOIN acre_persona p ON p.id_persona = u.id_persona " +
			"LEFT JOIN acre_carrera c ON c.id_carrera = p.id_carrera " +
			"WHERE c.id_carrera = ?1 AND u.estado != 'X'", nativeQuery = true)
	List<Usuario> listaUsuarioPorIdCarrera(Long idCarrera);

	@Query(value = """
    select u.*
    from usuario u 
    left join usuario_carpeta uc ON uc.id_usuario = u.id_usuario 
    left join acre_carpeta c on c.id_carpeta = uc.id_carpeta 
    where c.id_carpeta = ?1 and u.id_usuario != ?2 and u.estado != 'X'
""", nativeQuery = true)
public List<Usuario> listaUsuarioPorIdCarpetaExceptoUsuarioActual(Long idCarpeta, Long idUsuario);


}
