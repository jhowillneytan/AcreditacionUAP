package com.uap.acreditacion.service;

import java.util.List;

import com.uap.acreditacion.entity.Carpeta;

public interface ICarpetaService {
    
    public List<Carpeta> findAll();

	public void save(Carpeta endidad);

	public Carpeta findOne(Long id);

	public void delete(Long id);

	List<Carpeta> listaCarpetasHijosPorIdCarpeta(Long idCarpeta);
	
	public List<Carpeta> getAllCarpetasCarrera(Long id_carrera);
	
	public List<Carpeta> getAllCarpetasUsuario(Long id_usuario);

	public List<Carpeta> getAllCarpetasNullPadreUsuario(Long id_usuario);

	List<Carpeta> getCarpetasUsuarioYHijos(Long id_usuario, Long id_carpeta);
	 
	List<Carpeta> listaDireccionesParaMoverArchivo(Long idCarpeta, Long idUsuario);

	List<Carpeta> listaDireccionesParaMoverArchivoAdmin(Long idCarpeta);

	List<Carpeta> listaDireccionesParaMoverArchivoDocente(Long idCarpeta, Long idDocente);
}
