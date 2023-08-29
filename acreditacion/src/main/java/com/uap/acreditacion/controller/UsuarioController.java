package com.uap.acreditacion.controller;

import java.util.Calendar;
import java.util.List;
import java.util.Set;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.uap.acreditacion.dao.IUsuarioDao;
import com.uap.acreditacion.entity.Archivo;
import com.uap.acreditacion.entity.Carpeta;
import com.uap.acreditacion.entity.Evaluador;
import com.uap.acreditacion.entity.Persona;
import com.uap.acreditacion.entity.TipoPersona;
import com.uap.acreditacion.entity.Usuario;
import com.uap.acreditacion.service.EmailServiceImpl;
import com.uap.acreditacion.service.ICarpetaService;
import com.uap.acreditacion.service.IPersonaService;
import com.uap.acreditacion.service.ITipoPersonaService;
import com.uap.acreditacion.service.IUsuarioService;

@Controller
public class UsuarioController {
    @Autowired
    private IUsuarioService iUsuarioService;

	@Autowired
	private ITipoPersonaService tipoPersonaService;

	@Autowired
	private IPersonaService iPersonaService;

	@Autowired
	private IPersonaService personaService;
	
	@Autowired
	private ICarpetaService carpetaService;

	@Autowired
	private EmailServiceImpl emailServiceImpl;

    @GetMapping("/form-usuario")
	public String formUsuario(ModelMap model, HttpServletRequest request){
		if (request.getSession().getAttribute("persona") != null) {
			Persona p2 = (Persona) request.getSession().getAttribute("persona");
			Persona p = personaService.findOne(p2.getId_persona());
    	Calendar cal= Calendar.getInstance();
    	int year= cal.get(Calendar.YEAR);

		TipoPersona tipoPersona = tipoPersonaService.findOne(p.getTipoPersona().getId_tipo_persona());
		model.addAttribute("personasession", p);
		model.addAttribute("tipoPersonasession", tipoPersona);
    	model.addAttribute("usuario", new Usuario());
		model.addAttribute("usuarios", iUsuarioService.findAll());
		model.addAttribute("carpetas", carpetaService.findAll());
    	model.addAttribute("ExisteArchivo", "true");
		model.addAttribute("personasUser", iPersonaService.findAll());
    	return "/Usuarios/formulario";
		} else {
			return "redirect:/login";
		}
	}

	@PostMapping("/RegistrarUsuario")
	public String RegistrarUsuario(ModelMap model, HttpServletRequest request,@Validated Usuario usuario) throws MessagingException{
		if (request.getSession().getAttribute("persona") != null) {
			Persona p = (Persona) request.getSession().getAttribute("persona");
    	Calendar cal= Calendar.getInstance();
    	int year= cal.get(Calendar.YEAR);
		//Carpeta carpeta = carpetaService.findOne(id_carpeta);
		//System.out.println("la carpeta es: "+carpeta.getNom_carpeta());
		TipoPersona tipoPersona = tipoPersonaService.findOne(p.getTipoPersona().getId_tipo_persona());
		model.addAttribute("personasession", p);
		model.addAttribute("tipoPersonasession", tipoPersona);
    	model.addAttribute("usuario", new Usuario());
		model.addAttribute("usuarios", iUsuarioService.findAll());
    	model.addAttribute("ExisteArchivo", "true");
		model.addAttribute("personasUser", iPersonaService.findAll());
		usuario.setEstado("A");
		Set<Carpeta> carpetas = usuario.getCarpetas();
		
		//carpeta.getUsuarios().add(usuario);
		//carpetaService.save(carpeta);

		//Set<Usuario> usuarios = carpetas.
		//usuario.setCarpetas(carpetas);
		//usuario.getCarpetas().add(carpeta);
		iUsuarioService.save(usuario);
		//p.setEmail("destinatario@example.com"); // Establecer la dirección de correo de la persona
        emailServiceImpl.enviarEmail(usuario.getPersona().getEmail(), "Confirmacion", "Tu Usuario es: "+usuario.getUsername()+" y tu contrasena es: "+usuario.getPassword());
		//System.out.println("el email destino es: "+usuario.getPersona().getEmail());
    	return "redirect:/form-usuario";
		} else {
			return "redirect:/login";
		}
	}

