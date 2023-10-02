package com.uap.acreditacion.controller;

import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.weaver.patterns.PerObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.uap.acreditacion.Config;
import com.uap.acreditacion.entity.Carrera;
import com.uap.acreditacion.entity.Persona;
import com.uap.acreditacion.entity.TipoPersona;
import com.uap.acreditacion.entity.Usuario;
import com.uap.acreditacion.service.ICargoService;
import com.uap.acreditacion.service.ICarreraService;
import com.uap.acreditacion.service.IPersonaService;
import com.uap.acreditacion.service.IPuestoService;
import com.uap.acreditacion.service.ITipoPersonaService;
import com.uap.acreditacion.service.IUsuarioService;

@Controller
@RequestMapping(value = "/persona")
public class PersonaController {

	Config config = new Config();

	@Autowired
	private ITipoPersonaService tipoPersonaService;
	@Autowired
	private IPersonaService personaService;

	@Autowired
	private IUsuarioService usuarioService;

	@Autowired
	private ICarreraService carreraService;

	@Autowired
	private ICargoService cargoService;

	@Autowired
	private IPuestoService iPuestoService;

	@GetMapping("/form-persona")
	public String formPersona(ModelMap model, HttpServletRequest request) {
		if (request.getSession().getAttribute("persona") != null) {

			Persona p2 = (Persona) request.getSession().getAttribute("persona");
			Persona p = personaService.findOne(p2.getId_persona());
			model.addAttribute("personasession", p);
			model.addAttribute("tipoPersonasession",
					tipoPersonaService.findOne(p.getTipoPersona().getId_tipo_persona()));
			List<TipoPersona> listaTipoPersonas = tipoPersonaService.findAll();
			List<Persona> listaPersonas = personaService.findAll();
			model.addAttribute("persona", new Persona());
			model.addAttribute("listaPersonas", listaPersonas);
			model.addAttribute("listaCarreras", carreraService.findAll());
			model.addAttribute("listaCargos", cargoService.findAll());
			model.addAttribute("listaPuesto", iPuestoService.findAll());
			model.addAttribute("listaTipoPersonas", listaTipoPersonas);
			return "persona";
		} else {
			return "redirect:/login"; 
		}
	}

	@PostMapping("/RegistrarPersona")
	public String RegistrarPersona(ModelMap model, RedirectAttributes redirectAttrs, RedirectAttributes flash,
			@Validated Persona persona,
			@RequestParam(value = "Foto", required = false) MultipartFile file) {
		persona.setEstado("A");
		if (!file.isEmpty()) {
			String arch = config.guardarArchivo(file);
			persona.setImagen_persona(arch);
			String[] ta = arch.split("\\.");
			for (String string : ta) {
				System.out.println(string);
			}
		} else {
			flash
					.addFlashAttribute("mensaje", "Es necesario cargar un imagen")
					.addFlashAttribute("clase", "danger");
			return "redirect:/carrera/formulario/";
		}
		persona.setFecha_registro(new Date());

		personaService.save(persona);
		flash.addAttribute("success", "Registro Exitoso!");
		return "redirect:/persona/form-persona";
	}

	@PostMapping("/ModificarPersona")
	public String ModificarPersona(ModelMap model, RedirectAttributes redirectAttrs, RedirectAttributes flash,
			@Validated Persona persona,
			@RequestParam(value = "Foto", required = false) MultipartFile file) {
		Persona persona2 = personaService.findOne(persona.getId_persona());
		persona.setEstado("A");
		if (file.isEmpty()) {
			persona.setImagen_persona(persona2.getImagen_persona());
		} else {
			String arch = config.guardarArchivo(file);
			persona.setImagen_persona(arch);
			String[] ta = arch.split("\\.");
			for (String string : ta) {
				System.out.println(string);
			}
		}
		persona.setFecha_registro(persona2.getFecha_registro());

		personaService.save(persona);
		flash.addAttribute("success", "Modificacion Exitosa!");
		return "redirect:/persona/form-persona";
	}

	/*@PostMapping("/agregar-persona")
	public String agregarPersona(ModelMap model, RedirectAttributes redirectAttrs,
			@RequestParam(value = "nombre") String nombre,
			@RequestParam(value = "ap_paterno") String ap_paterno,
			@RequestParam(value = "ap_materno") String ap_materno,
			@RequestParam(value = "ci") String ci,
			// @RequestParam(value="fecha_registro")@DateTimeFormat(pattern =
			// "yyyy-MM-dd")Date fecha_registro,
			@RequestParam(value = "imagen_persona") MultipartFile file,
			// @RequestParam(value="estado")String estado,
			@RequestParam(value = "tipoPersona") TipoPersona tipoPersona,

			@RequestParam(value = "tipoPersona") Set<TipoPersona> tipoPerson,
			@Validated Persona persona) {

		if (nombre == "" || ap_materno == "" || ap_materno == "" || ci == "") {
			redirectAttrs
					.addFlashAttribute("mensaje", "Se requiere llenar los campos")
					.addFlashAttribute("clase", "danger");
			return "redirect:/persona/form-persona";
		}
		// System.out.println("ar= "+file);
		if (!file.isEmpty()) {
			String arch = config.guardarArchivo(file);
			persona.setImagen_persona(arch);
			String[] ta = arch.split("\\.");
			for (String string : ta) {
				System.out.println(string);
			}
		} else {
			persona.setImagen_persona("FotoPersonDefaul.png");
		}
		persona.setNombre(nombre);
		persona.setAp_paterno(ap_paterno);
		persona.setAp_materno(ap_materno);
		persona.setCi(ci);
		persona.setFecha_registro(new Date());
		persona.setEstado("A");
		persona.setTipoPersona(tipoPersona);
		personaService.save(persona);
		redirectAttrs
				.addFlashAttribute("mensaje", "Agregado correctamente")
				.addFlashAttribute("clase", "success");
		return "redirect:/persona/form-persona";
	}*/

