package com.uap.acreditacion.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.uap.acreditacion.dao.IRequisitoDao;
import com.uap.acreditacion.entity.Requisito;

public class RequisitoService implements IRequisitoService{
    @Autowired
    private IRequisitoDao requisitoDao;

    @Override
    public List<Requisito> findAll() {
        return (List<Requisito>) requisitoDao.findAll();
    }

    @Override
    public void save(Requisito requisito) {
        requisitoDao.save(requisito);
    }

    @Override
    public Requisito findOne(Long id) {
        return requisitoDao.findById(id).orElse(null);
    }

    @Override
    public void delete(Long id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }
    
}
