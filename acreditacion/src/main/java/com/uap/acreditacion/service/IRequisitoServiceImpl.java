package com.uap.acreditacion.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uap.acreditacion.dao.IRequisitoDao;
import com.uap.acreditacion.entity.Requisito;

@Service
public class IRequisitoServiceImpl implements IRequisitoService{

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

    @Override
    public List<Requisito> listaRequisitosMateria2(Long id_materia, Long id_carpeta) {
        return requisitoDao.listaRequisitosMateria2(id_materia, id_carpeta);
    }
    
}
