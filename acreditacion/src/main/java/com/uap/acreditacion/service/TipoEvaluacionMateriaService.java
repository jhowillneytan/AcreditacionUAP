package com.uap.acreditacion.service;

import java.util.List;

import com.uap.acreditacion.entity.TipoEvaluacionMateria;

public interface TipoEvaluacionMateriaService {
    public List<TipoEvaluacionMateria> findAll();

	public void save(TipoEvaluacionMateria endidad);

	public TipoEvaluacionMateria findOne(Long id);
	
	public void delete(Long id);

    public TipoEvaluacionMateria evaluacionMateriaPorNombre(String nombre);
}
