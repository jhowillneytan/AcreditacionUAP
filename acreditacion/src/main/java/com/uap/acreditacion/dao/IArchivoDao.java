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
    
	@Query(value = "select * from acre_archivo aa " +  
        "left join parametro_archivo pa on aa.id_archivo = pa.id_archivo " +  
        "left join parametro p on p.id_parametro = pa.id_parametro " + 
        "left join requisitos r on r.id_requisito  = p.id_requisito " +  
        "left join requisito_materia rm on rm.id_requisito = r.id_requisito " +  
        "left join materia m on m.id_materia = rm.id_materia " +  
        "left join acre_carpeta ac on ac.id_carpeta = m.id_carpeta " +  
        "where aa.id_carpeta = ac.id_carpeta and aa.id_materia = m.id_materia " + 
        "and p.id_parametro = ?1 and ac.id_carpeta = ?2 and m.id_materia = ?3", nativeQuery = true)
public List<Archivo> archivoParametro(Long id_parametro, Long id_carpeta, Long id_materia);


}
