package com.uap.acreditacion.service;

import java.util.List;

import com.uap.acreditacion.entity.Facultad;

public interface IFacultadService {
    
    public List<Facultad> findAll();

	public void save(Facultad endidad);

	public Facultad findOne(Long id);
	
	public void delete(Long id);

	public Facultad facultadPorNombre(String nombreFacultad);

}
