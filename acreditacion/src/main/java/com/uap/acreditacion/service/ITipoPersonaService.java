package com.uap.acreditacion.service;

import java.util.List;

import com.uap.acreditacion.entity.TipoPersona;

public interface ITipoPersonaService {
    
    public List<TipoPersona> findAll();

	public void save(TipoPersona endidad);

	public TipoPersona findOne(Long id);

	public void delete(Long id);

}
