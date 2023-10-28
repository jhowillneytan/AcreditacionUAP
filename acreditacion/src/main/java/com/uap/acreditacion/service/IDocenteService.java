package com.uap.acreditacion.service;

import java.util.List;

import com.uap.acreditacion.entity.Docente;

public interface IDocenteService {
    
    public List<Docente> findAll();

	public void save(Docente docente);

	public Docente findOne(Long id);
 
	public void delete(Long id);
}
