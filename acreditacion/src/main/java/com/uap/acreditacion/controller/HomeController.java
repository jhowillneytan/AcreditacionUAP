package com.uap.acreditacion.controller;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
 
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.uap.acreditacion.Config;
import com.uap.acreditacion.entity.Archivo;
import com.uap.acreditacion.entity.Carpeta;
import com.uap.acreditacion.entity.Carrera;

import com.uap.acreditacion.entity.Persona;
import com.uap.acreditacion.entity.TipoPersona;
import com.uap.acreditacion.entity.Usuario;
import com.uap.acreditacion.service.IArchivoService;
import com.uap.acreditacion.service.ICarpetaService;
import com.uap.acreditacion.service.ICarreraService;

import com.uap.acreditacion.service.IPersonaService;

import com.uap.acreditacion.service.ITipoPersonaService;
import com.uap.acreditacion.service.IUsuarioService;
import java.awt.image.BufferedImage;

@Controller

public class HomeController {

	Config config = new Config();

	@Autowired
	private ICarpetaService carpetaService;

	@Autowired
	private IArchivoService archivoService;

	/*
	 * @Autowired
	 * private ITipoArchivoService tipoArchivoService;
	 * 
	 * @Autowired
	 * private IEvaluadorService evaluadorService;
	 */
	@Autowired
	private ICarreraService carreraService;

	@Autowired
	private IPersonaService personaService;

	@Autowired
	private ITipoPersonaService tipoPersonaService;

	@Autowired
	private IUsuarioService usuarioService;