	@GetMapping("/ModUsuario/{id_usuario}")
	public String ModUsuario(ModelMap model, HttpServletRequest request,
	@PathVariable("id_usuario")Long id_usuario){
		if (request.getSession().getAttribute("persona") != null) {
			Persona p2 = (Persona) request.getSession().getAttribute("persona");
			Persona p = personaService.findOne(p2.getId_persona());
    	Calendar cal= Calendar.getInstance();
    	int year= cal.get(Calendar.YEAR);

		TipoPersona tipoPersona = tipoPersonaService.findOne(p.getTipoPersona().getId_tipo_persona());
		model.addAttribute("personasession", p);
		model.addAttribute("tipoPersonasession", tipoPersona);
    	model.addAttribute("usuario", iUsuarioService.findOne(id_usuario));
		model.addAttribute("usuarios", iUsuarioService.findAll());
		model.addAttribute("carpetas", carpetaService.findAll());
    	model.addAttribute("ExisteArchivo", "true");
		model.addAttribute("personasUser", iPersonaService.findAll());
    	return "/Usuarios/formulario";
		} else {
			return "redirect:/login";
		}
	}

	@PostMapping("/ModificarUsuario")
	public String ModificarUsuario(ModelMap model, HttpServletRequest request,@Validated Usuario usuario,
	@RequestParam(value = "carpeta")Long id_carpeta) throws MessagingException{
		if (request.getSession().getAttribute("persona") != null) {
			Persona p = (Persona) request.getSession().getAttribute("persona");
    	Calendar cal= Calendar.getInstance();
    	int year= cal.get(Calendar.YEAR);
		Carpeta carpeta = carpetaService.findOne(id_carpeta);
		TipoPersona tipoPersona = tipoPersonaService.findOne(p.getTipoPersona().getId_tipo_persona());
		model.addAttribute("personasession", p);
		model.addAttribute("tipoPersonasession", tipoPersona);
    	model.addAttribute("usuario", new Usuario());
		model.addAttribute("usuarios", iUsuarioService.findAll());
    	model.addAttribute("ExisteArchivo", "true");
		model.addAttribute("personasUser", iPersonaService.findAll());
		//carpeta.setUsuario(usuario);
		iUsuarioService.save(usuario);

		//p.setEmail("destinatario@example.com"); // Establecer la dirección de correo de la persona
        //emailServiceImpl.enviarEmail(usuario.getPersona().getEmail(), "Confirmacion", "Tu Usuario es: "+usuario.getUsername()+" y tu contrasena es: "+usuario.getPassword());
		//System.out.println("el email destino es: "+usuario.getPersona().getEmail());
    	return "redirect:/form-usuario";
		} else {
			return "redirect:/login";
		}
	}

	@GetMapping("/EliminarUsuario/{id_usuario}")
	public String EliminarUsuario(ModelMap model, HttpServletRequest request,
	@PathVariable("id_usuario")Long id_usuario){
		if (request.getSession().getAttribute("persona") != null) {
			Persona p2 = (Persona) request.getSession().getAttribute("persona");
			Persona p = personaService.findOne(p2.getId_persona());
    	Calendar cal= Calendar.getInstance();
    	int year= cal.get(Calendar.YEAR);
		Usuario usuarioE =  iUsuarioService.findOne(id_usuario);
		TipoPersona tipoPersona = tipoPersonaService.findOne(p.getTipoPersona().getId_tipo_persona());
		model.addAttribute("personasession", p);
		model.addAttribute("tipoPersonasession", tipoPersona);
    	model.addAttribute("usuario", new Usuario());
		model.addAttribute("usuarios", iUsuarioService.findAll());
		model.addAttribute("carpetas", carpetaService.findAll());
    	model.addAttribute("ExisteArchivo", "true");
		model.addAttribute("personasUser", iPersonaService.findAll());

		/*carpeta.setUsuario(usuario);
		iUsuarioService.save(usuario);*/
    	return "/Usuarios/formulario";
		} else {
			return "redirect:/login";
		}
	}

}
