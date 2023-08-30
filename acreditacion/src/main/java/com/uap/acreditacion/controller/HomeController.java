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
				//model.addAttribute("usuarios", usuarioService.findAll());
			}
			if (p.getTipoPersona().getNom_tipo_persona().equals("Docente")) {

				List<Carpeta> Listcarpetas = carpetaService.getAllCarpetasUsuario(p.getUsuario().getId_usuario());
				model.addAttribute("carpetas", Listcarpetas);
				model.addAttribute("menus", Listcarpetas);
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

				List<Carpeta> Listcarpetas = carpetaService.getAllCarpetasUsuario(p.getUsuario().getId_usuario());
				model.addAttribute("carpetas", Listcarpetas);
				model.addAttribute("menus", Listcarpetas);
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

			model.addAttribute("carpetas", carpetaService.findOne(id_carpeta).getCarpetasHijos());
			model.addAttribute("menus", carpetaService.findOne(id_carpeta).getCarpetasHijos());

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
			if (p.getTipoPersona().getNom_tipo_persona().equals("Personal")) {
				// Carpeta carpetaPrinc = p.getUsuario().getCarpetas().get(0);
				// Carpeta carpetaPrinc = p.getUsuario().getCarpetas()
				// List<Carpeta> Listcarpetas = (List<Carpeta>) p.getUsuario().getCarpetas();
				// Carpeta carpetaPrinc = p.getUsuario().getCarpetas().get(0);
				list.add(carpeta);

				/*
				 * while (carpeta.getCarpetaPadre() != carpetaPrinc) {
				 * list.add(carpeta.getCarpetaPadre());
				 * carpeta = carpeta.getCarpetaPadre();
				 * }
				 */
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

		if (id_carpeta_anterior != null) {
			carpeta.setCarpetaPadre(carpetaService.findOne(id_carpeta_anterior));
		}

		if (id_carpeta_anterior == null) {
			carpeta.setCarrera(persona.getCarrera());
		}

		if (persona.getTipoPersona().getNom_tipo_persona().equals("Personal")) {
			// Set<Usuario> usuarios = new HashSet<>();
			// usuarios.add(persona.getUsuario());
			// carpeta.setUsuarios2(usuarios);

		}
		Set<Usuario> usuarios = new HashSet<>();
		usuarios.add(persona.getUsuario());

		carpeta.setUsuarios(usuarios);
		if (id_carpeta_anterior != null) {
			if (!ruta_icon.isEmpty()) {
				String arch = config.guardarArchivo(ruta_icon);
				carpeta.setRuta_icono(arch);
				String[] ta = arch.split("\\.");
				for (String string : ta) {
					System.out.println(string);
				}
				// archivo.setTipoArchivo(tipoArchivoService.getTipoArchivo(ta[ta.length-1]));
				if (!ta[ta.length - 1].equals("png")) {
					redirectAttrs
							.addFlashAttribute("mensaje", "El archivo tiene que ser imagen png")
							.addFlashAttribute("clase", "danger");
					return "redirect:/home/" + id_carpeta_anterior;
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
				// archivo.setTipoArchivo(tipoArchivoService.getTipoArchivo(ta[ta.length-1]));
				if (!ta[ta.length - 1].equals("png")) {
					redirectAttrs
							.addFlashAttribute("mensaje", "El archivo tiene que ser imagen png")
							.addFlashAttribute("clase", "danger");
					return "redirect:/home/";
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
				if (!ta[ta.length - 1].equals("png")) {
					redirectAttrs
							.addFlashAttribute("mensaje", "El archivo tiene que ser imagen png")
							.addFlashAttribute("clase", "danger");
					return "redirect:/home/" + carpeta.getCarpetaPadre().getId_carpeta();
				} else {
					carpeta.setNom_carpeta(nom_carpeta);
					carpeta.setDescripcion(descripcion);
					carpetaService.save(carpeta);
					redirectAttrs
							.addFlashAttribute("mensaje", "Renombrado correctamente con cambio de icono")
							.addFlashAttribute("clase", "success");
					return "redirect:/home/" + carpeta.getCarpetaPadre().getId_carpeta();
				}
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
				if (!ta[ta.length - 1].equals("png")) {
					redirectAttrs
							.addFlashAttribute("mensaje", "El archivo tiene que ser imagen png")
							.addFlashAttribute("clase", "danger");
					return "redirect:/home/";
				} else {
					carpeta.setNom_carpeta(nom_carpeta);
					carpeta.setDescripcion(descripcion);
					carpetaService.save(carpeta);
					redirectAttrs
							.addFlashAttribute("mensaje", "Renombrado correctamente con cambio de icono")
							.addFlashAttribute("clase", "success");
					return "redirect:/home/";
				}
			}
			/*
			 * if (!ruta_ico.isEmpty()) {
			 * String arch = config.guardarArchivo(ruta_ico);
			 * carpeta.setRuta_icono(arch);
			 * String [] ta = arch.split("\\.");
			 * //archivo.setTipoArchivo(tipoArchivoService.getTipoArchivo(ta[ta.length-1]));
			 * if (!ta[ta.length-1].equals("png")) {
			 * redirectAttrs
			 * .addFlashAttribute("mensaje", "El archivo tiene que ser imagen png")
			 * .addFlashAttribute("clase", "danger");
			 * return "redirect:/home/";
			 * }else{
			 * carpeta.setNom_carpeta(nom_carpeta);
			 * carpeta.setDescripcion(descripcion);
			 * carpetaService.save(carpeta);
			 * redirectAttrs
			 * .addFlashAttribute("mensaje", "Renombrado correctamente")
			 * .addFlashAttribute("clase", "success");
			 * }
			 * }else {
			 * String arch = config.guardarArchivo(ruta_ico);
			 * 
			 * carpeta.setNom_carpeta(nom_carpeta);
			 * carpeta.setDescripcion(descripcion);
			 * carpetaService.save(carpeta);
			 * redirectAttrs
			 * .addFlashAttribute("mensaje", "Renombrado correctamente")
			 * .addFlashAttribute("clase", "success");
			 * 
			 * 
			 * return "redirect:/home/";
			 * }
			 */

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
	@PostMapping("/AsignarPermisoUsuario")
	public String AsignarPermisoUsuario(ModelMap model, RedirectAttributes redirectAttrs,
			@RequestParam(value = "permiso") String permiso,
			@RequestParam(value = "id_usuario") Long id_usuario) {
		Usuario usuario = usuarioService.findOne(id_usuario);

		usuario.setPermisosCarpeta(permiso);

		usuarioService.save(usuario);
		return "redirect:/home/";
	}

	// ----------- FIN DEL METODO

	@PostMapping("/guardar-archivo")
	public String guardarArchivo(@Validated Archivo archivo, RedirectAttributes redirectAttrs,
			@RequestParam(name = "archivo", required = false) MultipartFile file,
			@RequestParam(value = "auxiliar") Long id_carpeta_anterior) throws IOException {
		if (archivo.getNom_archivo() == null || archivo.getNom_archivo() == "" || archivo.getDescripcion() == null
				|| archivo.getDescripcion() == "") {
			redirectAttrs
					.addFlashAttribute("mensaje", "Se requiere llenar los campos")
					.addFlashAttribute("clase", "danger");
			return "redirect:/home/" + id_carpeta_anterior;
		}
		if (id_carpeta_anterior != null) {
			archivo.setCarpeta(carpetaService.findOne(id_carpeta_anterior));
		}
		archivo.setEstado("A");
		archivo.setFecha_registro(new Date());
		System.out.println("anterior " + id_carpeta_anterior);
		if (!file.isEmpty()) {
			String arch = config.guardarArchivo(file);
			archivo.setFile(arch);
			// archivo.setContenido(file.getBytes());
			String[] ta = arch.split("\\.");
			// archivo.setTipoArchivo(tipoArchivoService.getTipoArchivo(ta[ta.length - 1]));
			if (!ta[ta.length - 1].equals("pdf")) {
				redirectAttrs
						.addFlashAttribute("mensaje", "El archivo tiene que ser en formato pdf")
						.addFlashAttribute("clase", "danger");
				return "redirect:/home/" + id_carpeta_anterior;
			}
		} else {
			redirectAttrs
					.addFlashAttribute("mensaje", "Es necesario cargar un archivo")
					.addFlashAttribute("clase", "danger");
			return "redirect:/home/" + id_carpeta_anterior;
		}

		archivoService.save(archivo);

		redirectAttrs
				.addFlashAttribute("mensaje", "Agregado correctamente")
				.addFlashAttribute("clase", "success");
		return "redirect:/home/" + id_carpeta_anterior;
	}

	// * --------- VER PDF -------- */
	/*
	 * @GetMapping("/verPdf/{id}")
	 * public ResponseEntity<ByteArrayResource> verPdf(@PathVariable Long id) {
	 * Optional<Archivo> archivoOptional = archivoService.findOneOptional(id);
	 * 
	 * if (archivoOptional.isPresent()) {
	 * Archivo archivo = archivoOptional.get();
	 * 
	 * ByteArrayResource resource = new ByteArrayResource(archivo.getContenido());
	 * 
	 * return ResponseEntity.ok()
	 * .header(HttpHeaders.CONTENT_DISPOSITION, "inline;filename=" +
	 * archivo.getNom_archivo()+".pdf")
	 * .contentType(MediaType.APPLICATION_PDF)
	 * .contentLength(archivo.getContenido().length)
	 * .body(resource);
	 * } else {
	 * return ResponseEntity.notFound().build();
	 * }
	 * }
	 * 
	 * @GetMapping(value = "/ContenidoArchivo/{id_archivo}")
	 * public String ContenidoArchivo(HttpServletRequest request, Model model,
	 * 
	 * @PathVariable("id_archivo") Long id_archivo) {
	 * System.out.println("EDITAR ARCHIVOS");
	 * model.addAttribute("archivo", archivoService.findOne(id_archivo));
	 * model.addAttribute("edit", "true");
	 * return "/Archivo/archivo";
	 * }
	 */
	@GetMapping("/verIcoPdf/{id}")
	public ResponseEntity<byte[]> verIcoPdf(@PathVariable Long id) {
		Path projectPath = Paths.get("").toAbsolutePath();

		// Optional<Archivo> archivoOptional = archivoService.findOneOptional(id);
		Archivo archivo = archivoService.findOne(id);
		String rutaArchivo = projectPath + "\\acreditacion\\\\uploads\\" + archivo.getFile();
		// System.out.println("Ruta absoluta de uploads es: " + rutaArchivo);

		// File file = new File(rutaArchivo);

		// Archivo archivo = archivoOptional.get();

		try {

			byte[] fileBytes;
			try (InputStream inputStream = new FileInputStream(rutaArchivo)) {
				fileBytes = inputStream.readAllBytes();
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
	/* aaaaaaaaaaaaaaaa */

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
}
