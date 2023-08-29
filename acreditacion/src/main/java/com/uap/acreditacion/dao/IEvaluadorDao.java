package com.uap.acreditacion.dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.uap.acreditacion.entity.Evaluador;

public interface IEvaluadorDao extends CrudRepository<Evaluador, Long>{
	
	@Query("select e from Evaluador e left join e.persona p where p.id_persona=?1 and e.gestion=?2")
	public Evaluador getEvaluadorPersonaGestion(Long id_persona, Integer gestion);
    
}