	@GetMapping(value = "/home")
	public String home(ModelMap model, HttpServletRequest request,
			@RequestParam(value = "id_carpeta", required = false) Long id_carpeta) {
		if (request.getSession().getAttribute("persona") != null) {
			Persona p2 = (Persona) request.getSession().getAttribute("persona");
			Persona p = personaService.findOne(p2.getId_persona());

			Calendar cal = Calendar.getInstance();
			int year = cal.get(Calendar.YEAR);

			if (p.getTipoPersona().getNom_tipo_persona().equals("Administrador")) {
				List<Carpeta> Listcarpetas = new ArrayList<>();
				List<Carpeta> listFill = carpetaService.findAll();
				for (int i = 0; i < listFill.size(); i++) {
					if (listFill.get(i).getCarpetaPadre() == null) {
						Listcarpetas.add(listFill.get(i));
					}
				}

				model.addAttribute("carpetas", Listcarpetas);
				model.addAttribute("menus", Listcarpetas);
				System.out.println("***********METODOD HOME");

				model.addAttribute("archivo", new Archivo());
				model.addAttribute("carpeta", new Carpeta());
				model.addAttribute("anterior", new Carpeta());
				model.addAttribute("ExisteArchivo", "true");
				model.addAttribute("usuarios", usuarioService.findAll());
			}
			if (p.getTipoPersona().getNom_tipo_persona().equals("Evaluador")) {
				List<Carpeta> Listcarpetas = new ArrayList<>();
				List<Carpeta> listFill = carpetaService.findAll();
				for (int i = 0; i < listFill.size(); i++) {
					if (listFill.get(i).getCarpetaPadre() == null) {
						Listcarpetas.add(listFill.get(i));
					}
				}
				model.addAttribute("carpetas", Listcarpetas);
				model.addAttribute("menus", Listcarpetas);
				System.out.println("***********METODOD HOME");

				model.addAttribute("archivo", new Archivo());
				model.addAttribute("carpeta", new Carpeta());
				model.addAttribute("anterior", new Carpeta());
				model.addAttribute("ExisteArchivo", "true");
				// model.addAttribute("usuarios", usuarioService.findAll());
			}
			if (p.getTipoPersona().getNom_tipo_persona().equals("Docente")) {

				List<Carpeta> ListcarpetasPadres = new ArrayList<>();
				List<Carpeta> carpetasConUsuario = new ArrayList<>();

				List<Carpeta> listFill = carpetaService.findAll();
				for (Carpeta carpeta : listFill) {
					if (carpeta.getCarpetaPadre() == null) {
						ListcarpetasPadres.add(carpeta);
					}
				}

				Usuario usuarioActual = p.getUsuario(); // Obtén el usuario actual de tu aplicación

				for (Carpeta carpetaPadre : ListcarpetasPadres) {
					List<Carpeta> carpetasEncontradas = buscarCarpetasConUsuario(carpetaPadre,
							usuarioActual);
					carpetasConUsuario.addAll(carpetasEncontradas);
				}

				// Ahora carpetasConUsuario contiene todas las carpetas que tienen al usuario
				// actual

				model.addAttribute("carpetas", carpetasConUsuario);
				model.addAttribute("menus", carpetasConUsuario);
				System.out.println("***********METODOD HOME");

				model.addAttribute("archivo", new Archivo());
				model.addAttribute("carpeta", new Carpeta());
				model.addAttribute("anterior", new Carpeta());
				model.addAttribute("ExisteArchivo", "true");
				List<Usuario> listUsuarios = new ArrayList<>();
				for (Persona persona : p.getCarrera().getPersonas()) {
					listUsuarios.add(persona.getUsuario());
				}
				model.addAttribute("usuarios", listUsuarios);
			}
			if (p.getTipoPersona().getNom_tipo_persona().equals("Director")) {

				List<Carpeta> ListcarpetasPadres = new ArrayList<>();
				List<Carpeta> carpetasConUsuario = new ArrayList<>();

				List<Carpeta> listFill = carpetaService.findAll();
				for (Carpeta carpeta : listFill) {
					if (carpeta.getCarpetaPadre() == null) {
						ListcarpetasPadres.add(carpeta);
					}
				}

				Usuario usuarioActual = p.getUsuario(); // Obtén el usuario actual de tu aplicación

				for (Carpeta carpetaPadre : ListcarpetasPadres) {
					List<Carpeta> carpetasEncontradas = buscarCarpetasConUsuario(carpetaPadre,
							usuarioActual);
					carpetasConUsuario.addAll(carpetasEncontradas);
				}

				// Ahora carpetasConUsuario contiene todas las carpetas que tienen al usuario
				// actual

				model.addAttribute("carpetas", carpetasConUsuario);
				model.addAttribute("menus", carpetasConUsuario);
				System.out.println("***********METODOD HOME");

				model.addAttribute("archivo", new Archivo());
				model.addAttribute("carpeta", new Carpeta());
				model.addAttribute("anterior", new Carpeta());
				model.addAttribute("ExisteArchivo", "true");
				List<Usuario> listUsuarios = new ArrayList<>();
				for (Persona persona : p.getCarrera().getPersonas()) {
					listUsuarios.add(persona.getUsuario());
				}
				model.addAttribute("usuarios", listUsuarios);
			}

			TipoPersona tipoPersona = tipoPersonaService.findOne(p.getTipoPersona().getId_tipo_persona());

			System.out.println("***********TIPO PERSONA: " + tipoPersona.getId_tipo_persona());
			model.addAttribute("personasession", p);
			model.addAttribute("tipoPersonasession", tipoPersona);

			return "home";
		} else {
			return "redirect:/login";
		}
	}

	public List<Carpeta> buscarCarpetasConUsuario(Carpeta carpetaActual, Usuario usuarioActual) {
		List<Carpeta> carpetasConUsuario = new ArrayList<>();

		if (carpetaActual.getUsuarios().contains(usuarioActual)) {
			carpetasConUsuario.add(carpetaActual);
		}

		if (!carpetasConUsuario.isEmpty()) {
			// Si ya encontraste carpetas con usuario, no sigas buscando en las carpetas
			// hijas.
			return carpetasConUsuario;
		}

		for (Carpeta carpetaHija : carpetaActual.getCarpetasHijos()) {
			List<Carpeta> carpetasHijosConUsuario = buscarCarpetasConUsuario(carpetaHija, usuarioActual);
			carpetasConUsuario.addAll(carpetasHijosConUsuario);

			if (!carpetasHijosConUsuario.isEmpty()) {
				// Si encontraste carpetas con usuario en las carpetas hijas, detén la búsqueda
				// en esta rama.
				break;
			}
		}

		return carpetasConUsuario;
	}

