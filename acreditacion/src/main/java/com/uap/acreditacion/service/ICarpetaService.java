package com.uap.acreditacion.service;

import java.util.List;

import com.uap.acreditacion.entity.Carpeta;

public interface ICarpetaService {
    
    public List<Carpeta> findAll();

	public void save(Carpeta endidad);

	public Carpeta findOne(Long id);

	public void delete(Long id);
	
	public List<Carpeta> getAllCarpetasCarrera(Long id_carrera);
	
	//public List<Carpeta> getAllCarpetasUsuario(Long id_usuario);
	
}
