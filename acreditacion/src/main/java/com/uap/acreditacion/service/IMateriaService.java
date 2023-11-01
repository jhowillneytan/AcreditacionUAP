package com.uap.acreditacion.service;

import java.util.List;

import com.uap.acreditacion.entity.Materia;

public interface IMateriaService {
    public List<Materia> findAll();

    public void save(Materia materia);

    public Materia findOne(Long id);

    public void delete(Long id);

    public Materia materiaNombre(String nombre);
}
