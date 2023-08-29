package com.uap.acreditacion.dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.uap.acreditacion.entity.TipoArchivo;

public interface ITipoArchivoDao extends CrudRepository<TipoArchivo, Long>{
	
	@Query("select ta from TipoArchivo ta where ta.nom_tipo_archivo=?1")
	public TipoArchivo getTipoArchivo(String tipo);
    
}
