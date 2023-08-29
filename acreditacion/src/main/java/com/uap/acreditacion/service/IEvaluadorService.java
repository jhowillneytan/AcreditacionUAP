package com.uap.acreditacion.service;

import java.util.List;

import com.uap.acreditacion.entity.Evaluador;

public interface IEvaluadorService {
    
    public List<Evaluador> findAll();

	public void save(Evaluador endidad);

	public Evaluador findOne(Long id);

	public void delete(Long id);
	
	public Evaluador getEvaluadorPersonaGestion(Long id_persona, Integer gestion);

}
