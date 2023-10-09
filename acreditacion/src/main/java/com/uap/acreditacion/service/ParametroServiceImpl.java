package com.uap.acreditacion.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uap.acreditacion.dao.IParametroDao;
import com.uap.acreditacion.entity.Parametro;

@Service
public class ParametroServiceImpl implements IParametroService{

    @Autowired
    private IParametroDao parametroDao;

    @Override
    public List<Parametro> findAll() {
        return (List<Parametro>) parametroDao.findAll();
    }

    @Override
    public void save(Parametro parametro) {
        parametroDao.save(parametro);
    }

    @Override
    public Parametro findOne(Long id) {
        return parametroDao.findById(id).orElse(null);
    }

    @Override
    public void delete(Long id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

    @Override
    public List<Parametro> listaParametro2(Long id_carpeta, Long id_materia, Long id_requisito) {
        return parametroDao.listaParametro2(id_carpeta, id_materia, id_requisito);
    }
    
}
