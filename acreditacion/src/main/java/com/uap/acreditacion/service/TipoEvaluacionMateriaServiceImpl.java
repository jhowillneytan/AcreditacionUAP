package com.uap.acreditacion.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uap.acreditacion.dao.TipoEvaluacionMateriaDao;
import com.uap.acreditacion.entity.TipoEvaluacionMateria;

@Service
public class TipoEvaluacionMateriaServiceImpl implements TipoEvaluacionMateriaService{

    @Autowired
    private TipoEvaluacionMateriaDao evaluacionMateriaDao;

    @Override
    public List<TipoEvaluacionMateria> findAll() {
        return (List<TipoEvaluacionMateria>) evaluacionMateriaDao.findAll();
    }

    @Override
    public void save(TipoEvaluacionMateria endidad) {
        evaluacionMateriaDao.save(endidad);
    }

    @Override
    public TipoEvaluacionMateria findOne(Long id) {
        return evaluacionMateriaDao.findById(id).orElse(null);
    }

    @Override
    public void delete(Long id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

    @Override
    public TipoEvaluacionMateria evaluacionMateriaPorNombre(String nombre) {
        return evaluacionMateriaDao.evaluacionMateriaPorNombre(nombre);
    }
    
}
