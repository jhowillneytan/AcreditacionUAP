package com.uap.acreditacion.controller;

import java.security.Principal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
//import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.uap.acreditacion.Config;
import com.uap.acreditacion.dao.IPersonaDao;
import com.uap.acreditacion.dao.IUsuarioDao;
import com.uap.acreditacion.entity.Cargo;
import com.uap.acreditacion.entity.Persona;
import com.uap.acreditacion.entity.TipoPersona;
import com.uap.acreditacion.entity.Usuario;
import com.uap.acreditacion.service.ICargoService;
import com.uap.acreditacion.service.IPersonaService;
import com.uap.acreditacion.service.ITipoPersonaService;
import com.uap.acreditacion.service.IUsuarioService;

@Controller
public class IndexController {
	Config config = new Config();
	@Autowired
	private IPersonaService personaService;

	@Autowired
	private ITipoPersonaService tipoPersonaService;

	@Autowired
	private ICargoService iCargoService;

	@Autowired
	private IUsuarioService iUsuarioService; 

	@GetMapping({"/login","/"})
	public String login(@RequestParam(value="error", required=false) String error,
			@RequestParam(value="logout", required = false) String logout,
			Model model, Principal principal, RedirectAttributes flash
			) {
		
		//String username=auth.getName();
		
		
		if(principal != null) {
			flash.addFlashAttribute("info", "Ya ha inciado sesión anteriormente");
			return "redirect:/home";
		}
		
		if(error != null) {
			model.addAttribute("error", "Error en el login: Nombre de usuario o contraseña incorrecta, por favor vuelva a intentarlo!");
		}
		
		if(logout != null) {
			model.addAttribute("success", "Ha cerrado sesión con éxito!");
		}
		
		return "index";
	}

	@PostMapping("/login")
	public String login(RedirectAttributes redirectAttrs,
			@RequestParam(value="username")String username,
    		@RequestParam(value="password")String password, HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		session = request.getSession(true);

		Usuario usuario = iUsuarioService.findByUsernamePassword(username, password);
		//Cargo cargo = iCargoService.findOne(usuario.getPersona().getCargo().getId_cargo());
		if (usuario!=null) {
			session.setAttribute("persona", usuario.getPersona());
			//session.setAttribute("carreraSesion", usuario.getPersona().getCarrera());
			session.setAttribute("tipo_persona", usuario.getPersona().getTipoPersona());
			//session.setAttribute("cargoSesion", cargo);
			return "redirect:/home";
		}else {
			redirectAttrs
	        .addFlashAttribute("mensaje", "Error en el login: Nombre de usuario o contraseña incorrecta, por favor vuelva a intentarlo!")
	        .addFlashAttribute("clase", "light")
			.addFlashAttribute("sty", "red")
			.addFlashAttribute("col", "#d09f8373");
			return "redirect:/login";
		}
	}
	@GetMapping("/logout")
	public String logout(HttpServletRequest request, RedirectAttributes redirectAttrs) {
		HttpSession session = request.getSession();
		session.invalidate();
		System.out.println("entrando a cerrar");
		redirectAttrs
        .addFlashAttribute("mensaje", "Ha cerrado sesión con éxito!")
        .addFlashAttribute("clase", "light")
        .addFlashAttribute("sty", "green")
		.addFlashAttribute("col", "#8ecdaa73");
		return "redirect:/login";
	}
	
	
	@GetMapping("/mi-perfil/{id_persona}")
	public String miPerfil(@PathVariable(value="id_persona")Long id_persona, ModelMap model, HttpServletRequest request){
		if (request.getSession().getAttribute("persona") != null) {
			Persona p2 = (Persona) request.getSession().getAttribute("persona");
			Persona p = personaService.findOne(p2.getId_persona());
			model.addAttribute("personasession", p);
			model.addAttribute("tipoPersonasession", tipoPersonaService.findOne(p.getTipoPersona().getId_tipo_persona()));

		Persona persona = personaService.findOne(id_persona);
		model.addAttribute("perfil", persona);
		
		model.addAttribute("editMode", "true");
		return "mi_perfil";
		} else {
			return "redirect:/login";
		}
	}

	@PostMapping("/guardarPefilEditado")
	public String guardarPefilEditado(ModelMap model, RedirectAttributes redirectAttrs,
			@RequestParam(value="nombre")String nombre,
			@RequestParam(value="ap_paterno")String ap_paterno,
			@RequestParam(value="ap_materno")String ap_materno,
			@RequestParam(value="ci")String ci,
			//@RequestParam(value="fecha_registro")String fech_registro,
			//@RequestParam(value="imagen_personaN")String imagen_personaN,
			@RequestParam(name="imagen_persona", required = false) MultipartFile file,
			//@RequestParam(value="estado")String estado,
			//@RequestParam(value="tipoPersona")TipoPersona tipoPersona,
			//@RequestParam(value="tipoPersona")Set<TipoPersona> tipoPerson,
			@RequestParam(value="id_persona")Long id_persona) {

				System.out.println("METODO MODIFICAR PERFIL");
		Persona persona = personaService.findOne(id_persona);
		
		System.out.println("ar= "+file);
		if (!file.isEmpty()) { 
    		String arch = config.guardarArchivo(file);	
    		persona.setImagen_persona(arch);
    		String [] ta = arch.split("\\.");
    		System.out.println(ta[ta.length-1]);
			if (!ta[ta.length-1].equals("png")) {
				redirectAttrs
	            .addFlashAttribute("mensaje", "El archivo tiene que ser en formato .png")
	            .addFlashAttribute("clase", "danger");
	    		return "redirect:/mi-perfil/"+persona.getId_persona();
	    	}
		} else{
			//persona.setImagen_persona(imagen_personaN);
		}
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		persona.setNombre(nombre);
		persona.setAp_paterno(ap_paterno);
		persona.setAp_materno(ap_materno);
		persona.setCi(ci);
		/*try {
		persona.setFecha_registro(dateFormat.parse(fech_registro));	
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		persona.setEstado(estado);
		persona.setTipoPersona(tipoPersona);
		persona.setTipoPerson(tipoPerson);*/
		personaService.save(persona);
		redirectAttrs
        .addFlashAttribute("mensaje", "Actualizado correctamente")
        .addFlashAttribute("clase", "success");
		return "redirect:/home";
	}
	
	
}
