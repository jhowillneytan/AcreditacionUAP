package com.uap.acreditacion.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uap.acreditacion.dao.IDocenteDao;
import com.uap.acreditacion.entity.Docente;

@Service
public class DocenteServiceImpl implements IDocenteService{

    @Autowired
    private IDocenteDao docenteDao;

    @Override
    public List<Docente> findAll() {
        return (List<Docente>) docenteDao.findAll();
    }

    @Override
    public void save(Docente docente) {
        docenteDao.save(docente);
    }

    @Override
    public Docente findOne(Long id) {
        return docenteDao.findById(id).orElse(null);
    }

    @Override
    public void delete(Long id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

    @Override
    public Docente docenteRD(String rd) {
        return docenteDao.docenteRD(rd);
    }
    
}
