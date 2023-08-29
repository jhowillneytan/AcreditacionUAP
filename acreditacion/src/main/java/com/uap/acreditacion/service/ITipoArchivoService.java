package com.uap.acreditacion.service;

import java.util.List;
import java.util.Optional;

import com.uap.acreditacion.entity.TipoArchivo;

public interface ITipoArchivoService {
    
    public List<TipoArchivo> findAll();

	public void save(TipoArchivo endidad);

	public TipoArchivo findOne(Long id);

	public void delete(Long id);
	
	public TipoArchivo getTipoArchivo(String tipo);

    public Optional<TipoArchivo> findOneOptional(Long id);

}