	@GetMapping("/home/{id_carpeta}")
	public String entrarCarpetas(@PathVariable(value = "id_carpeta") Long id_carpeta, ModelMap model,
			HttpServletRequest request) {
		if (request.getSession().getAttribute("persona") != null) {
			Persona p2 = (Persona) request.getSession().getAttribute("persona");
			Persona p = personaService.findOne(p2.getId_persona());

			Calendar cal = Calendar.getInstance();
			int year = cal.get(Calendar.YEAR);
			Carpeta carpeta = carpetaService.findOne(id_carpeta);
			model.addAttribute("personasession", p);
			model.addAttribute("tipoPersonasession",
					tipoPersonaService.findOne(p.getTipoPersona().getId_tipo_persona()));
			List<Carpeta> carpetas = carpeta.getCarpetasHijos();
			/*
			 * for (int i = 0; i < carpetas.size(); i++) {
			 * for (int j = 0; j < carpetas.get(i).getArchivos().size(); j++) {
			 * String nombA = carpetas.get(i).getArchivos().get(j).getFile();
			 * String[] ta2 = nombA.split("\\.");
			 * System.out.println("el NOMBRE DE ES: "+ta2[1]);
			 * carpetas.get(i).getArchivos().get(j).setTipoArchivo(ta2[1]);
			 * }
			 * }
			 */

			model.addAttribute("carpetas", carpetas);
			model.addAttribute("menus", carpetas);

			Archivo archivo = new Archivo();
			model.addAttribute("carpeta", new Carpeta());
			model.addAttribute("archivo", archivo);
			model.addAttribute("editMode", "true");
			model.addAttribute("anterior", carpeta);
			// model.addAttribute("carpetas", carpetas);
			model.addAttribute("ExisteCarpeta", carpetas.isEmpty());
			model.addAttribute("ExisteArchivo", carpeta.getArchivos().isEmpty());
			// model.addAttribute("TiposArchivos2", tipoArchivoService.findAll());
			// model.addAttribute("menus", menus);
			List<Carpeta> list = new ArrayList<Carpeta>();

			if (p.getTipoPersona().getNom_tipo_persona().equals("Administrador")) {

				list.add(carpeta);

				while (carpeta.getCarpetaPadre() != null) {
					list.add(carpeta.getCarpetaPadre());
					carpeta = carpeta.getCarpetaPadre();
				}
				Collections.reverse(list);
			}
			if (p.getTipoPersona().getNom_tipo_persona().equals("Evaluador")) {

				list.add(carpeta);
				while (carpeta.getCarpetaPadre() != null) {
					list.add(carpeta.getCarpetaPadre());
					carpeta = carpeta.getCarpetaPadre();
				}
				Collections.reverse(list);
			}
			if (p.getTipoPersona().getNom_tipo_persona().equals("Docente")) {

				list.add(carpeta);
				// List<Usuario> usuarios = new
				// ArrayList<>(carpeta.getCarpetaPadre().getUsuarios());

				/*
				 * while (carpeta.getCarpetaPadre() != null) {
				 * for (int i = 0; i < usuarios.size(); i++) {
				 * if (usuarios.get(i) == p.getUsuario()) {
				 * list.add(carpeta.getCarpetaPadre());
				 * carpeta = carpeta.getCarpetaPadre();
				 * }
				 * }
				 * }
				 */
				while (carpeta.getCarpetaPadre() != null) {
					list.add(carpeta.getCarpetaPadre());
					carpeta = carpeta.getCarpetaPadre();
				}
				Collections.reverse(list);
			}
			if (p.getTipoPersona().getNom_tipo_persona().equals("Director")) {

				list.add(carpeta);
				while (carpeta.getCarpetaPadre() != null) {
					list.add(carpeta.getCarpetaPadre());
					carpeta = carpeta.getCarpetaPadre();
				}
				Collections.reverse(list);
			}
			model.addAttribute("usuarios", usuarioService.findAll());
			model.addAttribute("list", list);
			return "home";
		} else {
			return "redirect:/login";
		}
	}

