package com.uap.acreditacion.controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.uap.acreditacion.entity.Docente;
import com.uap.acreditacion.entity.Persona;
import com.uap.acreditacion.entity.TipoPersona;
import com.uap.acreditacion.entity.Usuario;
import com.uap.acreditacion.service.EmailServiceImpl;
import com.uap.acreditacion.service.IDocenteService;
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
	private EmailServiceImpl emailServiceImpl;

	@Autowired
	private IDocenteService docenteService;

	@GetMapping("/form-usuario")
	public String formUsuario(ModelMap model, HttpServletRequest request) {
		if (request.getSession().getAttribute("persona") != null) {
			Persona p2 = (Persona) request.getSession().getAttribute("persona");
			Persona p = personaService.findOne(p2.getId_persona());
			Calendar cal = Calendar.getInstance();
			int year = cal.get(Calendar.YEAR);
			List<Usuario> listUsuarios = iUsuarioService.findAll();
			/*
			 * List<Usuario> listUsuarios = new ArrayList<>();
			 * for (Persona persona : p.getCarrera().getPersonas()) {
			 * listUsuarios.add(persona.getUsuario());
			 * }
			 */
			TipoPersona tipoPersona = tipoPersonaService.findOne(p.getTipoPersona().getId_tipo_persona());
			model.addAttribute("personasession", p);
			model.addAttribute("tipoPersonasession", tipoPersona);
			model.addAttribute("usuario", new Usuario());

			if (tipoPersona.getNom_tipo_persona().equals("Administrador")) {
				model.addAttribute("docentes", docenteService.findAll());
				model.addAttribute("usuarios", iUsuarioService.findAll());
				model.addAttribute("personasUser", iPersonaService.findAll());
			} else {
				List<Persona> Listpersona = p.getCarrera().getPersonas();
				List<Docente> docentes = new ArrayList<>();
				for (Persona persona : Listpersona) {
					if (persona.getDocente() != null) {
						docentes.add(persona.getDocente());
					}
				}
				model.addAttribute("usuarios", listUsuarios);
				model.addAttribute("personasUser", p.getCarrera().getPersonas());
				model.addAttribute("docentes", docentes);
			}

			// model.addAttribute("carpetas", carpetaService.findAll());
			model.addAttribute("ExisteArchivo", "true");
			// model.addAttribute("personasUser",p.getCarrera().getPersonas());
			model.addAttribute("opcionUsuario", "true");
			model.addAttribute("subMenuSeleccionado", "true");
			return "/Usuarios/formulario";
		} else {
			return "redirect:/login";
		}
	}

	@PostMapping("/RegistrarUsuario")
	public String RegistrarUsuario(ModelMap model, HttpServletRequest request, @Validated Usuario usuario)
			throws MessagingException {
		if (request.getSession().getAttribute("persona") != null) {

			usuario.setEstado("A");
			usuario.setFecha_registro(new Date());
			iUsuarioService.save(usuario);
			String mensaje = "\n" +
					"Tu Usuario es: " + usuario.getUsername() + "\n Contrasena es: " + usuario.getPassword() + "\n " +
					"Link: http://virtual.uap.edu.bo:8383/login";
			emailServiceImpl.enviarEmail(usuario.getPersona().getEmail(),
					"Bienvenido al Sistema de Acreditacion Señor: " + usuario.getPersona().getNombre() + " "
							+ usuario.getPersona().getAp_paterno(),
					mensaje);

			return "redirect:/form-usuario";
		} else {
			return "redirect:/login";
		}
	}

	@GetMapping("/ModUsuario/{id_usuario}")
	public String ModUsuario(ModelMap model, HttpServletRequest request,
			@PathVariable("id_usuario") Long id_usuario) {
		if (request.getSession().getAttribute("persona") != null) {
			Persona p2 = (Persona) request.getSession().getAttribute("persona");
			Persona p = personaService.findOne(p2.getId_persona());
			Calendar cal = Calendar.getInstance();
			int year = cal.get(Calendar.YEAR);

			TipoPersona tipoPersona = tipoPersonaService.findOne(p.getTipoPersona().getId_tipo_persona());
			model.addAttribute("personasession", p);
			model.addAttribute("tipoPersonasession", tipoPersona);
			model.addAttribute("usuario", iUsuarioService.findOne(id_usuario));
			// model.addAttribute("carpetas", carpetaService.findAll());
			model.addAttribute("ExisteArchivo", "true");
			if (tipoPersona.getNom_tipo_persona().equals("Administrador")) {
				model.addAttribute("usuarios", iUsuarioService.findAll());
				model.addAttribute("personasUser", iPersonaService.findAll());
			} else {
				List<Usuario> listUsuarios = iUsuarioService.findAll();
				/*
				 * List<Usuario> listUsuarios = new ArrayList<>();
				 * for (Persona persona : p.getCarrera().getPersonas()) {
				 * listUsuarios.add(persona.getUsuario());
				 * }
				 */
				model.addAttribute("usuarios", listUsuarios);
				model.addAttribute("personasUser", p.getCarrera().getPersonas());
			}
			model.addAttribute("editMode", "true");
			model.addAttribute("opcionUsuario", "true");
			model.addAttribute("subMenuSeleccionado", "true");
			return "/Usuarios/formulario";
		} else {
			return "redirect:/login";
		}
	}

	@PostMapping("/ModificarUsuario")
	public String ModificarUsuario(ModelMap model, HttpServletRequest request, @Validated Usuario usuario)
			throws MessagingException {
		if (request.getSession().getAttribute("persona") != null) {
			Usuario usuario2 = iUsuarioService.findOne(usuario.getId_usuario());
			usuario.setEstado(usuario2.getEstado());
			// usuario.setPersona(usuario2.getPersona());
			iUsuarioService.save(usuario);

			return "redirect:/form-usuario";
		} else {
			return "redirect:/login";
		}
	}

	@GetMapping("/EliminarUsuario/{id_usuario}")
	public String EliminarUsuario(ModelMap model, HttpServletRequest request,
			@PathVariable("id_usuario") Long id_usuario) {
		if (request.getSession().getAttribute("persona") != null) {
			Usuario usuario = iUsuarioService.findOne(id_usuario);
			usuario.setEstado("X");
			iUsuarioService.save(usuario);
			return "redirect:/form-usuario";
		} else {
			return "redirect:/login";
		}
	}

	@PostMapping("/BuscarUsuario/")
	public ResponseEntity<String> BuscarUsuario(@Validated Usuario usuario) {
		String mensaje = "";
		// Usuario usuario2 = iUsuarioService.findByUsername(usuario.getUsername());

		if (iUsuarioService.findByUsername(usuario.getUsername()) == null) {
			mensaje = "registra";
		} else {
			mensaje = "Ya existe un Usuario con este nombre";
		}
		return ResponseEntity.ok(mensaje);
	}

}
