package com.uap.acreditacion.controller;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
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
import org.dom4j.DocumentException;
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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.uap.acreditacion.Config;
import com.uap.acreditacion.dao.IArchivoDao;
import com.uap.acreditacion.dao.IParametroDao;
import com.uap.acreditacion.dao.IPersonaDao;
import com.uap.acreditacion.dao.IRequisitoDao;
import com.uap.acreditacion.entity.Archivo;
import com.uap.acreditacion.entity.Carpeta;
import com.uap.acreditacion.entity.Carrera;
import com.uap.acreditacion.entity.Materia;
import com.uap.acreditacion.entity.Parametro;
import com.uap.acreditacion.entity.Persona;
import com.uap.acreditacion.entity.Requisito;
import com.uap.acreditacion.entity.TipoPersona;
import com.uap.acreditacion.entity.Usuario;
import com.uap.acreditacion.service.IArchivoService;
import com.uap.acreditacion.service.ICarpetaService;
import com.uap.acreditacion.service.ICarreraService;
import com.uap.acreditacion.service.IMateriaService;
import com.uap.acreditacion.service.IParametroService;
import com.uap.acreditacion.service.IPersonaService;
import com.uap.acreditacion.service.IRequisitoService;
import com.uap.acreditacion.service.ITipoPersonaService;
import com.uap.acreditacion.service.IUsuarioService;

import java.awt.image.BufferedImage;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;

import com.itextpdf.text.Element;

import com.itextpdf.text.FontFactory;

