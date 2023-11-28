package com.uap.acreditacion.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uap.acreditacion.dao.IMateriaDao;
import com.uap.acreditacion.entity.Materia;


@Service
public class MateriaServiceImpl implements IMateriaService{

    @Autowired
    private IMateriaDao materiaDao;

    @Override
    public List<Materia> findAll() {
        return (List<Materia>) materiaDao.findAll();
    }

    @Override
    public void save(Materia materia) {
        materiaDao.save(materia);
    }

    @Override
    public Materia findOne(Long id) {
        return materiaDao.findById(id).orElse(null);
    }

    @Override
    public void delete(Long id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

    @Override
    public Materia materiaNombre(String nombre) {
        return materiaDao.materiaNombre(nombre);
    }

    @Override
    public Materia materiaSigla(String sigla) {
        return materiaDao.materiaSigla(sigla);
    }
    
}