	@GetMapping("/editar-persona/{id_persona}")
	public String getIdPersona(@PathVariable(value = "id_persona") Long id_persona, ModelMap model,
			HttpServletRequest request) {
		if (request.getSession().getAttribute("persona") != null) {

			Persona p2 = (Persona) request.getSession().getAttribute("persona");
			Persona p = personaService.findOne(p2.getId_persona());
			model.addAttribute("personasession", p);
			model.addAttribute("tipoPersonasession", tipoPersonaService.findOne(p.getTipoPersona().getId_tipo_persona()));
			Persona persona = personaService.findOne(id_persona);
			model.addAttribute("persona", persona);
			model.addAttribute("editMode", "true");
			List<TipoPersona> listaTipoPersonas = tipoPersonaService.findAll();
			List<Persona> listaPersonas = personaService.findAll();
			model.addAttribute("listaPersonas", listaPersonas);
			model.addAttribute("listaCarreras", carreraService.findAll());
			model.addAttribute("listaCargos", cargoService.findAll());
			model.addAttribute("listaPuesto", iPuestoService.findAll());
			model.addAttribute("listaTipoPersonas", listaTipoPersonas);
			return "persona";
		} else {
			return "redirect:/login";
		}
	}

	@GetMapping("/cancelar-editar-persona")
	public String cancelarEditarPersona() {
		return "redirect:/persona/form-persona";
	}

	@PostMapping("/guardar-editado-persona")
	public String guardarEditadoPersona(ModelMap model, RedirectAttributes redirectAttrs,
			@RequestParam(value = "nombre") String nombre,
			@RequestParam(value = "ap_paterno") String ap_paterno,
			@RequestParam(value = "ap_materno") String ap_materno,
			@RequestParam(value = "ci") String ci,
			@RequestParam(value = "fecha_registro") @DateTimeFormat(pattern = "yyyy-MM-dd") Date fecha_registro,
			@RequestParam(value = "imagen_persona", required = false) MultipartFile file,
			@RequestParam(value = "estado") String estado,
			@RequestParam(value = "tipoPersona") TipoPersona tipoPersona,
			@RequestParam(value = "tipoPersona") Set<TipoPersona> tipoPerson,
			@RequestParam(value = "id_persona") Long id_persona) {
		Persona persona = personaService.findOne(id_persona);
		if (nombre == "" || ap_materno == "" || ap_materno == "" || ci == "") {
			redirectAttrs
					.addFlashAttribute("mensaje", "Se requiere llenar los campos")
					.addFlashAttribute("clase", "danger");
			return "redirect:/persona/editar-persona/" + persona.getId_persona();
		}

		System.out.println("ar= " + file);
		if (!file.isEmpty()) {
			String arch = config.guardarArchivo(file);
			persona.setImagen_persona(arch);
			String[] ta = arch.split("\\.");
			for (String string : ta) {
				System.out.println(string);
			}
		}
		persona.setNombre(nombre);
		persona.setAp_paterno(ap_paterno);
		persona.setAp_materno(ap_materno);
		persona.setCi(ci);
		// persona.setUsername(nombre);
		// String contra = bCryptPasswordEncoder.encode("admin");
		// persona.setPassword("admin");
		persona.setFecha_registro(fecha_registro);
		persona.setEstado(estado);
		persona.setTipoPersona(tipoPersona);
		personaService.save(persona);
		redirectAttrs
				.addFlashAttribute("mensaje", "Actualizado correctamente")
				.addFlashAttribute("clase", "success");
		return "redirect:/persona/form-persona";
	}

	@GetMapping("/eliminar-persona/{id_persona}")
	public String eliminarPersona(@PathVariable(value = "id_persona") Long id_persona,
			RedirectAttributes redirectAttrs) {
		//personaService.delete(id_persona);
		Persona persona = personaService.findOne(id_persona);
		if (persona.getUsuario() != null) {
			Usuario usuario = persona.getUsuario();
			usuario.setEstado("X");
			usuarioService.save(usuario);
		}
		persona.setEstado("X");
		personaService.save(persona);
		redirectAttrs
				.addFlashAttribute("mensaje", "Eliminado correctamente")
				.addFlashAttribute("clase", "danger");
		return "redirect:/persona/form-persona";
	}

}
