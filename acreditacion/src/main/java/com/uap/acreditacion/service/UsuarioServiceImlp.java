package com.uap.acreditacion.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uap.acreditacion.dao.IUsuarioDao;
import com.uap.acreditacion.entity.Usuario;

@Service
public class UsuarioServiceImlp implements IUsuarioService{
    
    @Autowired
    private IUsuarioDao iUsuarioDao;

    @Override
    public List<Usuario> findAll() {
        return (List<Usuario>) iUsuarioDao.findAll();
    }

    @Override
    public void save(Usuario endidad) {
        iUsuarioDao.save(endidad);
    }

    @Override
    public Usuario findOne(Long id) {
        return iUsuarioDao.findById(id).orElse(null);
    }

    @Override
    public void delete(Long id) {
        iUsuarioDao.deleteById(id);
    }

    @Override
    public Usuario findByUsernamePassword(String username, String password) {
        return iUsuarioDao.findByUsernamePassword(username, password);
    }

    @Override
    public Usuario findByUsername(String username) {
       return iUsuarioDao.findByUsername(username);
    }

    @Override
    public Usuario usuarioPorIdPersona(Long idPersona) {
        return iUsuarioDao.usuarioPorIdPersona(idPersona);
    }

    @Override
    public List<Usuario> listaUsuarioPorIdCarpeta(Long idCarpeta) {
        return iUsuarioDao.listaUsuarioPorIdCarpeta(idCarpeta);
    }

    @Override
    public List<Usuario> listaUsuarios() {
        return iUsuarioDao.listaUsuarios();
    }

    @Override
    public List<Usuario> listaUsuarioPorIdCarrera(Long idCarrera) {
        return iUsuarioDao.listaUsuarioPorIdCarrera(idCarrera);
    }

}
