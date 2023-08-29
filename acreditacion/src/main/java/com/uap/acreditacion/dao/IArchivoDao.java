package com.uap.acreditacion.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.uap.acreditacion.entity.Archivo;

public interface IArchivoDao extends CrudRepository<Archivo,Long>{
	
	@Query("select a from Archivo a where a.descripcion like %?1%")
	public List<Archivo> listaArchivosPorDescripcionAdmin(String descripcion);

	@Query(value = "select * from usuario u " +
			"left join acre_carpeta ac ON u.id_usuario = ac.id_usuario " +
			"left join acre_archivo aa on ac.id_carpeta = aa.id_carpeta " +
			"where aa.descripcion like %?1% and u.id_usuario = ?2", nativeQuery = true)
	public List<Archivo> listaArchivosPorDescripcion(String descripcion, Long id_usuario);
    
}