	@PostMapping("/guardar-carpeta")
	public String guardar(RedirectAttributes redirectAttrs, @Validated Carpeta carpeta,
			@RequestParam(name = "ruta_icon", required = false) MultipartFile ruta_icon,
			@RequestParam(value = "auxiliar", required = false) Long id_carpeta_anterior, HttpServletRequest request) {

		if (carpeta.getNom_carpeta() == null || carpeta.getNom_carpeta() == "" || carpeta.getDescripcion() == null
				|| carpeta.getDescripcion() == "") {
			redirectAttrs
					.addFlashAttribute("mensaje", "Se requiere llenar los campos")
					.addFlashAttribute("clase", "danger");
			return "redirect:/home/" + id_carpeta_anterior;
		}

		Calendar cal = Calendar.getInstance();
		int year = cal.get(Calendar.YEAR);
		Persona p2 = (Persona) request.getSession().getAttribute("persona");
		Persona persona = personaService.findOne(p2.getId_persona());

		/*
		 * if (id_carpeta_anterior != null) {
		 * carpeta.setCarpetaPadre(carpetaService.findOne(id_carpeta_anterior));
		 * }
		 */

		if (id_carpeta_anterior == null) {
			carpeta.setCarrera(persona.getCarrera());
		}

		if (persona.getTipoPersona().getNom_tipo_persona().equals("Personal")) {
			// Set<Usuario> usuarios = new HashSet<>();
			// usuarios.add(persona.getUsuario());
			// carpeta.setUsuarios2(usuarios);

		}
		if (id_carpeta_anterior != null) {
			carpeta.setCarpetaPadre(carpetaService.findOne(id_carpeta_anterior));
			// List<Usuario> listUsuarios = new ArrayList<>();
			Set<Usuario> usuarios = new HashSet<>();
			for (Usuario usuario : carpeta.getCarpetaPadre().getUsuarios()) {
				usuarios.add(usuario);
			}
			carpeta.setUsuarios(usuarios);

		}
		if (id_carpeta_anterior == null) {
			Set<Usuario> usuarios = new HashSet<>();
			usuarios.add(persona.getUsuario());
			carpeta.setUsuarios(usuarios);
		}

		if (id_carpeta_anterior != null) {
			if (!ruta_icon.isEmpty()) {
				String arch = config.guardarArchivo(ruta_icon);
				carpeta.setRuta_icono(arch);
				String[] ta = arch.split("\\.");
				for (String string : ta) {
					System.out.println(string);
				}
			} else {
				redirectAttrs
						.addFlashAttribute("mensaje", "Es necesario cargar un imagen")
						.addFlashAttribute("clase", "danger");
				return "redirect:/home/" + id_carpeta_anterior;
			}

			carpeta.setFecha_registro(new Date());
			// carpeta.setUsuario(persona.getUsuario());
			// System.out.println(carpeta.getUsuarios().get(0).getUsername());
			carpeta.setEstado("A");

			// for (Usuario usuario : carpeta.getUsuarios()) {
			// System.out.println("AAAAAAAAAA" + carpeta.getCarpetaPadre().getNom_carpeta()+
			// usuario.getUsername());
			// }
			carpetaService.save(carpeta);
			redirectAttrs
					.addFlashAttribute("mensaje", "Carpeta agregado correctamente")
					.addFlashAttribute("clase", "success");
			return "redirect:/home/" + id_carpeta_anterior;
		} else {
			if (!ruta_icon.isEmpty()) {
				String arch = config.guardarArchivo(ruta_icon);
				carpeta.setRuta_icono(arch);
				String[] ta = arch.split("\\.");
				for (String string : ta) {
					System.out.println(string);
				}
			} else {
				redirectAttrs
						.addFlashAttribute("mensaje", "Es necesario cargar un imagen")
						.addFlashAttribute("clase", "danger");
				return "redirect:/home/";
			}
			carpeta.setFecha_registro(new Date());
			carpeta.setEstado("A");
			carpetaService.save(carpeta);
			redirectAttrs
					.addFlashAttribute("mensaje", "Carpeta agregado correctamente")
					.addFlashAttribute("clase", "success");
			return "redirect:/home/";
		}

	}

