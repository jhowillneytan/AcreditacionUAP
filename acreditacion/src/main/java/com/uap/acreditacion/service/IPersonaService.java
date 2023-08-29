package com.uap.acreditacion.service;

import java.util.List;
import java.util.Optional;

import com.uap.acreditacion.entity.Persona;

public interface IPersonaService {
    
    public List<Persona> findAll();

	public void save(Persona endidad);

	public Persona findOne(Long id);

	public void delete(Long id);

}