import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPCellEvent;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;

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

	@Autowired
	private IMateriaService materiaService;

	@Autowired
	private IRequisitoService requisitoService;

	@Autowired
	private IParametroService parametroService;

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
			// model.addAttribute("requisitos", requisitoService.findAll());
			model.addAttribute("materia", new Materia());
			model.addAttribute("opcionHome", "true");
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
			List<Carpeta> carp = carpeta.getCarpetasHijos();
			for (Carpeta carpeta2 : carp) {
				if (carpeta2.getNom_carpeta().equals("FILE DOCENTES")) {
					model.addAttribute("GestionCarpeta", carpeta2.getCarpetasHijos());
				}

			}

			model.addAttribute("usuarios", usuarioService.findAll());
			model.addAttribute("list", list);
			model.addAttribute("requisitosList", requisitoService.findAll());
			model.addAttribute("materia", new Materia());
			model.addAttribute("materias", carpetaService.findOne(id_carpeta).getMaterias());
			model.addAttribute("opcionHome", "true");
			return "home";
		} else {
			return "redirect:/login";
		}
	}

	@PostMapping("/guardar-carpeta")
	public String guardar(RedirectAttributes redirectAttrs, @Validated Carpeta carpeta,
			@RequestParam(name = "ruta_icon", required = false) MultipartFile ruta_icon,
			@RequestParam(value = "auxiliar", required = false) Long id_carpeta_anterior, HttpServletRequest request) {

		/*
		 * if (carpeta.getNom_carpeta() == null || carpeta.getNom_carpeta() == "" ||
		 * carpeta.getDescripcion() == null
		 * || carpeta.getDescripcion() == "") {
		 * redirectAttrs
		 * .addFlashAttribute("mensaje", "Se requiere llenar los campos")
		 * .addFlashAttribute("clase", "danger");
		 * return "redirect:/home/" + id_carpeta_anterior;
		 * }
		 */

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

	/*
	 * @PostMapping("/guardar-archivo")
	 * public String guardarArchivo(@Validated Archivo archivo, RedirectAttributes
	 * redirectAttrs,
	 * 
	 * @RequestParam(name = "archivo", required = false) List<MultipartFile> file,
	 * 
	 * @RequestParam(value = "auxiliar") Long id_carpeta_anterior,
	 * 
	 * @RequestParam(value = "idParametro", required = false) Long id_parametro)
	 * throws IOException {
	 * List<Archivo> archivoS = new ArrayList<>();
	 * 
	 * String NombValidos[] = { "FOTOCOPIA DE TITULO DE BACHILLER",
	 * "FOTOCOPIA DE C.I.",
	 * "FOTOCOPIA DE CERTIFICADO DE NACIMIENTO",
	 * "MODALIDAD DE INGRESO", "MATRICULA", "PROGRAMACIÓN", "FOTO"
	 * };
	 * for (MultipartFile multipartFile : file) {
	 * Archivo archivo2 = new Archivo();
	 * if (id_carpeta_anterior != null) {
	 * archivo2.setCarpeta(carpetaService.findOne(id_carpeta_anterior));
	 * }
	 * String nombA = multipartFile.getOriginalFilename();
	 * String[] ta2 = nombA.split("\\.");
	 * String nombreSinExtension = obtenerNombreSinExtension(nombA);
	 * 
	 * System.out.println("anterior " + id_carpeta_anterior);
	 * if (!multipartFile.isEmpty()) {
	 * String arch = config.guardarArchivo(multipartFile);
	 * // archivo.setContenido(file.getBytes());
	 * String[] ta = arch.split("\\.");
	 * archivo2.setFile(arch);
	 * archivo2.setNom_archivo(nombreSinExtension);
	 * archivo2.setTipoArchivo(ta2[ta2.length - 1]);
	 * archivo2.setDescripcion(archivo.getDescripcion());
	 * archivo2.setEstado("A");
	 * archivo2.setFecha_registro(new Date());
	 * 
	 * for (String nombres : NombValidos) {
	 * // System.out.println("***************EL NOMBRE DEL ARCHIVO ES****"
	 * // +(archivo2.getNom_archivo()));
	 * if (archivo2.getNom_archivo().equals(nombres)) {
	 * archivoService.save(archivo2);
	 * 
	 * }
	 * }
	 * // archivoService.save(archivo2);
	 * System.out.println("***************EL NOMBRE DEL ARCHIVO ES****" +
	 * archivo2.getNom_archivo());
	 * 
	 * 
	 * } else {
	 * redirectAttrs
	 * .addFlashAttribute("mensaje", "Es necesario cargar un archivo")
	 * .addFlashAttribute("clase", "danger");
	 * return "redirect:/home/" + id_carpeta_anterior;
	 * }
	 * 
	 * }
	 * 
	 * redirectAttrs
	 * .addFlashAttribute("mensaje", "Agregado correctamente")
	 * .addFlashAttribute("clase", "success");
	 * return "redirect:/home/" + id_carpeta_anterior;
	 * }
	 */
	@PostMapping("/guardar-archivo")
	public String guardarArchivo(@Validated Archivo archivo, RedirectAttributes redirectAttrs,
			@RequestParam(name = "archivo", required = false) List<MultipartFile> file,
			@RequestParam(value = "auxiliar") Long id_carpeta_anterior,
			@RequestParam(value = "idParametro", required = false) Long id_parametro,
			@RequestParam(value = "idMateria", required = false) Long id_materia,
			@RequestParam(value = "idRequisito", required = false) Long id_requisito) throws IOException {

		Parametro parametro = parametroService.findOne(id_parametro);
		Set<Parametro> parametros = new HashSet<>();
		String direccion = "redirect:/home/";
		for (MultipartFile multipartFile : file) {
			Archivo archivo2 = new Archivo();
			if (id_carpeta_anterior != null) {
				archivo2.setCarpeta(carpetaService.findOne(id_carpeta_anterior));
				direccion = "redirect:/home/" + id_carpeta_anterior;
			}
			if (id_materia != null) {
				archivo2.setMateria(materiaService.findOne(id_materia));
				direccion = "redirect:/RequisitosMateria/" + id_carpeta_anterior + "/" + id_materia + "/";
			}
			if (id_requisito != null) {
				// archivo2.setMateria(materiaService.findOne(id_materia));
				direccion = "redirect:/ParametroRequisitosMateria/" + id_carpeta_anterior + "/" + id_materia + "/"
						+ id_requisito;
			}
			String nombA = multipartFile.getOriginalFilename();
			String[] ta2 = nombA.split("\\.");
			String nombreSinExtension = obtenerNombreSinExtension(nombA);

			System.out.println("anterior " + id_carpeta_anterior);
			if (!multipartFile.isEmpty()) {
				String arch = config.guardarArchivo(multipartFile);

				archivo2.setFile(arch);

				archivo2.setNom_archivo(nombreSinExtension);
				archivo2.setTipoArchivo(ta2[ta2.length - 1]);
				archivo2.setDescripcion(archivo.getDescripcion());
				archivo2.setEstado("A");
				archivo2.setFecha_registro(new Date());

				parametros.add(parametro);
				archivo2.setParametros(parametros);

				archivoService.save(archivo2);

			} else {
				redirectAttrs
						.addFlashAttribute("mensaje", "Es necesario cargar un archivo")
						.addFlashAttribute("clase", "danger");
				return direccion;
			}

		}

		redirectAttrs
				.addFlashAttribute("mensaje", "Agregado correctamente")
				.addFlashAttribute("clase", "success");
		return direccion;
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

			// Obtener la mitad superior de la imagen
			int height = image.getHeight();
			int width = image.getWidth();
			BufferedImage topHalfImage = image.getSubimage(0, 0, width, height / 3);

			// Crear un flujo de bytes para la imagen
			ByteArrayOutputStream baos = new ByteArrayOutputStream();

			// Guardar la imagen en formato JPG
			ImageIO.write(topHalfImage, "jpg", baos);

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

	@PostMapping("/eliminar-archivo2/{id_archivo}")
	@ResponseBody
	public void eliminarArchivo2(@PathVariable(value = "id_archivo") Long id_archivo) {

		Archivo archivo = archivoService.findOne(id_archivo);
		archivo.setEstado("X");
		archivoService.save(archivo);
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

	@PostMapping("/ListMaterias")
	public ResponseEntity<String[][]> ListMaterias(
			HttpServletRequest request) {

		List<Materia> materias = materiaService.findAll();

		String[][] materiaArray = new String[materias.size()][3];

		int index = 0;
		for (Materia materia : materias) {
			materiaArray[index][0] = materia.getNombre();
			materiaArray[index][1] = String.valueOf(materia.getId_materia());
			materiaArray[index][2] = String.valueOf(materia.getEstado());
			index++;
		}
		return ResponseEntity.ok(materiaArray);
	}

	/*
	 * @PostMapping("/GuardarMateria")
	 * public ResponseEntity<String> GuardarMateria(@Validated Materia materia,
	 * 
	 * @RequestParam(value = "id_carpetaD")Long id_carpeta){
	 * try {
	 * materia.setCarpeta(carpetaService.findOne(id_carpeta));
	 * materiaService.save(materia);
	 * return ResponseEntity.ok("Se ha Guardado la Materia");
	 * } catch (Exception e) {
	 * return ResponseEntity.ok("Error: "+e);
	 * }
	 * 
	 * }
	 */
	@PostMapping("/GuardarMateria")
	public String GuardarMateria(@Validated Materia materia,
			@RequestParam(value = "id_carpetaD") Long id_carpeta) {

		materia.setCarpeta(carpetaService.findOne(id_carpeta));
		materiaService.save(materia);
		return "redirect:/home/" + id_carpeta;
	}

	@GetMapping("/RequisitosMateria/{id_carpeta}/{id_materia}/")
	public String RequisitosMateria(@PathVariable(value = "id_carpeta") Long id_carpeta,
			@PathVariable(value = "id_materia") Long id_materia,
			ModelMap model,
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

			model.addAttribute("carpetas", carpetas);
			model.addAttribute("menus", carpetas);

			Archivo archivo = new Archivo();
			model.addAttribute("carpeta", new Carpeta());
			model.addAttribute("archivo", archivo);
			model.addAttribute("editMode", "true");
			model.addAttribute("anterior", carpeta);

			model.addAttribute("ExisteCarpeta", carpetas.isEmpty());
			model.addAttribute("ExisteArchivo", carpeta.getArchivos().isEmpty());

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
			// model.addAttribute("requisitos",
			// materiaService.findOne(id_materia).getRequisitos());
			// model.addAttribute("requisitosM",
			// materiaService.findOne(id_materia).getRequisitos());
			model.addAttribute("requisitos", requisitoService.listaRequisitosMateria2(id_materia, id_carpeta));
			model.addAttribute("materia", new Materia());
			model.addAttribute("materias", carpetaService.findOne(id_carpeta).getMaterias());
			model.addAttribute("opcionMSelect", id_materia);
			model.addAttribute("opcionHome", "true");
			return "home";
		} else {
			return "redirect:/login";
		}
	}

	@GetMapping("/ParametroRequisitosMateria/{id_carpeta}/{id_materia}/{id_requisito}")
	public String ParametroRequisitosMateria(@PathVariable(value = "id_carpeta") Long id_carpeta,
			@PathVariable(value = "id_materia") Long id_materia,
			@PathVariable(value = "id_requisito") Long id_requisito,
			ModelMap model,
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
			model.addAttribute("requisitos", requisitoService.listaRequisitosMateria2(id_materia, id_carpeta));
			// model.addAttribute("requisitosM",
			// iRequisitoDao.listaRequisitosMateria(id_materia));
			model.addAttribute("materia", new Materia());

			model.addAttribute("materias", carpetaService.findOne(id_carpeta).getMaterias());
			List<Parametro> parametros = parametroService.listaParametro2(id_carpeta, id_materia, id_requisito);
			for (Parametro parametro : parametros) {
				parametro.getArchivos().clear();
			}
			for (Parametro parametro : parametros) {
				parametro.setArchivos(
						archivoService.archivoParametro(parametro.getId_parametro(), id_carpeta, id_materia));
			}
			for (Parametro parametro : parametros) {
				List<Archivo> archivosAEliminar = new ArrayList<>();

				for (Archivo archivo2 : parametro.getArchivos()) {
					if (archivo2.getEstado().equals("X")) {
						archivosAEliminar.add(archivo2);
					}
				}

				parametro.getArchivos().removeAll(archivosAEliminar);
			}

			model.addAttribute("parametrosR", parametros);
			model.addAttribute("opcionRselect", id_requisito);
			model.addAttribute("opcionMSelect", id_materia);
			model.addAttribute("opcionHome", "true");
			return "home";
		} else {
			return "redirect:/login";
		}
	}

	@PostMapping("/CargarRequisitos/")
	public ResponseEntity<String[][]> CargarRequisitos() {
		List<Requisito> requisitos = requisitoService.findAll();
		String[][] materiaArray = new String[requisitos.size()][2];
		int index = 0;
		for (Requisito requisito : requisitos) {
			materiaArray[index][0] = String.valueOf(requisito.getId_requisito());
			materiaArray[index][1] = requisito.getNombre();
			index++;
		}
		return ResponseEntity.ok(materiaArray);
	}

	@PostMapping("/CargarPeriodo/{id_carpeta}")
	public ResponseEntity<String[][]> CargarPeriodo(@PathVariable(value = "id_carpeta") Long id_carpeta) {
		List<Carpeta> carpetas = carpetaService.findOne(id_carpeta).getCarpetasHijos();
		String[][] materiaArray = new String[carpetas.size()][2];
		int index = 0;
		for (Carpeta carpeta : carpetas) {
			materiaArray[index][0] = String.valueOf(carpeta.getId_carpeta());
			materiaArray[index][1] = carpeta.getNom_carpeta();
			index++;
		}
		return ResponseEntity.ok(materiaArray);
	}

	@PostMapping("/CargarDocentePeriodo/{id_carpeta}")
	public ResponseEntity<String[][]> CargarDocentePeriodo(@PathVariable(value = "id_carpeta") Long id_carpeta) {
		List<Carpeta> carpetas = carpetaService.findOne(id_carpeta).getCarpetasHijos();
		String[][] materiaArray = new String[carpetas.size()][2];
		int index = 0;
		for (Carpeta carpeta : carpetas) {
			materiaArray[index][0] = String.valueOf(carpeta.getId_carpeta());
			materiaArray[index][1] = carpeta.getNom_carpeta();
			index++;
		}
		return ResponseEntity.ok(materiaArray);
	}

	@GetMapping("/ReporteDocente/{id_carpeta}")
	public String ReporteDocente(@PathVariable(value = "id_carpeta") Long id_carpeta, ModelMap model,
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
			List<Carpeta> carpetasDocente = carpetaService.findOne(id_carpeta).getCarpetasHijos();

			List<Materia> materias = materiaService.findAll();

			// List<Archivo> archivos = archivoService.findAll();
			for (Materia materia : materias) {
				Carpeta carpetaMateria = materia.getCarpeta().getCarpetaPadre();
				for (Carpeta carpeta2 : carpetasDocente) {
					if (carpetaMateria.equals(carpeta2)) {
						carpeta2.getMaterias().add(materia);
						System.out.println("");
						break;
					}
				}
			}
			List<Archivo> archivos = new ArrayList<>();
			for (Carpeta carpeta2 : carpetasDocente) {
				for (Carpeta carpeta3 : carpeta2.getCarpetasHijos()) {
					for (Archivo archivo2 : carpeta3.getArchivos()) {
						archivos.add(archivo);
						break;
					}
				}
			}

			for (Carpeta carpeta2 : carpetasDocente) {
				for (Materia materia : carpeta2.getMaterias()) {
					for (Requisito requisito : materia.getRequisitos()) {
						List<Parametro> parametros = parametroService.listaParametro2(carpeta2.getId_carpeta(),
								materia.getId_materia(),
								requisito.getId_requisito());
						for (Parametro parametro : parametros) {
							parametro.getArchivos().clear();
						}
						for (Parametro parametro : parametros) {
							parametro.setArchivos(
									archivoService.archivoParametro(parametro.getId_parametro(),
											carpeta2.getId_carpeta(),
											materia.getId_materia()));
						}
						for (Parametro parametro : parametros) {
							List<Archivo> archivosAEliminar = new ArrayList<>();

							for (Archivo archivo2 : parametro.getArchivos()) {
								if (archivo2.getEstado().equals("X")) {
									archivosAEliminar.add(archivo2);
								}
							}

							parametro.getArchivos().removeAll(archivosAEliminar);
						}
					}
				}
			}

			model.addAttribute("carpetasDocente", carpetasDocente);
			model.addAttribute("usuarios", usuarioService.findAll());
			model.addAttribute("list", list);
			model.addAttribute("requisitosList", requisitoService.findAll());
			model.addAttribute("materia", new Materia());
			model.addAttribute("materias", carpetaService.findOne(id_carpeta).getMaterias());
			model.addAttribute("ReporteSeleccionado", "true");
			return "home";
		} else {
			return "redirect:/login";
		}
	}

	/*
	 * @PostMapping("/ArchivoParametro/{id_parametro}/{id_carpeta}/{id_materia}")
	 * public ResponseEntity<String[][]> ArchivoParametro(@PathVariable(value =
	 * "id_parametro") Long id_parametro,
	 * 
	 * @PathVariable(value = "id_carpeta") Long id_carpeta,
	 * 
	 * @PathVariable(value = "id_materia") Long id_materia,
	 * ModelMap model,
	 * HttpServletRequest request) {
	 * 
	 * List<Archivo> archivos = archivoDao.archivoParametro(id_parametro,
	 * id_carpeta, id_materia);
	 * 
	 * String[][] materiaArray = new String[archivos.size()][3];
	 * 
	 * int index = 0;
	 * for (Archivo archivo : archivos) {
	 * materiaArray[index][0] = String.valueOf(archivo.getId_archivo());
	 * materiaArray[index][1] = archivo.getNom_archivo();
	 * materiaArray[index][2] = archivo.getFile();
	 * index++;
	 * }
	 * return ResponseEntity.ok(materiaArray);
	 * }
	 */

	/*--------- REPORTE MATERIA ---------------- */
	@PostMapping("/ReporteMateria")
	public String ReporteMateria(@RequestParam(value = "selectDocente") Long id_carpetaD,
			@RequestParam(value = "idCarpetaActual") Long id_carpeta,
			@RequestParam(value = "selectPeriodo") Long id_carpetaP,
			@RequestParam(value = "selectGestion") Long id_carpetaG,
			ModelMap model, HttpServletRequest request) {
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

			model.addAttribute("carpetas", carpetas);
			model.addAttribute("menus", carpetas);

			Archivo archivo = new Archivo();
			model.addAttribute("carpeta", new Carpeta());
			model.addAttribute("archivo", archivo);
			model.addAttribute("editMode", "true");
			model.addAttribute("anterior", carpeta);
			model.addAttribute("ExisteCarpeta", carpetas.isEmpty());
			model.addAttribute("ExisteArchivo", carpeta.getArchivos().isEmpty());

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
			List<Carpeta> carp = carpeta.getCarpetasHijos();
			for (Carpeta carpeta2 : carp) {
				if (carpeta2.getNom_carpeta().equals("FILE DOCENTES")) {
					model.addAttribute("GestionCarpeta", carpeta2.getCarpetasHijos());
				}

			}

			model.addAttribute("usuarios", usuarioService.findAll());
			model.addAttribute("list", list);
			model.addAttribute("requisitosList", requisitoService.findAll());
			model.addAttribute("materia", new Materia());
			model.addAttribute("materias", carpetaService.findOne(id_carpeta).getMaterias());
			model.addAttribute("opcionHome", "true");

			Carpeta carpetaDoc = carpetaService.findOne(id_carpetaD);
			model.addAttribute("carpetaDocente", carpetaDoc);

			Carpeta carpetaGes = carpetaService.findOne(id_carpetaG);
			model.addAttribute("carpetaGestion", carpetaGes);

			Carpeta carpetaPer = carpetaService.findOne(id_carpetaP);
			model.addAttribute("carpetaPeriodo", carpetaPer);

			// List<Materia> materiasConRequisitos = new ArrayList<>();
			List<Carpeta> carpetasMateria = carpetaDoc.getCarpetasHijos();
			Carpeta carpetaMateria = new Carpeta();
			for (Carpeta carpeta2 : carpetasMateria) {
				if (carpeta2.getNom_carpeta().equals("MATERIAS") && !carpeta2.getEstado().equals("X")) {
					for (Materia materia : carpeta2.getMaterias()) {

						materia.getRequisitos().clear();

						List<Requisito> nuevosRequisitos = requisitoService
								.listaRequisitosMateria2(materia.getId_materia(), carpeta2.getId_carpeta());

						materia.getRequisitos().addAll(nuevosRequisitos);

					}
				}
			}

			for (Carpeta carpeta2 : carpetasMateria) {
				if (carpeta2.getNom_carpeta().equals("MATERIAS") && !carpeta2.getEstado().equals("X")) {
					for (Materia materia : carpeta2.getMaterias()) {
						for (Requisito requisito : materia.getRequisitos()) {
							requisito.getParametros().clear();
							List<Parametro> nuevosParametros = parametroService.listaParametro2(
									carpeta2.getId_carpeta(), materia.getId_materia(), requisito.getId_requisito());

							requisito.getParametros().addAll(nuevosParametros);
						}
					}
				}
			}

			for (Carpeta carpeta2 : carpetasMateria) {
				if (carpeta2.getNom_carpeta().equals("MATERIAS") && !carpeta2.getEstado().equals("X")) {
					for (Materia materia : carpeta2.getMaterias()) {
						for (Requisito requisito : materia.getRequisitos()) {
							for (Parametro parametro : requisito.getParametros()) {
								System.out.println("REQUISITO: " + requisito.getNombre());
								System.out.println("PARAMETRO: " + parametro.getNombre() + "-"
										+ parametro.getId_parametro() + " CARPETA: " + carpeta2.getNom_carpeta() + "-"
										+ carpeta2.getId_carpeta() + " MATERIA: " + materia.getNombre() + "-"
										+ materia.getId_materia());
								parametro.getArchivos().clear();
								List<Archivo> archivos = archivoService.archivoParametro(
										parametro.getId_parametro(), carpeta2.getId_carpeta(), materia.getId_materia());

								parametro.getArchivos().addAll(archivos);

								// parametro.setArchivos(archivoService.archivoParametro(
								// parametro.getId_parametro(), carpeta2.getId_carpeta(),
								// materia.getId_materia()));
								System.out.println("TOTAL ARCHIVOS: " + parametro.getArchivos().size());
							}

						}
					}
				}
			}

			model.addAttribute("carpetaMateria", carpetaMateria);
			// model.addAttribute("carpetasMateria", carpetasMateria);

			return "home";
		} else {
			return "redirect:/login";
		}
	}

	/* --------------- GENERAR REPORTE PDF */
	@GetMapping("/GenerarReporteMateriaPDF/{id_carpetaD}/{id_carpeta}/{id_carpetaP}/{id_carpetaG}")
	public ResponseEntity<ByteArrayResource> verPdf(Model model, HttpServletRequest request,
			@PathVariable(value = "id_carpetaD") Long id_carpetaD,
			@PathVariable(value = "id_carpeta") Long id_carpeta,
			@PathVariable(value = "id_carpetaP") Long id_carpetaP,
			@PathVariable(value = "id_carpetaG") Long id_carpetaG)
			throws DocumentException, MalformedURLException, IOException, com.itextpdf.text.DocumentException {
		Carpeta carpetaDoc = carpetaService.findOne(id_carpetaD);
		// model.addAttribute("carpetaDocente", carpetaDoc);

		Carpeta carpetaGes = carpetaService.findOne(id_carpetaG);
		// model.addAttribute("carpetaGestion", carpetaGes);

		Carpeta carpetaPer = carpetaService.findOne(id_carpetaP);
		// model.addAttribute("carpetaPeriodo", carpetaPer);

		List<Carpeta> carpetasMateria = carpetaDoc.getCarpetasHijos();

		byte[] bytes = generarPdf(carpetaDoc, carpetaGes, carpetaPer, carpetasMateria);

		ByteArrayResource resource = new ByteArrayResource(bytes);

		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION,
						"inline;filename=" + "Reporte de Control de Archivos Docente.pdf")
				.contentType(MediaType.APPLICATION_PDF)
				.contentLength(bytes.length)
				.body(resource);
	}

	public byte[] generarPdf(Carpeta carpetaDoc, Carpeta carpetaGes, Carpeta carpetaPer, List<Carpeta> carpetaMateria)
			throws IOException, DocumentException, com.itextpdf.text.DocumentException {

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

		Document document = new Document(PageSize.LETTER, 30f, 20f, 50f, 40f);
		Paragraph emptyParagraph = new Paragraph();

		PdfWriter writer = PdfWriter.getInstance(document, outputStream);
		document.open();

		emptyParagraph.add(" ");
		document.add(emptyParagraph);

		/* tabla datos DOCENTE */
		PdfPTable tablaSegunda = new PdfPTable(2);
		tablaSegunda.setWidthPercentage(95);

		float[] columnWidths2 = { 3.5f, 6f };
		tablaSegunda.setWidths(columnWidths2);

		PdfPCell cell1 = new PdfPCell(new Phrase("NOMBRE:"));

		PdfPCell cell2 = new PdfPCell(
				new Phrase(carpetaDoc.getNom_carpeta()));

		PdfPCell cell3 = new PdfPCell(new Phrase("PERIODO:"));

		PdfPCell cell4 = new PdfPCell(
				new Phrase(carpetaPer.getNom_carpeta()));

		PdfPCell cell5 = new PdfPCell(new Phrase("GESTION:"));

		PdfPCell cell6 = new PdfPCell(
				new Phrase(carpetaGes.getNom_carpeta()));

		// Agregar las celdas a la tabla
		tablaSegunda.addCell(cell1);
		tablaSegunda.addCell(cell2);
		tablaSegunda.addCell(cell3);
		tablaSegunda.addCell(cell4);
		tablaSegunda.addCell(cell5);
		tablaSegunda.addCell(cell6);

		document.add(tablaSegunda);
		// canvas2.restoreState();
		emptyParagraph.add(" ");
		document.add(emptyParagraph);

		for (Carpeta carpeta2 : carpetaMateria) {
			if (carpeta2.getNom_carpeta().equals("MATERIAS") && !carpeta2.getEstado().equals("X")) {
				for (Materia materia : carpeta2.getMaterias()) {

					materia.getRequisitos().clear();

					List<Requisito> nuevosRequisitos = requisitoService
							.listaRequisitosMateria2(materia.getId_materia(), carpeta2.getId_carpeta());

					materia.getRequisitos().addAll(nuevosRequisitos);

				}
			}
		}

		for (Carpeta carpeta2 : carpetaMateria) {
			if (carpeta2.getNom_carpeta().equals("MATERIAS") && !carpeta2.getEstado().equals("X")) {
				for (Materia materia : carpeta2.getMaterias()) {
					for (Requisito requisito : materia.getRequisitos()) {
						requisito.getParametros().clear();
						List<Parametro> nuevosParametros = parametroService.listaParametro2(
								carpeta2.getId_carpeta(), materia.getId_materia(), requisito.getId_requisito());

						requisito.getParametros().addAll(nuevosParametros);
					}
				}
			}
		}

		for (Carpeta carpeta2 : carpetaMateria) {
			if (carpeta2.getNom_carpeta().equals("MATERIAS") && !carpeta2.getEstado().equals("X")) {
				for (Materia materia : carpeta2.getMaterias()) {
					PdfPTable tablaArchivos2 = new PdfPTable(1);
					tablaArchivos2.setWidthPercentage(95);

					float[] columnWidths3 = { 2 };
					tablaArchivos2.setWidths(columnWidths3);

					// Crear celda con el color específico para la primera fila
					PdfPCell greenCell2 = new PdfPCell(new Phrase(materia.getNombre()));
					greenCell2.setHorizontalAlignment(Element.ALIGN_CENTER);
					greenCell2.setBackgroundColor(new BaseColor(200, 200, 200)); // HEX #659308
					greenCell2.setVerticalAlignment(Element.ALIGN_MIDDLE); // Alineación vertical al centro
					greenCell2.setHorizontalAlignment(Element.ALIGN_CENTER);
					greenCell2.setPaddingBottom(12);
					greenCell2.setPaddingTop(10);
					tablaArchivos2.addCell(greenCell2);
					document.add(tablaArchivos2);

					PdfPTable tablaArchivos = new PdfPTable(2);
					tablaArchivos.setWidthPercentage(95);

					float[] columnWidths = { 3, 6 };
					tablaArchivos.setWidths(columnWidths);

					for (Requisito requisito : materia.getRequisitos()) {
						PdfPCell titleCell = new PdfPCell(new Phrase(requisito.getNombre()));
						titleCell.setRowspan(requisito.getParametros().size());
						titleCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
						titleCell.setHorizontalAlignment(Element.ALIGN_CENTER);
						tablaArchivos.addCell(titleCell);

						for (Parametro parametro : requisito.getParametros()) {
							System.out.println("REQUISITO: " + requisito.getNombre());
							System.out.println("PARAMETRO: " + parametro.getNombre() + "-"
									+ parametro.getId_parametro() + " CARPETA: " + carpeta2.getNom_carpeta() + "-"
									+ carpeta2.getId_carpeta() + " MATERIA: " + materia.getNombre() + "-"
									+ materia.getId_materia());
							parametro.getArchivos().clear();
							List<Archivo> archivos = archivoService.archivoParametro(
									parametro.getId_parametro(), carpeta2.getId_carpeta(), materia.getId_materia());

							parametro.getArchivos().addAll(archivos);

							System.out.println("TOTAL ARCHIVOS: " + parametro.getArchivos().size());

							String cumple = "N0 CUMPLE";

							if (parametro.getArchivos().size() > 0) {
								cumple = "SI";
							}
							PdfPCell titleCell2 = new PdfPCell(
									new Phrase(parametro.getNombre() + "-" + cumple));
							titleCell2.setVerticalAlignment(Element.ALIGN_MIDDLE);
							titleCell2.setHorizontalAlignment(Element.ALIGN_CENTER);
							if (cumple.equals("N0 CUMPLE")) {
								titleCell2.setBackgroundColor(new BaseColor(255, 0, 0, 128));
							}
							
							tablaArchivos.addCell(titleCell2);

						}
					}

					document.add(tablaArchivos);
					emptyParagraph.add(" ");
					document.add(emptyParagraph);

				}

			}
		}

		// Cerrar el documento
		document.close();

		return outputStream.toByteArray();
	}

	private void configureCell(PdfPCell cell) {
		cell.setBackgroundColor(BaseColor.DARK_GRAY);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		// Otras configuraciones de celda
	}

}