	@PostMapping("/renombrar-carpeta")
	public String guardarCarpetaRenombrado(RedirectAttributes redirectAttrs,
			@RequestParam(value = "id_carpeta") Long id_carpeta,
			@RequestParam(value = "nom_carpeta") String nom_carpeta,
			@RequestParam(value = "descripcion") String descripcion,
			@RequestParam(name = "ruta_icorenombrar", required = false) MultipartFile ruta_ico) {
		Carpeta carpeta = carpetaService.findOne(id_carpeta);
		System.out.println(ruta_ico.getOriginalFilename() + "--------------------------");

		if (carpeta.getCarpetaPadre() != null) {
			if (ruta_ico.isEmpty()) {
				carpeta.setNom_carpeta(nom_carpeta);
				carpeta.setDescripcion(descripcion);
				carpetaService.save(carpeta);
				redirectAttrs
						.addFlashAttribute("mensaje", "Renombrado correctamente sin cambio de icono")
						.addFlashAttribute("clase", "success");
				return "redirect:/home/" + carpeta.getCarpetaPadre().getId_carpeta();

			} else {
				String arch = config.guardarArchivo(ruta_ico);
				carpeta.setRuta_icono(arch);
				String[] ta = arch.split("\\.");
				/*
				 * if (!ta[ta.length - 1].equals("png")) {
				 * redirectAttrs
				 * .addFlashAttribute("mensaje", "El archivo tiene que ser imagen png")
				 * .addFlashAttribute("clase", "danger");
				 * return "redirect:/home/" + carpeta.getCarpetaPadre().getId_carpeta();
				 * } else {
				 * carpeta.setNom_carpeta(nom_carpeta);
				 * carpeta.setDescripcion(descripcion);
				 * carpetaService.save(carpeta);
				 * redirectAttrs
				 * .addFlashAttribute("mensaje", "Renombrado correctamente con cambio de icono")
				 * .addFlashAttribute("clase", "success");
				 * return "redirect:/home/" + carpeta.getCarpetaPadre().getId_carpeta();
				 * }
				 */
				carpeta.setNom_carpeta(nom_carpeta);
				carpeta.setDescripcion(descripcion);
				carpetaService.save(carpeta);
				redirectAttrs
						.addFlashAttribute("mensaje", "Renombrado correctamente con cambio de icono")
						.addFlashAttribute("clase", "success");
				return "redirect:/home/" + carpeta.getCarpetaPadre().getId_carpeta();
			}

		} else {
			if (ruta_ico.isEmpty()) {
				carpeta.setNom_carpeta(nom_carpeta);
				carpeta.setDescripcion(descripcion);
				carpetaService.save(carpeta);
				redirectAttrs
						.addFlashAttribute("mensaje", "Renombrado correctamente sin cambio de icono")
						.addFlashAttribute("clase", "success");
				return "redirect:/home/";

			} else {
				String arch = config.guardarArchivo(ruta_ico);
				carpeta.setRuta_icono(arch);
				String[] ta = arch.split("\\.");
				/*
				 * if (!ta[ta.length - 1].equals("png")) {
				 * redirectAttrs
				 * .addFlashAttribute("mensaje", "El archivo tiene que ser imagen png")
				 * .addFlashAttribute("clase", "danger");
				 * return "redirect:/home/";
				 * } else {
				 * carpeta.setNom_carpeta(nom_carpeta);
				 * carpeta.setDescripcion(descripcion);
				 * carpetaService.save(carpeta);
				 * redirectAttrs
				 * .addFlashAttribute("mensaje", "Renombrado correctamente con cambio de icono")
				 * .addFlashAttribute("clase", "success");
				 * return "redirect:/home/";
				 * }
				 */
				carpeta.setNom_carpeta(nom_carpeta);
				carpeta.setDescripcion(descripcion);
				carpetaService.save(carpeta);
				redirectAttrs
						.addFlashAttribute("mensaje", "Renombrado correctamente con cambio de icono")
						.addFlashAttribute("clase", "success");
				return "redirect:/home/";
			}

		}

	}

	@GetMapping("/eliminar-carpeta/{id_carpeta}")
	public String eliminarCarpeta(@PathVariable(value = "id_carpeta") Long id_carpeta, ModelMap model,
			RedirectAttributes redirectAttrs) {
		Carpeta carpeta = carpetaService.findOne(id_carpeta);
		carpeta.setEstado("X");
		carpetaService.save(carpeta);

		redirectAttrs
				.addFlashAttribute("mensaje", "Carpeta Eliminado correctamente")
				.addFlashAttribute("clase", "success");
		System.out.println("carpeta eliminado");

		if (carpeta.getCarpetaPadre() != null) {
			return "redirect:/home/" + carpeta.getCarpetaPadre().getId_carpeta();
		} else {
			return "redirect:/home/";
		}

	}

	// ------------ASIGNAR USUARIOS A CARPETAS

