package com.uap.acreditacion.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uap.acreditacion.dao.ITipoArchivoDao;
import com.uap.acreditacion.entity.TipoArchivo;

@Service
public class TipoArchivoServiceImpl implements ITipoArchivoService{

    @Autowired
    private ITipoArchivoDao tipoArchivoDao;

    @Override
    public List<TipoArchivo> findAll() {
        // TODO Auto-generated method stub
        return (List<TipoArchivo>) tipoArchivoDao.findAll();
    }

    @Override
    public void save(TipoArchivo endidad) {
        // TODO Auto-generated method stub
        tipoArchivoDao.save(endidad);
    }

    @Override
    public TipoArchivo findOne(Long id) {
        // TODO Auto-generated method stub
        return tipoArchivoDao.findById(id).orElse(null);
    }

    @Override
    public void delete(Long id) {
        // TODO Auto-generated method stub
        tipoArchivoDao.deleteById(id);
    }

	@Override
	public TipoArchivo getTipoArchivo(String tipo) {
		// TODO Auto-generated method stub
		return tipoArchivoDao.getTipoArchivo(tipo);
	}

    @Override
    public Optional<TipoArchivo> findOneOptional(Long id) {
        // TODO Auto-generated method stub
        return tipoArchivoDao.findById(id);
    }
    
}
