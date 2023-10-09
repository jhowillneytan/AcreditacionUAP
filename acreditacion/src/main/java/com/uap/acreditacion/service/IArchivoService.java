package com.uap.acreditacion.service;

import java.util.List;
import java.util.Optional;

import com.uap.acreditacion.entity.Archivo;

public interface IArchivoService {
    
    public List<Archivo> findAll();

	public void save(Archivo endidad);

	public Archivo findOne(Long id);

	public void delete(Long id);
	
	public List<Archivo> listaArchivosPorDescripcion(String descripcion, Long id_usuario);
	public List<Archivo> listaArchivosPorDescripcionAdmin(String descripcion);

	public Optional<Archivo> findOneOptional(Long id);

	public List<Archivo> archivoParametro(Long id_parametro, Long id_carpeta, Long id_materia);
 
}