	@PostMapping("/AsignarUsuarios")
	public String AsignarUsuarios(ModelMap model, RedirectAttributes redirectAttrs,
			@RequestParam(value = "usuarios") Long[] id_usuarios,
			@RequestParam(value = "id_carpeta") Long id_carpeta) {
		Carpeta carpeta = carpetaService.findOne(id_carpeta);

		for (int i = 0; i < id_usuarios.length; i++) {
			carpeta.getUsuarios().add(usuarioService.findOne(id_usuarios[i]));
		}

		carpetaService.save(carpeta);
		return "redirect:/home/";
	}

	// ------------ASIGNAR PERMISO A USUARIOS A CARPETAS
	@PostMapping("/QuitarUsuariosCarpeta")
	public String QuitarUsuariosCarpeta(ModelMap model, RedirectAttributes redirectAttrs,
			@RequestParam(value = "usuarios") Long[] id_usuarios,
			@RequestParam(value = "id_carpeta2") Long id_carpeta) {
		Carpeta carpeta = carpetaService.findOne(id_carpeta);

		for (int i = 0; i < id_usuarios.length; i++) {
			carpeta.getUsuarios().remove(usuarioService.findOne(id_usuarios[i]));
		}

		carpetaService.save(carpeta);
		return "redirect:/home/";
	}

	// ----------- FIN DEL METODO

	@PostMapping("/guardar-archivo")
	public String guardarArchivo(@Validated Archivo archivo, RedirectAttributes redirectAttrs,
			@RequestParam(name = "archivo", required = false) List<MultipartFile> file,
			@RequestParam(value = "auxiliar") Long id_carpeta_anterior) throws IOException {
		/*
		 * if (archivo.getNom_archivo() == null || archivo.getNom_archivo() == "" ||
		 * archivo.getDescripcion() == null
		 * || archivo.getDescripcion() == "") {
		 * redirectAttrs
		 * .addFlashAttribute("mensaje", "Se requiere llenar los campos")
		 * .addFlashAttribute("clase", "danger");
		 * return "redirect:/home/" + id_carpeta_anterior;
		 * }
		 */
		/*
		 * String NombValidos[] = { "FOTOCOPIA DE TITULO DE BACHILLER",
		 * "FOTOCOPIA DE C.I.",
		 * "FOTOCOPIA DE CERTIFICADO DE NACIMIENTO",
		 * "MODALIDAD DE INGRESO", "MATRICULA 1", "MATRICULA 2", "PROGRAMACIÓN", "FOTO"
		 * };
		 */
		String NombValidos[] = { "FOTOCOPIA DE TITULO DE BACHILLER",
				"FOTOCOPIA DE C.I.",
				"FOTOCOPIA DE CERTIFICADO DE NACIMIENTO",
				"MODALIDAD DE INGRESO", "MATRICULA", "PROGRAMACIÓN", "FOTO"
		};
		for (MultipartFile multipartFile : file) {
			Archivo archivo2 = new Archivo();
			if (id_carpeta_anterior != null) {
				archivo2.setCarpeta(carpetaService.findOne(id_carpeta_anterior));
			}
			String nombA = multipartFile.getOriginalFilename();
			String[] ta2 = nombA.split("\\.");
			String nombreSinExtension = obtenerNombreSinExtension(nombA);

			System.out.println("anterior " + id_carpeta_anterior);
			if (!multipartFile.isEmpty()) {
				String arch = config.guardarArchivo(multipartFile);
				// archivo.setContenido(file.getBytes());
				String[] ta = arch.split("\\.");
				archivo2.setFile(arch);
				archivo2.setNom_archivo(nombreSinExtension);
				archivo2.setTipoArchivo(ta2[ta2.length - 1]);
				archivo2.setDescripcion(archivo.getDescripcion());
				archivo2.setEstado("A");
				archivo2.setFecha_registro(new Date());
				for (String nombres : NombValidos) {
					// System.out.println("***************EL NOMBRE DEL ARCHIVO ES****"
					// +(archivo2.getNom_archivo()));
					if (archivo2.getNom_archivo().equals(nombres)) {
						archivoService.save(archivo2);
					}
				}
				// archivoService.save(archivo2);
				System.out.println("***************EL NOMBRE DEL ARCHIVO ES****" + archivo2.getNom_archivo());

				/*
				 * for (int i = 0; i < NombValidos.length; i++) {
				 * if (ta[0].equals(NombValidos[i])) {
				 * archivo2.setFile(arch);
				 * archivo2.setNom_archivo(ta[0]);
				 * archivo2.setDescripcion(archivo.getDescripcion());
				 * archivo2.setEstado("A");
				 * archivo2.setFecha_registro(new Date());
				 * archivoService.save(archivo2);
				 * } else{
				 * redirectAttrs
				 * .addFlashAttribute("mensaje", "HAY ARCHIVOS CON NOMBRE NO VALIDOS")
				 * .addFlashAttribute("clase", "danger");
				 * return "redirect:/home/" + id_carpeta_anterior;
				 * }
				 * }
				 */
			} else {
				redirectAttrs
						.addFlashAttribute("mensaje", "Es necesario cargar un archivo")
						.addFlashAttribute("clase", "danger");
				return "redirect:/home/" + id_carpeta_anterior;
			}

		}

		redirectAttrs
				.addFlashAttribute("mensaje", "Agregado correctamente")
				.addFlashAttribute("clase", "success");
		return "redirect:/home/" + id_carpeta_anterior;
	}

