package com.uap.acreditacion.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.uap.acreditacion.entity.Carpeta;

public interface ICarpetaDao extends JpaRepository<Carpeta, Long> {

	@Query("select c from Carpeta c left join c.carrera ca where ca.id_carrera=?1 order by c.id_carpeta asc")
	public List<Carpeta> getAllCarpetasCarrera(Long id_carrera);

	// @Query(value = "SELECT c.* " +
	// "FROM acre_carpeta c " +
	// "JOIN usuario_carpeta uc ON c.id_carpeta = uc.id_carpeta " +
	// "JOIN usuario u ON uc.id_usuario = u.id_usuario " +
	// "WHERE u.id_usuario = ?1", nativeQuery = true)
	// public List<Carpeta> getAllCarpetasUsuario(Long id_usuario);

	@Query(value = "select c from Carpeta c where c.estado != 'X' and c.carpetaPadre.id_carpeta = ?1")
	List<Carpeta> listaCarpetasHijosPorIdCarpeta(Long idCarpeta);

	@Query(value = "SELECT * FROM acre_carpeta ca " +
			"LEFT JOIN usuario_carpeta uc ON uc.id_carpeta = ca.id_carpeta " +
			"LEFT JOIN usuario u ON u.id_usuario = uc.id_usuario " +
			"WHERE uc.id_usuario = ?1 and ca.estado !='X'" +
			"AND NOT EXISTS (SELECT 1 FROM acre_carpeta ca2 " +
			"WHERE ca2.c_padre = ca.id_carpeta);", nativeQuery = true)
	public List<Carpeta> getAllCarpetasUsuario(Long id_usuario);

	@Query(value = "select ca.*, uc.id_usuario from acre_carpeta ca " +
			"left join usuario_carpeta uc on uc.id_carpeta = ca.id_carpeta " +
			"left join usuario u on u.id_usuario = uc.id_usuario " +
			"left join acre_carrera c on c.id_carrera = ca.id_carrera " +
			"where uc.id_usuario = ?1 and ca.estado !='X' and ca.c_padre is null", nativeQuery = true)
	public List<Carpeta> getAllCarpetasNullPadreUsuario(Long id_usuario);

	@Query(value = "SELECT ca.* " +
			"FROM acre_carpeta ca " +
			"LEFT JOIN usuario_carpeta uc ON uc.id_carpeta = ca.id_carpeta " +
			"LEFT JOIN usuario u ON u.id_usuario = uc.id_usuario " +
			"LEFT JOIN acre_carrera c ON c.id_carrera = ca.id_carrera " +
			"WHERE uc.id_usuario = ?1 AND ca.c_padre = ?2 and ca.estado !='X'", nativeQuery = true)
	List<Carpeta> getCarpetasUsuarioYHijos(Long id_usuario, Long id_carpeta);

	@Query(value = "select * from acre_carpeta c where c.id_carpeta != ?1 and c.estado !='X' and nom_carpeta !='MATERIAS';", nativeQuery = true)
	List<Carpeta> listaDireccionesParaMoverArchivoAdmin(Long idCarpeta);

	@Query(value = "select * from acre_carpeta c " +
			"left join usuario_carpeta uc on uc.id_carpeta = c.id_carpeta " +
			"left join usuario u on u.id_usuario = uc.id_usuario " +
			"where c.id_carpeta != ?1 and c.estado != 'X' " +
			"and c.nom_carpeta != 'MATERIAS' and u.id_usuario = ?2", nativeQuery = true)
	List<Carpeta> listaDireccionesParaMoverArchivo(Long idCarpeta, Long idUsuario);

	@Query(value = "select * from acre_carpeta c where c.id_carpeta != ?1 and c.estado !='X' and nom_carpeta !='MATERIAS' and c.id_docente = ?2;", nativeQuery = true)
	List<Carpeta> listaDireccionesParaMoverArchivoDocente(Long idCarpeta, Long idDocente);
}
