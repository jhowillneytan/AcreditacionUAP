package com.uap.acreditacion.service;

import java.util.List;

import com.uap.acreditacion.entity.Cargo;

public interface ICargoService {
    
    public List<Cargo> findAll();

	public void save(Cargo endidad);

	public Cargo findOne(Long id);

	public void delete(Long id);

}