	private String obtenerNombreSinExtension(String nombreArchivo) {
		// Utilizar una expresión regular para dividir el nombre del archivo en función
		// del último punto
		int ultimoPuntoIndex = nombreArchivo.lastIndexOf(".");
		if (ultimoPuntoIndex != -1) {
			return nombreArchivo.substring(0, ultimoPuntoIndex);
		} else {
			// Si no se encuentra ningún punto, devolver el nombre completo
			return nombreArchivo;
		}
	}

	@GetMapping("/verIcoPdf/{id}")
	public ResponseEntity<byte[]> verIcoPdf(@PathVariable Long id) {
		Path projectPath = Paths.get("").toAbsolutePath();

		Archivo archivo = archivoService.findOne(id);
		// String rutaArchivo = projectPath + "\\acreditacion\\\\uploads\\" +
		// archivo.getFile();
		String rutaArchivo = projectPath + "/acreditacion/uploads/" + archivo.getFile();
		try {

			byte[] fileBytes;
			try (InputStream inputStream = new FileInputStream(rutaArchivo)) {
				ByteArrayOutputStream buffer = new ByteArrayOutputStream();
				int nRead;
				byte[] data = new byte[16384]; // Tamaño del búfer, puedes ajustarlo según tus necesidades
				while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
					buffer.write(data, 0, nRead);
				}
				buffer.flush();
				fileBytes = buffer.toByteArray();
			}

			// Cargar el documento PDF
			PDDocument document = PDDocument.load(fileBytes);

			// Obtener el renderizador PDF
			PDFRenderer renderer = new PDFRenderer(document);

			// Convertir la primera página a imagen
			BufferedImage image = renderer.renderImageWithDPI(0, 300); // Ajusta la resolución según tus necesidades

			// Crear un flujo de bytes para la imagen
			ByteArrayOutputStream baos = new ByteArrayOutputStream();

			// Guardar la imagen en formato JPG
			ImageIO.write(image, "jpg", baos);

			baos.flush();
			byte[] imageBytes = baos.toByteArray();
			baos.close();
			document.close();

			// Configurar los encabezados para evitar el caché
			HttpHeaders headers = new HttpHeaders();
			headers.setCacheControl("no-cache, no-store, must-revalidate");
			headers.setPragma("no-cache");
			headers.setExpires(0);

			return ResponseEntity.ok()
					.headers(headers)
					.contentType(MediaType.IMAGE_JPEG)
					.contentLength(imageBytes.length)
					.body(imageBytes);
		} catch (IOException e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
	/* aaaaaaaaaaaaaaaaa */

	@PostMapping("/renombrar-archivo")
	public String guardarRenombradoArchivo(RedirectAttributes redirectAttrs,
			@RequestParam(value = "id_archivo") Long id_archivo,
			@RequestParam(value = "nom_archivo") String nom_archivo,
			@RequestParam(value = "descripcion") String descripcion) {

		Archivo archivo = archivoService.findOne(id_archivo);
		Carpeta carpeta = carpetaService.findOne(archivo.getCarpeta().getId_carpeta());

		System.out.println("id carpeta= " + carpeta.getId_carpeta());
		System.out.println("id= " + id_archivo);
		System.out.println("nom= " + nom_archivo);
		System.out.println("desc= " + descripcion);

		archivo.setNom_archivo(nom_archivo);
		archivo.setDescripcion(descripcion);
		archivoService.save(archivo);

		redirectAttrs
				.addFlashAttribute("mensaje", "Renombrado correctamente")
				.addFlashAttribute("clase", "success");

		return "redirect:/home/" + carpeta.getId_carpeta();
	}

	@GetMapping("/eliminar-archivo/{id_archivo}")
	public String eliminarArchivo(@PathVariable(value = "id_archivo") Long id_archivo,
			RedirectAttributes redirectAttrs) {

		Archivo archivo = archivoService.findOne(id_archivo);
		archivo.setEstado("X");
		archivoService.save(archivo);

		Carpeta carpeta = carpetaService.findOne(archivo.getCarpeta().getId_carpeta());

		redirectAttrs
				.addFlashAttribute("mensaje", "Archivo Eliminado correctamente")
				.addFlashAttribute("clase", "success");

		return "redirect:/home/" + carpeta.getId_carpeta();
	}

	@GetMapping("home/archivo/{texto}")
	public String buscarArchivos(@PathVariable(value = "texto", required = false) String descripcion, ModelMap model,
			HttpServletRequest request) {
		System.out.println("des=" + descripcion);

		Persona p2 = (Persona) request.getSession().getAttribute("persona");
		Persona p = personaService.findOne(p2.getId_persona());
		if (p.getTipoPersona().getNom_tipo_persona().equals("Personal")) {
			model.addAttribute("archivos", archivoService.listaArchivosPorDescripcion(descripcion.toUpperCase(),
					p.getUsuario().getId_usuario()));
		}
		if (p.getTipoPersona().getNom_tipo_persona().equals("Administrador")) {
			model.addAttribute("archivos", archivoService.listaArchivosPorDescripcionAdmin(descripcion.toUpperCase()));
		}

		return "content :: content1";
	}

	@GetMapping("/vista")
	public String vista() {
		return "vista";
	}

	@PostMapping("/usuariosCarpeta/{id_carpeta}")
	public ResponseEntity<String[][]> obtenerUsuariosDeCarpeta(@PathVariable Long id_carpeta,
			HttpServletRequest request) {
		Carpeta carpeta = carpetaService.findOne(id_carpeta);
		if (carpeta == null) {
			return ResponseEntity.notFound().build();
		}

		Persona p2 = (Persona) request.getSession().getAttribute("persona");
		Persona p = personaService.findOne(p2.getId_persona());

		List<Persona> listpers = new ArrayList<>();
		if (p.getTipoPersona().getNom_tipo_persona().equals("Administrador")) {
			listpers = personaService.findAll();
		}
		if (p.getTipoPersona().getNom_tipo_persona().equals("Director")) {
			listpers = p.getCarrera().getPersonas();
		}
		List<Usuario> listUse = new ArrayList<>();
		for (Persona persona : listpers) {
			listUse.add(persona.getUsuario());
		}

		Set<Usuario> listaUsuarios = carpeta.getUsuarios();

		for (int i = 0; i < listUse.size(); i++) {
			for (Usuario usuario : listaUsuarios) {
				if (listUse.get(i) == usuario) {
					listUse.remove(i);
				}
			}
		}

		String[][] usuariosArray = new String[listUse.size()][3];

		int index = 0;
		for (Usuario usuario : listUse) {
			usuariosArray[index][0] = usuario.getUsername();
			usuariosArray[index][1] = String.valueOf(usuario.getId_usuario());
			usuariosArray[index][2] = String.valueOf(usuario.getEstado());
			index++;
		}

		return ResponseEntity.ok(usuariosArray);
	}

	@PostMapping("/usuariosCarpetaActuales/{id_carpeta}")
	public ResponseEntity<String[][]> usuariosCarpetaActuales(@PathVariable Long id_carpeta,
			HttpServletRequest request) {
		Carpeta carpeta = carpetaService.findOne(id_carpeta);

		Set<Usuario> listaUsuarios = carpeta.getUsuarios();

		String[][] usuariosArray = new String[listaUsuarios.size()][3];

		int index = 0;
		for (Usuario usuario : listaUsuarios) {
			usuariosArray[index][0] = usuario.getUsername();
			usuariosArray[index][1] = String.valueOf(usuario.getId_usuario());
			usuariosArray[index][2] = String.valueOf(usuario.getEstado());
			index++;
		}
		return ResponseEntity.ok(usuariosArray);
	}
}
