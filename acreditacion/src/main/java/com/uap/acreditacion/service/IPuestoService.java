package com.uap.acreditacion.service;

import java.util.List;

import com.uap.acreditacion.entity.Puesto;

public interface IPuestoService {
    
    public List<Puesto> findAll();

	public void save(Puesto endidad);

	public Puesto findOne(Long id);

	public void delete(Long id);

}
