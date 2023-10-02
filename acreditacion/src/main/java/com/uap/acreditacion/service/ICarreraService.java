package com.uap.acreditacion.service;

import java.util.List;

import com.uap.acreditacion.entity.Carrera;

public interface ICarreraService {
    
    public List<Carrera> findAll();

	public void save(Carrera endidad);

	public Carrera findOne(Long id);
 
	public void delete(Long id);

}
