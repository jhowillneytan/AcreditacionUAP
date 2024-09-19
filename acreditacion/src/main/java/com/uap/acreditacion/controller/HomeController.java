package com.uap.acreditacion.controller;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
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
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.uap.acreditacion.Config;
import com.uap.acreditacion.dao.IArchivoDao;
import com.uap.acreditacion.dao.IParametroDao;
import com.uap.acreditacion.dao.IPersonaDao;
import com.uap.acreditacion.dao.IRequisitoDao;
import com.uap.acreditacion.entity.Archivo;
import com.uap.acreditacion.entity.Cargo;
import com.uap.acreditacion.entity.Carpeta;
import com.uap.acreditacion.entity.Carrera;
import com.uap.acreditacion.entity.Docente;
import com.uap.acreditacion.entity.Materia;
import com.uap.acreditacion.entity.Parametro;
import com.uap.acreditacion.entity.Persona;
import com.uap.acreditacion.entity.Puesto;
import com.uap.acreditacion.entity.Requisito;
import com.uap.acreditacion.entity.TipoEvaluacionMateria;
import com.uap.acreditacion.entity.TipoPersona;
import com.uap.acreditacion.entity.Usuario;
import com.uap.acreditacion.service.IArchivoService;
import com.uap.acreditacion.service.ICargoService;
import com.uap.acreditacion.service.ICarpetaService;
import com.uap.acreditacion.service.ICarreraService;
import com.uap.acreditacion.service.IDocenteService;
import com.uap.acreditacion.service.IMateriaService;
import com.uap.acreditacion.service.IParametroService;
import com.uap.acreditacion.service.IPersonaService;
import com.uap.acreditacion.service.IPuestoService;
import com.uap.acreditacion.service.IRequisitoService;
import com.uap.acreditacion.service.ITipoPersonaService;
import com.uap.acreditacion.service.IUsuarioService;
import com.uap.acreditacion.service.TipoEvaluacionMateriaService;

import jakarta.servlet.http.HttpServletRequest;

import java.awt.image.BufferedImage;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
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

	@Autowired
	private ICargoService cargoService;

	@Autowired
	private IPuestoService puestoService;

	@Autowired
	private IDocenteService docenteService;

	@Autowired
	private TipoEvaluacionMateriaService evaluacionMateriaService;

	@GetMapping(value = "/home")
	public String home(ModelMap model, HttpServletRequest request,
			@RequestParam(value = "id_carpeta", required = false) Long id_carpeta) {
		if (request.getSession().getAttribute("persona") != null) {
			Persona p2 = (Persona) request.getSession().getAttribute("persona");
			Persona p = personaService.findOne(p2.getId_persona());
			Usuario user = usuarioService.usuarioPorIdPersona(p.getId_persona());
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

				model.addAttribute("carpetas", p.getCarrera().getCarpetas());
				model.addAttribute("menus", Listcarpetas);
				System.out.println("***********METODOD HOME");

				model.addAttribute("archivo", new Archivo());
				model.addAttribute("carpeta", new Carpeta());
				model.addAttribute("anterior", new Carpeta());
				model.addAttribute("ExisteArchivo", "true");
				model.addAttribute("usuarios", usuarioService.findAll());
			}
			if (p.getTipoPersona().getNom_tipo_persona().equals("Evaluador")) {
				// List<Carpeta> Listcarpetas = new ArrayList<>();
				// List<Carpeta> listFill = carpetaService.findAll();
				// for (int i = 0; i < listFill.size(); i++) {
				// if (listFill.get(i).getCarpetaPadre() == null) {
				// Listcarpetas.add(listFill.get(i));
				// }
				// }
				List<Carpeta> Listcarpetas = carpetaService.getAllCarpetasUsuario(user.getId_usuario());
				List<Carpeta> listcarpetasNullPadre = carpetaService
						.getAllCarpetasNullPadreUsuario(user.getId_usuario());
				for (Carpeta cPadre : listcarpetasNullPadre) {
					Listcarpetas.removeAll(
							carpetaService.getCarpetasUsuarioYHijos(user.getId_usuario(), cPadre.getId_carpeta()));
				}
				for (Carpeta cPadre : listcarpetasNullPadre) {
					Listcarpetas.add(cPadre);
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

				// model.addAttribute("carpetas", carpetasConUsuario);
				model.addAttribute("carpetas", p.getDocente().getCarpetas());
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

				List<Carpeta> listFill = p.getCarrera().getCarpetas();
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
			model.addAttribute("ListaCarreras", carreraService.findAll());
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
			Usuario user = usuarioService.usuarioPorIdPersona(p.getId_persona());
			Calendar cal = Calendar.getInstance();
			int year = cal.get(Calendar.YEAR);
			Carpeta carpeta = carpetaService.findOne(id_carpeta);
			model.addAttribute("personasession", p);
			model.addAttribute("tipoPersonasession",
					tipoPersonaService.findOne(p.getTipoPersona().getId_tipo_persona()));
			List<Carpeta> carpetas = carpeta.getCarpetasHijos();

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
				carpetas = carpetaService.getCarpetasUsuarioYHijos(user.getId_usuario(), id_carpeta);
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
				if (carpeta2.getNom_carpeta().equals("EVALUACION DOCENTE")) {
					model.addAttribute("GestionCarpeta", carpeta2.getCarpetasHijos());
				}

			}
			// LISTAR MATERIAS DE DOCENTES
			List<Materia> materias = new ArrayList<Materia>();
			Carpeta carpetaMateria = carpetaService.findOne(id_carpeta);
			System.out.println("MATERIAS DE DOCENTES: " + carpetaMateria.getNom_carpeta());
			if (carpetaMateria.getNom_carpeta().equals("MATERIAS")) {
				System.out.println("VERDADERO 1");
				// if (carpeta.getCarpetaPadre().getCarpetaPadre() != null) {
				System.out.println("VERDADERO 2");
				// if (carpeta.getCarpetaPadre().getCarpetaPadre().getCarpetaPadre() != null) {

				Carpeta carpetaGestion = carpetaMateria.getCarpetaPadre().getCarpetaPadre().getCarpetaPadre();
				Carpeta carpetaPeriodo = carpetaMateria.getCarpetaPadre().getCarpetaPadre();
				Carpeta carpetaDocente = carpetaMateria.getCarpetaPadre();

				// Carrera carrera =
				// carreraService.findOne(obtenerIdCarpetaPadreRecursivo(carpeta));
				Carpeta carpeta2 = carpetaService.findOne(obtenerIdCarpetaPadreRecursivo(carpeta));
				String numero = carpetaGestion.getNom_carpeta().replaceAll("[^0-9]", "");
				int numeroGestion = Integer.parseInt(numero);
				System.out.println("gestion: " + numeroGestion);

				String numero2 = carpetaPeriodo.getNom_carpeta().replaceAll("[^0-9]", "");
				int numeroPeriodo = Integer.parseInt(numero2);
				System.out.println("periodo: " + numeroPeriodo);

				Map<String, Object> requests = new HashMap<String, Object>();
				requests.put("rd", carpetaDocente.getDocente().getRd());
				requests.put("periodo", numero2);
				requests.put("gestion", numero);
				requests.put("code_carrera", carpeta2.getCarrera().getId_carrera());
				System.out.println("RD: " + carpetaDocente.getDocente().getRd());
				System.out.println("periodo: " + numero2);
				System.out.println("gestion: " + numero);
				System.out.println("code_carrera: " + carpeta2.getCarrera().getId_carrera());
				String url = "http://181.115.188.250:9993/v1/service/api/f4adc106a6bf4902aa0e0e053e753962";
				String key = "key 46bc2f9cface91d161e6bf4f6e27c1aeb67d40d157b082d7a7135a677f5df1fb";

				HttpHeaders headers = new HttpHeaders();

				headers.setContentType(MediaType.APPLICATION_JSON);
				headers.set("x-api-key", key);

				HttpEntity<HashMap> req = new HttpEntity(requests, headers);

				RestTemplate restTemplate = new RestTemplate();

				ResponseEntity<Map> resp = restTemplate.exchange(url, HttpMethod.POST, req, Map.class);

				if (resp.getStatusCode() == HttpStatus.OK) {
					Map<String, Object> responseBody = resp.getBody();
					// Aquí puedes procesar los datos de responseBody
					Map<String, Object> data = (Map<String, Object>) responseBody.get("data");

					// Obtener los valores de "paterno", "ci", "fecha_nacimiento", etc., del objeto
					// "data"

					if (data != null) {

						String nombCarrera = (String) data.get("carrera");
						String paterno = (String) data.get("paterno");
						String ci = (String) data.get("ci");
						String nombre = (String) data.get("nombre");
						String materno = (String) data.get("materno");
						int rd = (int) data.get("rd");
						List<String> correos = (List<String>) data.get("correos");
						/*
						 * List<String> correos = new ArrayList<>();
						 * List<Map<String, String>> correosData = (List<Map<String, String>>)
						 * data.get("correos");
						 * for (Map<String, String> asignaturaData : correosData) {
						 * correos.add(asignaturaData.get("asignatura"));
						 * }
						 */
						System.out.println(nombCarrera);
						System.out.println(paterno);
						System.out.println(ci);
						System.out.println(nombre);
						System.out.println(materno);
						System.out.println(rd);
						String celular = (String) data.get("celular");
						List<String[]> asignaturas = new ArrayList<>();
						List<Map<String, String>> asignaturasData = (List<Map<String, String>>) data
								.get("asignaturas");
						for (Map<String, String> asignaturaData : asignaturasData) {
							String[] asig = { asignaturaData.get("asignatura"), asignaturaData.get("plan"),
									asignaturaData.get("tipo_evaluacion"), asignaturaData.get("sigla"),
									asignaturaData.get("grupo") };
							asignaturas.add(asig);
						}

						for (String[] asignatura : asignaturas) {
							materias.add(materiaService.materiaSigla(asignatura[3]));
						}

					}
					// System.out.println(responseBody);
				} else {
					System.out.println(
							"Error en la solicitud. Código de respuesta: " + resp.getStatusCodeValue());

				}
				// }
				// }
			} else {
				System.out.println(
						"No");
			}
			Set<Materia> materiasUnicasSet = new HashSet<>(materias);
			List<Materia> materiasUnicasList = new ArrayList<>(materiasUnicasSet);

			model.addAttribute("usuarios", usuarioService.findAll());
			model.addAttribute("carpetas", carpetas);
			model.addAttribute("menus", carpetas);
			model.addAttribute("list", list);
			model.addAttribute("requisitosList", requisitoService.findAll());
			model.addAttribute("materia", new Materia());
			// model.addAttribute("materias",
			// carpetaService.findOne(id_carpeta).getMaterias());
			model.addAttribute("materias", materiasUnicasList);
			model.addAttribute("opcionHome", "true");
			model.addAttribute("ListaCarreras", carreraService.findAll());
			return "home";
		} else {
			return "redirect:/login";
		}
	}

	@PostMapping("/guardar-carpeta")
	public String guardar(RedirectAttributes redirectAttrs, @Validated Carpeta carpeta,
			@RequestParam(name = "ruta_icon", required = false) MultipartFile ruta_icon,
			@RequestParam(value = "auxiliar", required = false) Long id_carpeta_anterior,
			@RequestParam(value = "gestion", required = false) String gestionCarp,
			@RequestParam(value = "periodo", required = false) String periodoCarp, HttpServletRequest request) {

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

		if (gestionCarp != null) {
			carpeta.setNom_carpeta("GESTION " + gestionCarp);
			carpeta.setDescripcion("GESTION " + gestionCarp);
		}
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
				carpeta.setRuta_icono("iconoPredeterminadoCarpeta.webp");
			}

			carpeta.setFecha_registro(new Date());
			// carpeta.setUsuario(persona.getUsuario());
			// System.out.println(carpeta.getUsuarios().get(0).getUsername());
			carpeta.setEstado("A");

			// for (Usuario usuario : carpeta.getUsuarios()) {
			// System.out.println("AAAAAAAAAA" + carpeta.getCarpetaPadre().getNom_carpeta()+
			// usuario.getUsername());
			// }
			// carpeta.setCarrera(persona.getCarrera());
			carpetaService.save(carpeta);
			if (gestionCarp != null) {
				// List<Carpeta> lisCarpetas = new ArrayList<>();
				for (int index = 0; index < 2; index++) {
					Carpeta carpeta2 = new Carpeta();
					carpeta2.setNom_carpeta("PERIODO " + (index + 1));
					carpeta2.setDescripcion("PERIODO " + (index + 1));
					Set<Usuario> usuarios = new HashSet<>();
					usuarios.add(persona.getUsuario());
					carpeta2.setUsuarios(usuarios);
					carpeta2.setRuta_icono("iconoPredeterminadoCarpeta.webp");
					carpeta2.setFecha_registro(new Date());
					carpeta2.setEstado("A");
					carpeta2.setCarpetaPadre(carpeta);
					carpetaService.save(carpeta2);
				}
			}
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
				carpeta.setRuta_icono("iconoPredeterminadoCarpeta.webp");
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

	@GetMapping("/vista")
	public String vista() {
		return "vista";
	}

	@PostMapping("/usuariosCarpeta/{id_carpeta}")
	public ResponseEntity<String[][]> obtenerUsuariosDeCarpeta(@PathVariable Long id_carpeta,
			HttpServletRequest request) {

		Persona p = (Persona) request.getSession().getAttribute("persona");
		Persona persona = personaService.findOne(p.getId_persona());

		List<Usuario> listaUsuariosCarpeta = usuarioService.listaUsuarioPorIdCarpeta(id_carpeta);
		List<Usuario> listaUsuario;
		if (persona.getTipoPersona().getNom_tipo_persona().equals("Administrador")) {
			listaUsuario = usuarioService.listaUsuarios();
		} else {
			listaUsuario = usuarioService.listaUsuarioPorIdCarrera(persona.getCarrera().getId_carrera());
		}

		List<Usuario> listaUsuariosDisponibles = new ArrayList<>(listaUsuario); // Copia de todos los usuarios

		// Eliminar de listaUsuariosDisponibles los usuarios que están en
		// listaUsuariosCarpeta
		listaUsuariosDisponibles.removeAll(listaUsuariosCarpeta);

		String[][] usuariosArray = new String[listaUsuariosDisponibles.size()][3];

		int index = 0;
		for (Usuario usuario : listaUsuariosDisponibles) {
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
			// LISTAR MATERIAS DE DOCENTES
			List<Materia> materias = new ArrayList<Materia>();
			Carpeta carpetaMateria = carpetaService.findOne(id_carpeta);
			System.out.println("MATERIAS DE DOCENTES: " + carpetaMateria.getNom_carpeta());
			if (carpetaMateria.getNom_carpeta().equals("MATERIAS")) {
				System.out.println("VERDADERO 1");
				// if (carpeta.getCarpetaPadre().getCarpetaPadre() != null) {
				System.out.println("VERDADERO 2");
				// if (carpeta.getCarpetaPadre().getCarpetaPadre().getCarpetaPadre() != null) {

				Carpeta carpetaGestion = carpetaMateria.getCarpetaPadre().getCarpetaPadre().getCarpetaPadre();
				Carpeta carpetaPeriodo = carpetaMateria.getCarpetaPadre().getCarpetaPadre();
				Carpeta carpetaDocente = carpetaMateria.getCarpetaPadre();

				// Carrera carrera =
				// carreraService.findOne(obtenerIdCarpetaPadreRecursivo(carpeta));
				Carpeta carpeta2 = carpetaService.findOne(obtenerIdCarpetaPadreRecursivo(carpeta));
				String numero = carpetaGestion.getNom_carpeta().replaceAll("[^0-9]", "");
				int numeroGestion = Integer.parseInt(numero);
				System.out.println("gestion: " + numeroGestion);

				String numero2 = carpetaPeriodo.getNom_carpeta().replaceAll("[^0-9]", "");
				int numeroPeriodo = Integer.parseInt(numero2);
				System.out.println("periodo: " + numeroPeriodo);

				Map<String, Object> requests = new HashMap<String, Object>();
				requests.put("rd", carpetaDocente.getDocente().getRd());
				requests.put("periodo", numero2);
				requests.put("gestion", numero);
				requests.put("code_carrera", carpeta2.getCarrera().getId_carrera());
				System.out.println("RD: " + carpetaDocente.getDocente().getRd());
				System.out.println("periodo: " + numero2);
				System.out.println("gestion: " + numero);
				System.out.println("code_carrera: " + carpeta2.getCarrera().getId_carrera());
				String url = "http://181.115.188.250:9993/v1/service/api/f4adc106a6bf4902aa0e0e053e753962";
				String key = "key 46bc2f9cface91d161e6bf4f6e27c1aeb67d40d157b082d7a7135a677f5df1fb";

				HttpHeaders headers = new HttpHeaders();

				headers.setContentType(MediaType.APPLICATION_JSON);
				headers.set("x-api-key", key);

				HttpEntity<HashMap> req = new HttpEntity(requests, headers);

				RestTemplate restTemplate = new RestTemplate();

				ResponseEntity<Map> resp = restTemplate.exchange(url, HttpMethod.POST, req, Map.class);

				if (resp.getStatusCode() == HttpStatus.OK) {
					Map<String, Object> responseBody = resp.getBody();
					// Aquí puedes procesar los datos de responseBody
					Map<String, Object> data = (Map<String, Object>) responseBody.get("data");

					// Obtener los valores de "paterno", "ci", "fecha_nacimiento", etc., del objeto
					// "data"

					if (data != null) {

						String nombCarrera = (String) data.get("carrera");
						String paterno = (String) data.get("paterno");
						String ci = (String) data.get("ci");
						String nombre = (String) data.get("nombre");
						String materno = (String) data.get("materno");
						int rd = (int) data.get("rd");
						List<String> correos = (List<String>) data.get("correos");
						/*
						 * List<String> correos = new ArrayList<>();
						 * List<Map<String, String>> correosData = (List<Map<String, String>>)
						 * data.get("correos");
						 * for (Map<String, String> asignaturaData : correosData) {
						 * correos.add(asignaturaData.get("asignatura"));
						 * }
						 */
						System.out.println(nombCarrera);
						System.out.println(paterno);
						System.out.println(ci);
						System.out.println(nombre);
						System.out.println(materno);
						System.out.println(rd);
						String celular = (String) data.get("celular");
						List<String[]> asignaturas = new ArrayList<>();
						List<Map<String, String>> asignaturasData = (List<Map<String, String>>) data
								.get("asignaturas");
						for (Map<String, String> asignaturaData : asignaturasData) {
							String[] asig = { asignaturaData.get("asignatura"), asignaturaData.get("plan"),
									asignaturaData.get("tipo_evaluacion"), asignaturaData.get("sigla"),
									asignaturaData.get("grupo") };
							asignaturas.add(asig);
						}

						for (String[] asignatura : asignaturas) {
							materias.add(materiaService.materiaSigla(asignatura[3]));
						}

					}
					// System.out.println(responseBody);
				} else {
					System.out.println(
							"Error en la solicitud. Código de respuesta: " + resp.getStatusCodeValue());

				}
				// }
				// }
			} else {
				System.out.println(
						"No");
			}
			Set<Materia> materiasUnicasSet = new HashSet<>(materias);
			List<Materia> materiasUnicasList = new ArrayList<>(materiasUnicasSet);
			model.addAttribute("usuarios", usuarioService.findAll());
			model.addAttribute("list", list);
			// model.addAttribute("requisitos",
			// materiaService.findOne(id_materia).getRequisitos());
			// model.addAttribute("requisitosM",
			// materiaService.findOne(id_materia).getRequisitos());
			model.addAttribute("requisitos", requisitoService.listaRequisitosMateria2(id_materia, id_carpeta));
			model.addAttribute("materia", new Materia());
			// model.addAttribute("materias",
			// carpetaService.findOne(id_carpeta).getMaterias());
			model.addAttribute("materias", materiasUnicasList);
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

			// LISTAR MATERIAS DE DOCENTES
			List<Materia> materias = new ArrayList<Materia>();
			Carpeta carpetaMateria = carpetaService.findOne(id_carpeta);
			System.out.println("MATERIAS DE DOCENTES: " + carpetaMateria.getNom_carpeta());
			if (carpetaMateria.getNom_carpeta().equals("MATERIAS")) {
				System.out.println("VERDADERO 1");
				// if (carpeta.getCarpetaPadre().getCarpetaPadre() != null) {
				System.out.println("VERDADERO 2");
				// if (carpeta.getCarpetaPadre().getCarpetaPadre().getCarpetaPadre() != null) {

				Carpeta carpetaGestion = carpetaMateria.getCarpetaPadre().getCarpetaPadre().getCarpetaPadre();
				Carpeta carpetaPeriodo = carpetaMateria.getCarpetaPadre().getCarpetaPadre();
				Carpeta carpetaDocente = carpetaMateria.getCarpetaPadre();

				// Carrera carrera =
				// carreraService.findOne(obtenerIdCarpetaPadreRecursivo(carpeta));
				Carpeta carpeta2 = carpetaService.findOne(obtenerIdCarpetaPadreRecursivo(carpeta));
				String numero = carpetaGestion.getNom_carpeta().replaceAll("[^0-9]", "");
				int numeroGestion = Integer.parseInt(numero);
				System.out.println("gestion: " + numeroGestion);

				String numero2 = carpetaPeriodo.getNom_carpeta().replaceAll("[^0-9]", "");
				int numeroPeriodo = Integer.parseInt(numero2);
				System.out.println("periodo: " + numeroPeriodo);

				Map<String, Object> requests = new HashMap<String, Object>();
				requests.put("rd", carpetaDocente.getDocente().getRd());
				requests.put("periodo", numero2);
				requests.put("gestion", numero);
				requests.put("code_carrera", carpeta2.getCarrera().getId_carrera());
				System.out.println("RD: " + carpetaDocente.getDocente().getRd());
				System.out.println("periodo: " + numero2);
				System.out.println("gestion: " + numero);
				System.out.println("code_carrera: " + carpeta2.getCarrera().getId_carrera());
				String url = "http://181.115.188.250:9993/v1/service/api/f4adc106a6bf4902aa0e0e053e753962";
				String key = "key 46bc2f9cface91d161e6bf4f6e27c1aeb67d40d157b082d7a7135a677f5df1fb";

				HttpHeaders headers = new HttpHeaders();

				headers.setContentType(MediaType.APPLICATION_JSON);
				headers.set("x-api-key", key);

				HttpEntity<HashMap> req = new HttpEntity(requests, headers);

				RestTemplate restTemplate = new RestTemplate();

				ResponseEntity<Map> resp = restTemplate.exchange(url, HttpMethod.POST, req, Map.class);

				if (resp.getStatusCode() == HttpStatus.OK) {
					Map<String, Object> responseBody = resp.getBody();
					// Aquí puedes procesar los datos de responseBody
					Map<String, Object> data = (Map<String, Object>) responseBody.get("data");

					// Obtener los valores de "paterno", "ci", "fecha_nacimiento", etc., del objeto
					// "data"

					if (data != null) {

						String nombCarrera = (String) data.get("carrera");
						String paterno = (String) data.get("paterno");
						String ci = (String) data.get("ci");
						String nombre = (String) data.get("nombre");
						String materno = (String) data.get("materno");
						int rd = (int) data.get("rd");
						List<String> correos = (List<String>) data.get("correos");
						/*
						 * List<String> correos = new ArrayList<>();
						 * List<Map<String, String>> correosData = (List<Map<String, String>>)
						 * data.get("correos");
						 * for (Map<String, String> asignaturaData : correosData) {
						 * correos.add(asignaturaData.get("asignatura"));
						 * }
						 */
						System.out.println(nombCarrera);
						System.out.println(paterno);
						System.out.println(ci);
						System.out.println(nombre);
						System.out.println(materno);
						System.out.println(rd);
						String celular = (String) data.get("celular");
						List<String[]> asignaturas = new ArrayList<>();
						List<Map<String, String>> asignaturasData = (List<Map<String, String>>) data
								.get("asignaturas");
						for (Map<String, String> asignaturaData : asignaturasData) {
							String[] asig = { asignaturaData.get("asignatura"), asignaturaData.get("plan"),
									asignaturaData.get("tipo_evaluacion"), asignaturaData.get("sigla"),
									asignaturaData.get("grupo") };
							asignaturas.add(asig);
						}

						for (String[] asignatura : asignaturas) {
							materias.add(materiaService.materiaSigla(asignatura[3]));
						}

					}
					// System.out.println(responseBody);
				} else {
					System.out.println(
							"Error en la solicitud. Código de respuesta: " + resp.getStatusCodeValue());

				}
				// }
				// }
			} else {
				System.out.println(
						"No");
			}

			Set<Materia> materiasUnicasSet = new HashSet<>(materias);
			List<Materia> materiasUnicasList = new ArrayList<>(materiasUnicasSet);

			model.addAttribute("usuarios", usuarioService.findAll());
			model.addAttribute("list", list);
			model.addAttribute("requisitos", requisitoService.listaRequisitosMateria2(id_materia, id_carpeta));
			// model.addAttribute("requisitosM",
			// iRequisitoDao.listaRequisitosMateria(id_materia));
			model.addAttribute("materia", new Materia());

			// model.addAttribute("materias",
			// carpetaService.findOne(id_carpeta).getMaterias());
			model.addAttribute("materias", materiasUnicasList);
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
		// List<Carpeta> carpetas = carpetaService.findOne(id_carpeta).getCarpetasHijos();
		List<Carpeta> carpetas = carpetaService.listaCarpetasHijosPorIdCarpeta(id_carpeta);
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
				if (carpeta2.getNom_carpeta().equals("EVALUACION DOCENTE")) {
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
	@GetMapping("/GenerarReporteMateriaDocentePDF/{id_carpetaD}/{id_carpeta}/{id_carpetaP}/{id_carpetaG}")
	public ResponseEntity<ByteArrayResource> GenerarReporteMateriaDocentePDF(Model model, HttpServletRequest request,
			@PathVariable(value = "id_carpetaD", required = false) Long id_carpetaD,
			@PathVariable(value = "id_carpeta", required = false) Long id_carpeta,
			@PathVariable(value = "id_carpetaP", required = false) Long id_carpetaP,
			@PathVariable(value = "id_carpetaG", required = false) Long id_carpetaG)
			throws DocumentException, MalformedURLException, IOException, com.itextpdf.text.DocumentException {
		Persona p2 = (Persona) request.getSession().getAttribute("persona");
		Persona p = personaService.findOne(p2.getId_persona());

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

		Document document = new Document(PageSize.LETTER, 30f, 20f, 20f, 70f);

		Paragraph emptyParagraph = new Paragraph();

		PdfWriter writer = PdfWriter.getInstance(document, outputStream);

		document.open();

		Path projectPath = Paths.get("").toAbsolutePath();
		String fuenteCalibriRegular = projectPath
				+ "/acreditacion/src/main/resources/static/fuenteLetra/Calibri Regular.ttf";
		String fuenteCalibriBold = projectPath + "/acreditacion/src/main/resources/static/fuenteLetra/Calibri Bold.ttf";
		Font fontSimple = FontFactory.getFont(fuenteCalibriRegular, BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 11);
		Font fontNegrilla = FontFactory.getFont(fuenteCalibriBold, BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 11);
		Font fontSimple9 = FontFactory.getFont(fuenteCalibriRegular, BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 9);
		Font fontNegrilla9 = FontFactory.getFont(fuenteCalibriBold, BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 9);

		emptyParagraph.add(" ");
		document.add(emptyParagraph);

		/* tabla datos DOCENTE */
		PdfPTable tablaSegunda = new PdfPTable(2);
		float tableWidth = document.getPageSize().getWidth() - document.leftMargin() - document.rightMargin();
		tablaSegunda.setTotalWidth(tableWidth);
		// tablaSegunda.setWidthPercentage(95);

		float[] columnWidths2 = { 3.5f, 6f };
		tablaSegunda.setWidths(columnWidths2);

		PdfPCell cell1 = new PdfPCell(new Phrase("NOMBRE:", fontNegrilla));

		PdfPCell cell2 = new PdfPCell(
				new Phrase(carpetaDoc.getNom_carpeta(), fontSimple));

		PdfPCell cell3 = new PdfPCell(new Phrase("PERIODO:", fontNegrilla));

		PdfPCell cell4 = new PdfPCell(
				new Phrase(carpetaPer.getNom_carpeta(), fontSimple));

		PdfPCell cell5 = new PdfPCell(new Phrase("GESTION:", fontNegrilla));

		PdfPCell cell6 = new PdfPCell(
				new Phrase(carpetaGes.getNom_carpeta(), fontSimple));

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

				// CARGAR MATERIAS CON API
				// LISTAR MATERIAS DE DOCENTES
				List<Materia> materias = new ArrayList<>();
				Carpeta carpetaMateria2 = carpeta2;
				System.out.println("MATERIAS DE DOCENTES: " + carpetaMateria2.getNom_carpeta());
				if (carpetaMateria2.getNom_carpeta().equals("MATERIAS")) {
					System.out.println("VERDADERO 1");
					// if (carpeta.getCarpetaPadre().getCarpetaPadre() != null) {
					System.out.println("VERDADERO 2");
					// if (carpeta.getCarpetaPadre().getCarpetaPadre().getCarpetaPadre() != null) {
					Carpeta carpetaGestion = carpetaMateria2.getCarpetaPadre().getCarpetaPadre()
							.getCarpetaPadre();
					Carpeta carpetaPeriodo = carpetaMateria2.getCarpetaPadre().getCarpetaPadre();
					Carpeta carpetaDocente = carpetaMateria2.getCarpetaPadre();

					// carreraService.findOne(obtenerIdCarpetaPadreRecursivo(carpeta));
					Carpeta carpeta3 = carpetaService.findOne(obtenerIdCarpetaPadreRecursivo(carpeta2));
					String numero = carpetaGestion.getNom_carpeta().replaceAll("[^0-9]", "");
					int numeroGestion = Integer.parseInt(numero);
					System.out.println("gestion: " + numeroGestion);

					String numero2 = carpetaPeriodo.getNom_carpeta().replaceAll("[^0-9]", "");
					int numeroPeriodo = Integer.parseInt(numero2);
					System.out.println("periodo: " + numeroPeriodo);

					Map<String, Object> requests = new HashMap<String, Object>();
					requests.put("rd", carpetaDocente.getDocente().getRd());
					requests.put("periodo", numero2);
					requests.put("gestion", numero);
					requests.put("code_carrera", carpeta3.getCarrera().getId_carrera());
					System.out.println("RD: " + carpetaDocente.getDocente().getRd());
					System.out.println("periodo: " + numero2);
					System.out.println("gestion: " + numero);
					System.out.println("code_carrera: " + carpeta3.getCarrera().getId_carrera());
					String url = "http://181.115.188.250:9993/v1/service/api/f4adc106a6bf4902aa0e0e053e753962";
					String key = "key 46bc2f9cface91d161e6bf4f6e27c1aeb67d40d157b082d7a7135a677f5df1fb";

					HttpHeaders headers = new HttpHeaders();

					headers.setContentType(MediaType.APPLICATION_JSON);
					headers.set("x-api-key", key);

					HttpEntity<HashMap> req = new HttpEntity(requests, headers);

					RestTemplate restTemplate = new RestTemplate();

					ResponseEntity<Map> resp = restTemplate.exchange(url, HttpMethod.POST, req, Map.class);

					if (resp.getStatusCode() == HttpStatus.OK) {
						Map<String, Object> responseBody = resp.getBody();
						// Aquí puedes procesar los datos de responseBody
						Map<String, Object> data = (Map<String, Object>) responseBody.get("data");

						// Obtener los valores de "paterno", "ci", "fecha_nacimiento", etc., del objeto
						// "data"

						if (data != null) {

							String nombCarrera = (String) data.get("carrera");
							String paterno = (String) data.get("paterno");
							String ci = (String) data.get("ci");
							String nombre = (String) data.get("nombre");
							String materno = (String) data.get("materno");
							int rd = (int) data.get("rd");
							List<String> correos = (List<String>) data.get("correos");
							/*
							 * List<String> correos = new ArrayList<>();
							 * List<Map<String, String>> correosData = (List<Map<String, String>>)
							 * data.get("correos");
							 * for (Map<String, String> asignaturaData : correosData) {
							 * correos.add(asignaturaData.get("asignatura"));
							 * }
							 */
							System.out.println(nombCarrera);
							System.out.println(paterno);
							System.out.println(ci);
							System.out.println(nombre);
							System.out.println(materno);
							System.out.println(rd);
							String celular = (String) data.get("celular");
							List<String[]> asignaturas = new ArrayList<>();
							List<Map<String, String>> asignaturasData = (List<Map<String, String>>) data
									.get("asignaturas");
							for (Map<String, String> asignaturaData : asignaturasData) {
								String[] asig = { asignaturaData.get("asignatura"), asignaturaData.get("plan"),
										asignaturaData.get("tipo_evaluacion"), asignaturaData.get("sigla"),
										asignaturaData.get("grupo") };
								asignaturas.add(asig);
							}

							for (String[] asignatura : asignaturas) {
								materias.add(materiaService.materiaSigla(asignatura[3]));
							}

						}
						// System.out.println(responseBody);
					} else {
						System.out.println(
								"Error en la solicitud. Código de respuesta: " + resp.getStatusCodeValue());

					}
					// }
					// }
				} else {
					System.out.println(
							"No");
				}
				Set<Materia> materiasUnicasSet = new HashSet<>(materias);
				List<Materia> materiasUnicasList = new ArrayList<>(materiasUnicasSet);

				for (Materia materia : materiasUnicasList) {
					PdfPTable tablaArchivos2 = new PdfPTable(1);
					float tableWidth2 = document.getPageSize().getWidth() - document.leftMargin()
							- document.rightMargin();
					tablaArchivos2.setTotalWidth(tableWidth2);
					// tablaArchivos2.setWidthPercentage(95);

					float[] columnWidths3 = { 2 };
					tablaArchivos2.setWidths(columnWidths3);

					PdfPCell greenCell2 = new PdfPCell(new Phrase(materia.getNombre(), fontNegrilla));
					greenCell2.setHorizontalAlignment(Element.ALIGN_CENTER);
					greenCell2.setBackgroundColor(new BaseColor(200, 200, 200));
					greenCell2.setVerticalAlignment(Element.ALIGN_MIDDLE);
					greenCell2.setHorizontalAlignment(Element.ALIGN_CENTER);
					greenCell2.setPaddingBottom(12);
					greenCell2.setPaddingTop(10);
					tablaArchivos2.addCell(greenCell2);
					document.add(tablaArchivos2);

					PdfPTable tablaArchivosh = new PdfPTable(3);
					float tableWidth3 = document.getPageSize().getWidth() - document.leftMargin()
							- document.rightMargin();
					tablaArchivosh.setTotalWidth(tableWidth3);
					// tablaArchivosh.setWidthPercentage(95);

					float[] columnWidthsh1 = { 3, 6, 1 };
					tablaArchivosh.setWidths(columnWidthsh1);

					PdfPCell titleh1 = new PdfPCell(new Phrase("Requisito", fontNegrilla));
					// titleh1.setRowspan(requisito.getParametros().size());
					titleh1.setVerticalAlignment(Element.ALIGN_MIDDLE);
					titleh1.setHorizontalAlignment(Element.ALIGN_CENTER);
					tablaArchivosh.addCell(titleh1);
					PdfPCell titleh2 = new PdfPCell(new Phrase("Parametro", fontNegrilla));
					titleh2.setVerticalAlignment(Element.ALIGN_MIDDLE);
					titleh2.setHorizontalAlignment(Element.ALIGN_CENTER);
					tablaArchivosh.addCell(titleh2);
					PdfPCell titleh3 = new PdfPCell(new Phrase("Cant. Archivos", fontNegrilla));
					titleh3.setVerticalAlignment(Element.ALIGN_MIDDLE);
					titleh3.setHorizontalAlignment(Element.ALIGN_CENTER);
					tablaArchivosh.addCell(titleh3);

					document.add(tablaArchivosh);

					List<Requisito> nuevosRequisitos = requisitoService
							.listaRequisitosMateria2(materia.getId_materia(), carpeta2.getId_carpeta());

					for (Requisito requisito : nuevosRequisitos) {

						PdfPTable tablarequisito = new PdfPTable(3);
						float tableWidthRe = document.getPageSize().getWidth() - document.leftMargin()
								- document.rightMargin();
						tablarequisito.setTotalWidth(tableWidthRe);
						// tablarequisito.setWidthPercentage(95);

						float[] columnWidths = { 3, 6, 1 };
						tablarequisito.setWidths(columnWidths);

						PdfPCell titleCell = new PdfPCell(new Phrase(requisito.getNombre(), fontNegrilla9));
						titleCell.setRowspan(requisito.getParametros().size());
						titleCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
						titleCell.setHorizontalAlignment(Element.ALIGN_CENTER);
						tablarequisito.addCell(titleCell);

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

							PdfPCell titleCell2 = new PdfPCell(new Phrase(parametro.getNombre(), fontSimple9));
							titleCell2.setVerticalAlignment(Element.ALIGN_MIDDLE);
							titleCell2.setHorizontalAlignment(Element.ALIGN_CENTER);

							tablarequisito.addCell(titleCell2);

							PdfPCell titleCell3 = new PdfPCell(
									new Phrase(String.valueOf(parametro.getArchivos().size()), fontSimple9));
							titleCell3.setVerticalAlignment(Element.ALIGN_MIDDLE);
							titleCell3.setHorizontalAlignment(Element.ALIGN_CENTER);
							tablarequisito.addCell(titleCell3);

							// document.add(tablarequisito);

						}
						document.add(tablarequisito);

					}
					emptyParagraph.add(" ");
					document.add(emptyParagraph);
				}

			}
		}

		// Cerrar el documento
		document.close();

		return outputStream.toByteArray();
	}

	@GetMapping("/GenerarReporteMateriaPeriodoPDF/{id_carpeta}/{id_carpetaP}/{id_carpetaG}")
	public ResponseEntity<ByteArrayResource> GenerarReporteMateriaPeriodoPDF(Model model, HttpServletRequest request,
			@PathVariable(value = "id_carpeta", required = false) Long id_carpeta,
			@PathVariable(value = "id_carpetaP", required = false) Long id_carpetaP,
			@PathVariable(value = "id_carpetaG", required = false) Long id_carpetaG)
			throws DocumentException, MalformedURLException, IOException, com.itextpdf.text.DocumentException {

		Carpeta carpetaGes = carpetaService.findOne(id_carpetaG);
		// model.addAttribute("carpetaGestion", carpetaGes);

		Carpeta carpetaPer = carpetaService.findOne(id_carpetaP);
		// model.addAttribute("carpetaPeriodo", carpetaPer);

		List<Carpeta> carpetasDocente = carpetaPer.getCarpetasHijos();

		byte[] bytes = generarPdfP(carpetaGes, carpetaPer, carpetasDocente);

		ByteArrayResource resource = new ByteArrayResource(bytes);

		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION,
						"inline;filename=" + "Reporte de Control de Archivos Docente.pdf")
				.contentType(MediaType.APPLICATION_PDF)
				.contentLength(bytes.length)
				.body(resource);
	}

	public byte[] generarPdfP(Carpeta carpetaGes, Carpeta carpetaPer, List<Carpeta> carpetasDocente)
			throws IOException, DocumentException, com.itextpdf.text.DocumentException {

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

		Document document = new Document(PageSize.LETTER, 30f, 20f, 20f, 70f);

		Paragraph emptyParagraph = new Paragraph();

		PdfWriter writer = PdfWriter.getInstance(document, outputStream);

		document.open();

		Path projectPath = Paths.get("").toAbsolutePath();
		String fuenteCalibriRegular = projectPath
				+ "/acreditacion/src/main/resources/static/fuenteLetra/Calibri Regular.ttf";
		String fuenteCalibriBold = projectPath + "/acreditacion/src/main/resources/static/fuenteLetra/Calibri Bold.ttf";
		Font fontSimple = FontFactory.getFont(fuenteCalibriRegular, BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 11);
		Font fontNegrilla = FontFactory.getFont(fuenteCalibriBold, BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 11);
		Font fontSimple9 = FontFactory.getFont(fuenteCalibriRegular, BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 9);
		Font fontNegrilla9 = FontFactory.getFont(fuenteCalibriBold, BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 9);

		emptyParagraph.add(" ");
		document.add(emptyParagraph);

		/* tabla datos DOCENTE */
		PdfPTable tablaSegunda = new PdfPTable(2);
		float tableWidth = document.getPageSize().getWidth() - document.leftMargin() - document.rightMargin();
		tablaSegunda.setTotalWidth(tableWidth);
		// tablaSegunda.setWidthPercentage(95);

		float[] columnWidths2 = { 3.5f, 6f };
		tablaSegunda.setWidths(columnWidths2);

		PdfPCell cell5 = new PdfPCell(new Phrase("GESTION:", fontNegrilla));

		PdfPCell cell6 = new PdfPCell(
				new Phrase(carpetaGes.getNom_carpeta(), fontSimple));

		PdfPCell cell3 = new PdfPCell(new Phrase("PERIODO:", fontNegrilla));

		PdfPCell cell4 = new PdfPCell(
				new Phrase(carpetaPer.getNom_carpeta(), fontSimple));

		// Agregar las celdas a la tabla
		tablaSegunda.addCell(cell5);
		tablaSegunda.addCell(cell6);
		tablaSegunda.addCell(cell3);
		tablaSegunda.addCell(cell4);

		document.add(tablaSegunda);
		// canvas2.restoreState();
		emptyParagraph.add(" ");
		document.add(emptyParagraph);

		int contador = 0;
		for (Carpeta carpetaDoc : carpetaPer.getCarpetasHijos()) {
			contador++;
			PdfPTable tablaDocente = new PdfPTable(4);
			float tableWidthDoc = document.getPageSize().getWidth() - document.leftMargin() - document.rightMargin();
			tablaSegunda.setTotalWidth(tableWidthDoc);
			// tablaSegunda.setWidthPercentage(95);

			float[] columnWidthsDoc = { 1f, 1, 3.5f, 6f };
			tablaDocente.setWidths(columnWidthsDoc);

			PdfPCell Nro = new PdfPCell(new Phrase("Nro:", fontNegrilla));
			Nro.setVerticalAlignment(Element.ALIGN_MIDDLE);
			Nro.setHorizontalAlignment(Element.ALIGN_CENTER);
			tablaDocente.addCell(Nro);

			PdfPCell contadorN = new PdfPCell(new Phrase(String.valueOf(contador), fontNegrilla));
			contadorN.setVerticalAlignment(Element.ALIGN_MIDDLE);
			contadorN.setHorizontalAlignment(Element.ALIGN_CENTER);
			tablaDocente.addCell(contadorN);

			PdfPCell Docente = new PdfPCell(new Phrase("Docente:", fontNegrilla));
			Docente.setVerticalAlignment(Element.ALIGN_MIDDLE);
			Docente.setHorizontalAlignment(Element.ALIGN_CENTER);
			tablaDocente.addCell(Docente);

			PdfPCell DocenteN = new PdfPCell(
					new Phrase(carpetaDoc.getNom_carpeta(), fontSimple));
			DocenteN.setVerticalAlignment(Element.ALIGN_MIDDLE);
			DocenteN.setHorizontalAlignment(Element.ALIGN_CENTER);
			tablaDocente.addCell(DocenteN);

			document.add(tablaDocente);

			PdfPTable tablaTitulo = new PdfPTable(4);
			float tableWidth3 = document.getPageSize().getWidth() - document.leftMargin()
					- document.rightMargin();
			tablaTitulo.setTotalWidth(tableWidth3);
			// tablaArchivosh.setWidthPercentage(95);

			float[] columnWidthsh1 = { 2, 3, 6, 1 };
			tablaTitulo.setWidths(columnWidthsh1);

			PdfPCell titlehm = new PdfPCell(new Phrase("Materia", fontNegrilla9));
			// titleh1.setRowspan(requisito.getParametros().size());
			titlehm.setVerticalAlignment(Element.ALIGN_MIDDLE);
			titlehm.setHorizontalAlignment(Element.ALIGN_CENTER);
			tablaTitulo.addCell(titlehm);

			PdfPCell titleh1 = new PdfPCell(new Phrase("Requisito", fontNegrilla9));
			// titleh1.setRowspan(requisito.getParametros().size());
			titleh1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			titleh1.setHorizontalAlignment(Element.ALIGN_CENTER);
			tablaTitulo.addCell(titleh1);

			PdfPCell titleh2 = new PdfPCell(new Phrase("Parametro", fontNegrilla9));
			titleh2.setVerticalAlignment(Element.ALIGN_MIDDLE);
			titleh2.setHorizontalAlignment(Element.ALIGN_CENTER);
			tablaTitulo.addCell(titleh2);

			PdfPCell titleh3 = new PdfPCell(new Phrase("Cant. Archivos", fontNegrilla9));
			titleh3.setVerticalAlignment(Element.ALIGN_MIDDLE);
			titleh3.setHorizontalAlignment(Element.ALIGN_CENTER);
			tablaTitulo.addCell(titleh3);

			document.add(tablaTitulo);

			for (Carpeta carpeta2 : carpetaDoc.getCarpetasHijos()) {
				if (carpeta2.getNom_carpeta().equals("MATERIAS") && !carpeta2.getEstado().equals("X")) {
					for (Materia materia : carpeta2.getMaterias()) {

						materia.getRequisitos().clear();

						List<Requisito> nuevosRequisitos = requisitoService
								.listaRequisitosMateria2(materia.getId_materia(), carpeta2.getId_carpeta());

						materia.getRequisitos().addAll(nuevosRequisitos);

					}
				}
			}

			for (Carpeta carpeta2 : carpetaDoc.getCarpetasHijos()) {
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

			for (Carpeta carpeta2 : carpetaDoc.getCarpetasHijos()) {
				if (carpeta2.getNom_carpeta().equals("MATERIAS") && !carpeta2.getEstado().equals("X")) {
					// CARGAR MATERIAS CON API
					// LISTAR MATERIAS DE DOCENTES
					List<Materia> materias = new ArrayList<>();
					Carpeta carpetaMateria = carpeta2;
					System.out.println("MATERIAS DE DOCENTES: " + carpetaMateria.getNom_carpeta());
					if (carpetaMateria.getNom_carpeta().equals("MATERIAS")) {
						System.out.println("VERDADERO 1");
						// if (carpeta.getCarpetaPadre().getCarpetaPadre() != null) {
						System.out.println("VERDADERO 2");
						// if (carpeta.getCarpetaPadre().getCarpetaPadre().getCarpetaPadre() != null) {
						Carpeta carpetaGestion = carpetaMateria.getCarpetaPadre().getCarpetaPadre()
								.getCarpetaPadre();
						Carpeta carpetaPeriodo = carpetaMateria.getCarpetaPadre().getCarpetaPadre();
						Carpeta carpetaDocente = carpetaMateria.getCarpetaPadre();

						// carreraService.findOne(obtenerIdCarpetaPadreRecursivo(carpeta));
						Carpeta carpeta3 = carpetaService.findOne(obtenerIdCarpetaPadreRecursivo(carpeta2));
						String numero = carpetaGestion.getNom_carpeta().replaceAll("[^0-9]", "");
						int numeroGestion = Integer.parseInt(numero);
						System.out.println("gestion: " + numeroGestion);

						String numero2 = carpetaPeriodo.getNom_carpeta().replaceAll("[^0-9]", "");
						int numeroPeriodo = Integer.parseInt(numero2);
						System.out.println("periodo: " + numeroPeriodo);

						Map<String, Object> requests = new HashMap<String, Object>();
						requests.put("rd", carpetaDocente.getDocente().getRd());
						requests.put("periodo", numero2);
						requests.put("gestion", numero);
						requests.put("code_carrera", carpeta3.getCarrera().getId_carrera());
						System.out.println("RD: " + carpetaDocente.getDocente().getRd());
						System.out.println("periodo: " + numero2);
						System.out.println("gestion: " + numero);
						System.out.println("code_carrera: " + carpeta3.getCarrera().getId_carrera());
						String url = "http://181.115.188.250:9993/v1/service/api/f4adc106a6bf4902aa0e0e053e753962";
						String key = "key 46bc2f9cface91d161e6bf4f6e27c1aeb67d40d157b082d7a7135a677f5df1fb";

						HttpHeaders headers = new HttpHeaders();

						headers.setContentType(MediaType.APPLICATION_JSON);
						headers.set("x-api-key", key);

						HttpEntity<HashMap> req = new HttpEntity(requests, headers);

						RestTemplate restTemplate = new RestTemplate();

						ResponseEntity<Map> resp = restTemplate.exchange(url, HttpMethod.POST, req, Map.class);

						if (resp.getStatusCode() == HttpStatus.OK) {
							Map<String, Object> responseBody = resp.getBody();
							// Aquí puedes procesar los datos de responseBody
							Map<String, Object> data = (Map<String, Object>) responseBody.get("data");

							// Obtener los valores de "paterno", "ci", "fecha_nacimiento", etc., del objeto
							// "data"

							if (data != null) {

								String nombCarrera = (String) data.get("carrera");
								String paterno = (String) data.get("paterno");
								String ci = (String) data.get("ci");
								String nombre = (String) data.get("nombre");
								String materno = (String) data.get("materno");
								int rd = (int) data.get("rd");
								List<String> correos = (List<String>) data.get("correos");
								/*
								 * List<String> correos = new ArrayList<>();
								 * List<Map<String, String>> correosData = (List<Map<String, String>>)
								 * data.get("correos");
								 * for (Map<String, String> asignaturaData : correosData) {
								 * correos.add(asignaturaData.get("asignatura"));
								 * }
								 */
								System.out.println(nombCarrera);
								System.out.println(paterno);
								System.out.println(ci);
								System.out.println(nombre);
								System.out.println(materno);
								System.out.println(rd);
								String celular = (String) data.get("celular");
								List<String[]> asignaturas = new ArrayList<>();
								List<Map<String, String>> asignaturasData = (List<Map<String, String>>) data
										.get("asignaturas");
								for (Map<String, String> asignaturaData : asignaturasData) {
									String[] asig = { asignaturaData.get("asignatura"), asignaturaData.get("plan"),
											asignaturaData.get("tipo_evaluacion"), asignaturaData.get("sigla"),
											asignaturaData.get("grupo") };
									asignaturas.add(asig);
								}

								for (String[] asignatura : asignaturas) {
									materias.add(materiaService.materiaSigla(asignatura[3]));
								}

							}
							// System.out.println(responseBody);
						} else {
							System.out.println(
									"Error en la solicitud. Código de respuesta: " + resp.getStatusCodeValue());

						}
						// }
						// }
					} else {
						System.out.println(
								"No");
					}
					Set<Materia> materiasUnicasSet = new HashSet<>(materias);
					List<Materia> materiasUnicasList = new ArrayList<>(materiasUnicasSet);

					for (Materia materia : materiasUnicasList) {

						int total = materia.getRequisitos().size();

						for (Requisito requisito : materia.getRequisitos()) {
							total += requisito.getParametros().size();
						}

						List<Requisito> nuevosRequisitos = requisitoService
								.listaRequisitosMateria2(materia.getId_materia(), carpeta2.getId_carpeta());

						PdfPTable tablarequisito = new PdfPTable(4);
						float tableWidthRe = document.getPageSize().getWidth() - document.leftMargin()
								- document.rightMargin();
						tablarequisito.setTotalWidth(tableWidthRe);
						float[] columnWidths = { 2, 3, 6, 1 };
						tablarequisito.setWidths(columnWidths);

						PdfPCell greenCell2 = new PdfPCell(new Phrase(materia.getNombre(), fontNegrilla));
						greenCell2.setRowspan(total);
						greenCell2.setHorizontalAlignment(Element.ALIGN_CENTER);
						greenCell2.setBackgroundColor(new BaseColor(200, 200, 200));
						greenCell2.setVerticalAlignment(Element.ALIGN_MIDDLE);
						greenCell2.setHorizontalAlignment(Element.ALIGN_CENTER);

						tablarequisito.addCell(greenCell2);

						for (Requisito requisito : nuevosRequisitos) {

							// tablarequisito.setWidthPercentage(95);

							PdfPCell titleCell = new PdfPCell(new Phrase(requisito.getNombre(), fontNegrilla9));
							titleCell.setRowspan(requisito.getParametros().size());
							titleCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
							titleCell.setHorizontalAlignment(Element.ALIGN_CENTER);
							tablarequisito.addCell(titleCell);

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

								PdfPCell titleCell2 = new PdfPCell(new Phrase(parametro.getNombre(), fontSimple9));
								titleCell2.setVerticalAlignment(Element.ALIGN_MIDDLE);
								titleCell2.setHorizontalAlignment(Element.ALIGN_CENTER);

								tablarequisito.addCell(titleCell2);

								PdfPCell titleCell3 = new PdfPCell(
										new Phrase(String.valueOf(parametro.getArchivos().size()), fontSimple9));
								titleCell3.setVerticalAlignment(Element.ALIGN_MIDDLE);
								titleCell3.setHorizontalAlignment(Element.ALIGN_CENTER);
								tablarequisito.addCell(titleCell3);

							}

						}
						document.add(tablarequisito);
					}

				}
			}
			emptyParagraph.add(" ");
			document.add(emptyParagraph);
		}

		// Cerrar el documento
		document.close();

		return outputStream.toByteArray();
	}

	@GetMapping("/GenerarReporteMateriaGestionPDF/{id_carpeta}/{id_carpetaG}")
	public ResponseEntity<ByteArrayResource> GenerarReporteMateriaGestionPDF(Model model, HttpServletRequest request,
			@PathVariable(value = "id_carpeta", required = false) Long id_carpeta,
			@PathVariable(value = "id_carpetaP", required = false) Long id_carpetaP,
			@PathVariable(value = "id_carpetaG", required = false) Long id_carpetaG)
			throws DocumentException, MalformedURLException, IOException, com.itextpdf.text.DocumentException {

		Carpeta carpetaGes = carpetaService.findOne(id_carpetaG);
		// model.addAttribute("carpetaGestion", carpetaGes);

		List<Carpeta> carpetasPeriodo = carpetaGes.getCarpetasHijos();

		byte[] bytes = generarPdfG(carpetaGes, carpetasPeriodo);

		ByteArrayResource resource = new ByteArrayResource(bytes);

		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION,
						"inline;filename=" + "Reporte de Control de Archivos Docente.pdf")
				.contentType(MediaType.APPLICATION_PDF)
				.contentLength(bytes.length)
				.body(resource);
	}

	public byte[] generarPdfG(Carpeta carpetaGes, List<Carpeta> carpetasPeriodo)
			throws IOException, DocumentException, com.itextpdf.text.DocumentException {

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

		// Document document = new Document(PageSize.LETTER, 30f, 20f, 20f, 40f);
		Document document = new Document(PageSize.LETTER, 30f, 20f, 20f, 70f);

		Paragraph emptyParagraph = new Paragraph();

		PdfWriter writer = PdfWriter.getInstance(document, outputStream);

		document.open();

		Path projectPath = Paths.get("").toAbsolutePath();
		String fuenteCalibriRegular = projectPath
				+ "/acreditacion/src/main/resources/static/fuenteLetra/Calibri Regular.ttf";
		String fuenteCalibriBold = projectPath + "/acreditacion/src/main/resources/static/fuenteLetra/Calibri Bold.ttf";
		Font fontSimple = FontFactory.getFont(fuenteCalibriRegular, BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 11);
		Font fontNegrilla = FontFactory.getFont(fuenteCalibriBold, BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 11);
		Font fontSimple9 = FontFactory.getFont(fuenteCalibriRegular, BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 9);
		Font fontNegrilla9 = FontFactory.getFont(fuenteCalibriBold, BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 9);

		emptyParagraph.add(" ");
		document.add(emptyParagraph);

		/* tabla datos DOCENTE */
		PdfPTable tablaSegunda = new PdfPTable(2);
		float tableWidth = document.getPageSize().getWidth() - document.leftMargin() - document.rightMargin();
		tablaSegunda.setTotalWidth(tableWidth);
		// tablaSegunda.setWidthPercentage(95);

		float[] columnWidths2 = { 3.5f, 6f };
		tablaSegunda.setWidths(columnWidths2);

		PdfPCell cell5 = new PdfPCell(new Phrase("GESTION:", fontNegrilla));

		PdfPCell cell6 = new PdfPCell(
				new Phrase(carpetaGes.getNom_carpeta(), fontSimple));

		// Agregar las celdas a la tabla
		tablaSegunda.addCell(cell5);
		tablaSegunda.addCell(cell6);

		document.add(tablaSegunda);
		// canvas2.restoreState();
		emptyParagraph.add(" ");
		document.add(emptyParagraph);

		int contador = 0;
		for (Carpeta carpetaPer : carpetaGes.getCarpetasHijos()) {

			PdfPTable tablaPer = new PdfPTable(1);
			float tableWidthPer = document.getPageSize().getWidth() - document.leftMargin() - document.rightMargin();
			tablaSegunda.setTotalWidth(tableWidthPer);
			// tablaSegunda.setWidthPercentage(95);

			float[] columnWidthsPer = { 5 };
			tablaPer.setWidths(columnWidthsPer);

			PdfPCell cellPer = new PdfPCell(new Phrase(carpetaPer.getNom_carpeta(), fontNegrilla));
			tablaPer.addCell(cellPer);

			document.add(tablaPer);

			for (Carpeta carpetaDoc : carpetaPer.getCarpetasHijos()) {
				contador++;
				PdfPTable tablaDocente = new PdfPTable(4);
				float tableWidthDoc = document.getPageSize().getWidth() - document.leftMargin()
						- document.rightMargin();
				tablaSegunda.setTotalWidth(tableWidthDoc);
				// tablaSegunda.setWidthPercentage(95);

				float[] columnWidthsDoc = { 1f, 1, 3.5f, 6f };
				tablaDocente.setWidths(columnWidthsDoc);

				PdfPCell Nro = new PdfPCell(new Phrase("Nro:", fontNegrilla));
				Nro.setVerticalAlignment(Element.ALIGN_MIDDLE);
				Nro.setHorizontalAlignment(Element.ALIGN_CENTER);
				tablaDocente.addCell(Nro);

				PdfPCell contadorN = new PdfPCell(new Phrase(String.valueOf(contador), fontNegrilla));
				contadorN.setVerticalAlignment(Element.ALIGN_MIDDLE);
				contadorN.setHorizontalAlignment(Element.ALIGN_CENTER);
				tablaDocente.addCell(contadorN);

				PdfPCell Docente = new PdfPCell(new Phrase("Docente:", fontNegrilla));
				Docente.setVerticalAlignment(Element.ALIGN_MIDDLE);
				Docente.setHorizontalAlignment(Element.ALIGN_CENTER);
				tablaDocente.addCell(Docente);

				PdfPCell DocenteN = new PdfPCell(
						new Phrase(carpetaDoc.getNom_carpeta(), fontSimple));
				DocenteN.setVerticalAlignment(Element.ALIGN_MIDDLE);
				DocenteN.setHorizontalAlignment(Element.ALIGN_CENTER);
				tablaDocente.addCell(DocenteN);

				document.add(tablaDocente);

				PdfPTable tablaTitulo = new PdfPTable(4);
				float tableWidth3 = document.getPageSize().getWidth() - document.leftMargin()
						- document.rightMargin();
				tablaTitulo.setTotalWidth(tableWidth3);
				// tablaArchivosh.setWidthPercentage(95);

				float[] columnWidthsh1 = { 2, 3, 6, 1 };
				tablaTitulo.setWidths(columnWidthsh1);

				PdfPCell titlehm = new PdfPCell(new Phrase("Materia", fontNegrilla9));
				// titleh1.setRowspan(requisito.getParametros().size());
				titlehm.setVerticalAlignment(Element.ALIGN_MIDDLE);
				titlehm.setHorizontalAlignment(Element.ALIGN_CENTER);
				tablaTitulo.addCell(titlehm);

				PdfPCell titleh1 = new PdfPCell(new Phrase("Requisito", fontNegrilla9));
				// titleh1.setRowspan(requisito.getParametros().size());
				titleh1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				titleh1.setHorizontalAlignment(Element.ALIGN_CENTER);
				tablaTitulo.addCell(titleh1);

				PdfPCell titleh2 = new PdfPCell(new Phrase("Parametro", fontNegrilla9));
				titleh2.setVerticalAlignment(Element.ALIGN_MIDDLE);
				titleh2.setHorizontalAlignment(Element.ALIGN_CENTER);
				tablaTitulo.addCell(titleh2);

				PdfPCell titleh3 = new PdfPCell(new Phrase("Cant. Archivos", fontNegrilla9));
				titleh3.setVerticalAlignment(Element.ALIGN_MIDDLE);
				titleh3.setHorizontalAlignment(Element.ALIGN_CENTER);
				tablaTitulo.addCell(titleh3);

				document.add(tablaTitulo);

				for (Carpeta carpeta2 : carpetaDoc.getCarpetasHijos()) {
					if (carpeta2.getNom_carpeta().equals("MATERIAS") && !carpeta2.getEstado().equals("X")) {
						for (Materia materia : carpeta2.getMaterias()) {

							materia.getRequisitos().clear();

							List<Requisito> nuevosRequisitos = requisitoService
									.listaRequisitosMateria2(materia.getId_materia(), carpeta2.getId_carpeta());

							materia.getRequisitos().addAll(nuevosRequisitos);

						}
					}
				}

				for (Carpeta carpeta2 : carpetaDoc.getCarpetasHijos()) {
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

				for (Carpeta carpeta2 : carpetaDoc.getCarpetasHijos()) {
					List<Materia> materias = new ArrayList<>();
					if (carpeta2.getNom_carpeta().equals("MATERIAS") && !carpeta2.getEstado().equals("X")) {

						Carpeta carpetaMateria = carpeta2;
						System.out.println("MATERIAS DE DOCENTES: " + carpetaMateria.getNom_carpeta());
						if (carpetaMateria.getNom_carpeta().equals("MATERIAS")) {
							System.out.println("VERDADERO 1");
							// if (carpeta.getCarpetaPadre().getCarpetaPadre() != null) {
							System.out.println("VERDADERO 2");
							// if (carpeta.getCarpetaPadre().getCarpetaPadre().getCarpetaPadre() != null) {
							Carpeta carpetaGestion = carpetaMateria.getCarpetaPadre().getCarpetaPadre()
									.getCarpetaPadre();
							Carpeta carpetaPeriodo = carpetaMateria.getCarpetaPadre().getCarpetaPadre();
							Carpeta carpetaDocente = carpetaMateria.getCarpetaPadre();

							// carreraService.findOne(obtenerIdCarpetaPadreRecursivo(carpeta));
							Carpeta carpeta3 = carpetaService.findOne(obtenerIdCarpetaPadreRecursivo(carpeta2));
							String numero = carpetaGestion.getNom_carpeta().replaceAll("[^0-9]", "");
							int numeroGestion = Integer.parseInt(numero);
							System.out.println("gestion: " + numeroGestion);

							String numero2 = carpetaPeriodo.getNom_carpeta().replaceAll("[^0-9]", "");
							int numeroPeriodo = Integer.parseInt(numero2);
							System.out.println("periodo: " + numeroPeriodo);

							Map<String, Object> requests = new HashMap<String, Object>();
							requests.put("rd", carpetaDocente.getDocente().getRd());
							requests.put("periodo", numero2);
							requests.put("gestion", numero);
							requests.put("code_carrera", carpeta3.getCarrera().getId_carrera());
							System.out.println("RD: " + carpetaDocente.getDocente().getRd());
							System.out.println("periodo: " + numero2);
							System.out.println("gestion: " + numero);
							System.out.println("code_carrera: " + carpeta3.getCarrera().getId_carrera());
							String url = "http://181.115.188.250:9993/v1/service/api/f4adc106a6bf4902aa0e0e053e753962";
							String key = "key 46bc2f9cface91d161e6bf4f6e27c1aeb67d40d157b082d7a7135a677f5df1fb";

							HttpHeaders headers = new HttpHeaders();

							headers.setContentType(MediaType.APPLICATION_JSON);
							headers.set("x-api-key", key);

							HttpEntity<HashMap> req = new HttpEntity(requests, headers);

							RestTemplate restTemplate = new RestTemplate();

							ResponseEntity<Map> resp = restTemplate.exchange(url, HttpMethod.POST, req, Map.class);

							if (resp.getStatusCode() == HttpStatus.OK) {
								Map<String, Object> responseBody = resp.getBody();
								// Aquí puedes procesar los datos de responseBody
								Map<String, Object> data = (Map<String, Object>) responseBody.get("data");

								// Obtener los valores de "paterno", "ci", "fecha_nacimiento", etc., del objeto
								// "data"

								if (data != null) {

									String nombCarrera = (String) data.get("carrera");
									String paterno = (String) data.get("paterno");
									String ci = (String) data.get("ci");
									String nombre = (String) data.get("nombre");
									String materno = (String) data.get("materno");
									int rd = (int) data.get("rd");
									List<String> correos = (List<String>) data.get("correos");
									/*
									 * List<String> correos = new ArrayList<>();
									 * List<Map<String, String>> correosData = (List<Map<String, String>>)
									 * data.get("correos");
									 * for (Map<String, String> asignaturaData : correosData) {
									 * correos.add(asignaturaData.get("asignatura"));
									 * }
									 */
									System.out.println(nombCarrera);
									System.out.println(paterno);
									System.out.println(ci);
									System.out.println(nombre);
									System.out.println(materno);
									System.out.println(rd);
									String celular = (String) data.get("celular");
									List<String[]> asignaturas = new ArrayList<>();
									List<Map<String, String>> asignaturasData = (List<Map<String, String>>) data
											.get("asignaturas");
									for (Map<String, String> asignaturaData : asignaturasData) {
										String[] asig = { asignaturaData.get("asignatura"), asignaturaData.get("plan"),
												asignaturaData.get("tipo_evaluacion"), asignaturaData.get("sigla"),
												asignaturaData.get("grupo") };
										asignaturas.add(asig);
									}

									for (String[] asignatura : asignaturas) {
										materias.add(materiaService.materiaSigla(asignatura[3]));
									}

								}
								// System.out.println(responseBody);
							} else {
								System.out.println(
										"Error en la solicitud. Código de respuesta: " + resp.getStatusCodeValue());

							}
							// }
							// }
						} else {
							System.out.println(
									"No");
						}
						Set<Materia> materiasUnicasSet = new HashSet<>(materias);
						List<Materia> materiasUnicasList = new ArrayList<>(materiasUnicasSet);
						// for (Materia materia : carpeta3.getMaterias()) {
						for (Materia materia : materiasUnicasList) {

							int total = materia.getRequisitos().size();

							for (Requisito requisito : materia.getRequisitos()) {
								total += requisito.getParametros().size();
							}

							List<Requisito> nuevosRequisitos = requisitoService
									.listaRequisitosMateria2(materia.getId_materia(), carpeta2.getId_carpeta());

							PdfPTable tablarequisito = new PdfPTable(4);
							float tableWidthRe = document.getPageSize().getWidth() - document.leftMargin()
									- document.rightMargin();
							tablarequisito.setTotalWidth(tableWidthRe);
							float[] columnWidths = { 2, 3, 6, 1 };
							tablarequisito.setWidths(columnWidths);

							PdfPCell greenCell2 = new PdfPCell(new Phrase(materia.getNombre(), fontNegrilla));
							greenCell2.setRowspan(total);
							greenCell2.setHorizontalAlignment(Element.ALIGN_CENTER);
							greenCell2.setBackgroundColor(new BaseColor(200, 200, 200));
							greenCell2.setVerticalAlignment(Element.ALIGN_MIDDLE);
							greenCell2.setHorizontalAlignment(Element.ALIGN_CENTER);

							tablarequisito.addCell(greenCell2);

							for (Requisito requisito : nuevosRequisitos) {

								// tablarequisito.setWidthPercentage(95);

								PdfPCell titleCell = new PdfPCell(new Phrase(requisito.getNombre(), fontNegrilla9));
								titleCell.setRowspan(requisito.getParametros().size());
								titleCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
								titleCell.setHorizontalAlignment(Element.ALIGN_CENTER);
								tablarequisito.addCell(titleCell);

								for (Parametro parametro : requisito.getParametros()) {

									System.out.println("REQUISITO: " + requisito.getNombre());
									System.out.println("PARAMETRO: " + parametro.getNombre() + "-"
											+ parametro.getId_parametro() + " CARPETA: " + carpeta2.getNom_carpeta()
											+ "-"
											+ carpeta2.getId_carpeta() + " MATERIA: " + materia.getNombre() + "-"
											+ materia.getId_materia());
									parametro.getArchivos().clear();
									List<Archivo> archivos = archivoService.archivoParametro(
											parametro.getId_parametro(), carpeta2.getId_carpeta(),
											materia.getId_materia());

									parametro.getArchivos().addAll(archivos);

									System.out.println("TOTAL ARCHIVOS: " + parametro.getArchivos().size());

									PdfPCell titleCell2 = new PdfPCell(new Phrase(parametro.getNombre(), fontSimple9));
									titleCell2.setVerticalAlignment(Element.ALIGN_MIDDLE);
									titleCell2.setHorizontalAlignment(Element.ALIGN_CENTER);

									tablarequisito.addCell(titleCell2);

									PdfPCell titleCell3 = new PdfPCell(
											new Phrase(String.valueOf(parametro.getArchivos().size()), fontSimple9));
									titleCell3.setVerticalAlignment(Element.ALIGN_MIDDLE);
									titleCell3.setHorizontalAlignment(Element.ALIGN_CENTER);
									tablarequisito.addCell(titleCell3);

								}

							}
							document.add(tablarequisito);
						}
						// .setMargins(30f, 20f, 20f, 70f);
					}
				}

				emptyParagraph.add(" ");
				document.add(emptyParagraph);

			}

			document.setMargins(30f, 20f, 20f, 70f);
			document.newPage();

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

	// ---------------------- CONSULTA API --------------------

	@PostMapping("/consultaApiDocenteRD")
	public String consultaApiDocenteRD(RedirectAttributes redirectAttrs,
			@RequestParam(value = "idPadre") Long id_carpeta_anterior,
			@RequestParam(value = "codigoDocente") String codigoDocente,
			@RequestParam(value = "carrera") Long id_carrera,
			@RequestParam(value = "gestion") String gestion,
			@RequestParam(value = "periodo") String periodo) {

		Carrera carrera = carreraService.findOne(id_carrera);
		System.out.println("codeCarrer: " + carrera.getId_carrera());

		String numero = gestion.replaceAll("[^0-9]", "");
		int numeroGestion = Integer.parseInt(numero);
		System.out.println("gestion: " + numeroGestion);

		String numero2 = periodo.replaceAll("[^0-9]", "");
		int numeroPeriodo = Integer.parseInt(numero2);
		System.out.println("periodo: " + numeroPeriodo);
		System.out.println("rd: " + codigoDocente);

		Map<String, Object> requests = new HashMap<String, Object>();
		requests.put("rd", codigoDocente);
		requests.put("periodo", numeroPeriodo);
		requests.put("gestion", numeroGestion);
		requests.put("code_carrera", carrera.getId_carrera());

		String url = "http://181.115.188.250:9993/v1/service/api/f4adc106a6bf4902aa0e0e053e753962";
		String key = "key 46bc2f9cface91d161e6bf4f6e27c1aeb67d40d157b082d7a7135a677f5df1fb";

		HttpHeaders headers = new HttpHeaders();

		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("x-api-key", key);

		HttpEntity<HashMap> req = new HttpEntity(requests, headers);

		RestTemplate restTemplate = new RestTemplate();

		ResponseEntity<Map> resp = restTemplate.exchange(url, HttpMethod.POST, req, Map.class);

		if (resp.getStatusCode() == HttpStatus.OK) {
			Map<String, Object> responseBody = resp.getBody();
			// Aquí puedes procesar los datos de responseBody
			Map<String, Object> data = (Map<String, Object>) responseBody.get("data");

			// Obtener los valores de "paterno", "ci", "fecha_nacimiento", etc., del objeto
			// "data"

			if (data != null) {

				String nombCarrera = (String) data.get("carrera");
				String paterno = (String) data.get("paterno");
				String ci = (String) data.get("ci");
				String nombre = (String) data.get("nombre");
				String materno = (String) data.get("materno");
				int rd = (int) data.get("rd");
				List<String> correos = (List<String>) data.get("correos");
				/*
				 * List<String> correos = new ArrayList<>();
				 * List<Map<String, String>> correosData = (List<Map<String, String>>)
				 * data.get("correos");
				 * for (Map<String, String> asignaturaData : correosData) {
				 * correos.add(asignaturaData.get("asignatura"));
				 * }
				 */

				String celular = (String) data.get("celular");
				List<String[]> asignaturas = new ArrayList<>();
				List<Map<String, String>> asignaturasData = (List<Map<String, String>>) data.get("asignaturas");
				for (Map<String, String> asignaturaData : asignaturasData) {
					String[] asig = { asignaturaData.get("asignatura"), asignaturaData.get("plan"),
							asignaturaData.get("tipo_evaluacion"), asignaturaData.get("sigla"),
							asignaturaData.get("grupo") };
					asignaturas.add(asig);
				}

				String nombreCompleto = nombre + " " + paterno + " " + materno;
				if (nombCarrera.equals(carreraService.findOne(id_carrera).getNom_carrera())) {
					int contador = 0;
					Carpeta carpetaPadre = carpetaService.findOne(id_carpeta_anterior);
					for (Carpeta carpeta2 : carpetaPadre.getCarpetasHijos()) {
						if (carpeta2.getNom_carpeta().equals(nombreCompleto) && !carpeta2.getEstado().equals("X")) {
							contador = 1;
						}
					}
					if (contador == 0) {
						if (personaService.personaCi(ci) == null) {
							// PERSONA DOCENTE
							System.out.println("REGISTRANDO PERSONA..");
							Persona persona = new Persona();
							persona.setNombre(nombre);
							persona.setAp_paterno(paterno);
							persona.setAp_materno(materno);
							persona.setCi(ci);
							// System.out.println("CORREO 1: "+correos.get(0));
							persona.setEmail(correos.get(0));
							persona.setCarrera(carrera);
							System.out.println("Carrera: " + carrera.getNom_carrera());
							Cargo cargo = cargoService.cargoNombre("Docente");
							Puesto puesto = puestoService.puestoNombre("Docente");
							TipoPersona tipoPersona = tipoPersonaService.tipoPersonaNombre("Docente");
							persona.setCargo(cargo);
							persona.setPuesto(puesto);
							persona.setTipoPersona(tipoPersona);
							persona.setFecha_registro(new Date());
							persona.setEstado("A");
							persona.setImagen_persona("FotoPerfilPrederminada.webp");
							personaService.save(persona);

							System.out.println("REGISTRANDO DOCENTE..");
							Docente docente = new Docente();
							docente.setRd(String.valueOf(rd));
							docente.setPersona(persona);
							docente.setFecha_registro(new Date());
							docenteService.save(docente);

							// CARPETA PRINCIPAL DOCENTE
							System.out.println("CREANDO CARPETA..");
							Carpeta carpeta = new Carpeta();
							carpeta.setRuta_icono("iconoPredeterminadoCarpeta.webp");
							carpeta.setNom_carpeta(nombreCompleto);
							carpeta.setDescripcion(gestion + " - " + periodo);
							carpeta.setCarpetaPadre(carpetaService.findOne(id_carpeta_anterior));
							carpeta.setEstado("A");
							carpeta.setDocente(docente);
							carpeta.setFecha_registro(new Date());
							Set<Usuario> usuarios = new HashSet<>();
							// usuarios.add(usuarioP);
							for (Usuario usuario : carpetaService.findOne(id_carpeta_anterior).getCarpetaPadre()
									.getUsuarios()) {
								usuarios.add(usuario);
							}
							carpeta.setUsuarios(usuarios);
							carpetaService.save(carpeta);

							// CARPETA MATERIAS
							Carpeta carpetaMateria = new Carpeta();
							carpetaMateria.setRuta_icono("iconoPredeterminadoCarpeta.webp");
							carpetaMateria.setNom_carpeta("MATERIAS");
							carpetaMateria.setDescripcion("MATERIAS");
							carpetaMateria.setCarpetaPadre(carpeta);
							carpetaMateria.setEstado("A");
							carpetaMateria.setFecha_registro(new Date());
							Set<Usuario> usuarios2 = new HashSet<>();
							for (Usuario usuario2 : carpeta.getCarpetaPadre().getUsuarios()) {
								usuarios2.add(usuario2);
							}
							carpetaMateria.setUsuarios(usuarios);
							carpetaService.save(carpetaMateria);

							// MATERIA
							System.out.println("CARGANDO MATERIAS..");
							// Set<Materia> materias = new HashSet<>();
							for (String[] asignatura : asignaturas) {
								if (materiaService.materiaSigla(asignatura[3]) == null) {
									TipoEvaluacionMateria evaluacionMateria = evaluacionMateriaService
											.evaluacionMateriaPorNombre(asignatura[2]);
									System.out.println("MATERIA:" + asignatura);
									Materia materia = new Materia();
									materia.setEstado("A");
									materia.setNombre(asignatura[0]);
									materia.setPlan(asignatura[1]);
									if (materia.getTipoEvaluacionMaterias() == null) {
										materia.setTipoEvaluacionMaterias(new HashSet<>());
									}
									if (evaluacionMateria.getMaterias() == null) {
										evaluacionMateria.setMaterias(new HashSet<>());
									}
									materia.getTipoEvaluacionMaterias().add(evaluacionMateria);
									evaluacionMateria.getMaterias().add(materia);
									// materia.setEvaluacion(asignatura[2]);
									materia.setSigla(asignatura[3]);
									materia.setCarpeta(carpetaMateria);
									materia.setFecha_registro(new Date());
									// REQUISITOS
									System.out.println("CARGANDO REQUISITOS..");
									Set<Requisito> requisitos = new HashSet<>(requisitoService.findAll());
									materia.setRequisitos(requisitos);

									// Inicializar el conjunto de asignaturas si es nulo
									if (docente.getAsignatura() == null) {
										docente.setAsignatura(new HashSet<>());
									}

									// Inicializar el conjunto de docentes si es nulo
									if (materia.getDocentes() == null) {
										materia.setDocentes(new HashSet<>());
									}
									materia.getDocentes().add(docente);
									// materia.setDocentes(docentes);
									materiaService.save(materia);
									// materias.add(materia);
									docente.getAsignatura().add(materia);
									docenteService.save(docente);
								} else {
									System.out.println("MATERIA:" + asignatura);
									Materia materia = materiaService.materiaSigla(asignatura[3]);
									for (TipoEvaluacionMateria evaluacionMateria : evaluacionMateriaService.findAll()) {
										if (evaluacionMateria.getNombre().equals(asignatura[2])) {
											if (materia.getTipoEvaluacionMaterias() == null) {
												materia.setTipoEvaluacionMaterias(new HashSet<>());
											}
											if (evaluacionMateria.getMaterias() == null) {
												evaluacionMateria.setMaterias(new HashSet<>());
											}
										}
									}
									materia.setEstado("A");
									materia.setNombre(asignatura[0]);
									materia.setPlan(asignatura[1]);
									for (TipoEvaluacionMateria tipoEvaluacionMateria : materia
											.getTipoEvaluacionMaterias()) {
										// if (!tipoEvaluacionMateria.getNombre().equals(asignatura[2])) {
										if (evaluacionMateriaService
												.evaluacionMateriaPorNombre(asignatura[2]) != tipoEvaluacionMateria) {
											if (materia.getTipoEvaluacionMaterias() == null) {
												materia.setTipoEvaluacionMaterias(new HashSet<>());
											}
											if (tipoEvaluacionMateria.getMaterias() == null) {
												tipoEvaluacionMateria.setMaterias(new HashSet<>());
											}
											materia.getTipoEvaluacionMaterias().add(tipoEvaluacionMateria);
											tipoEvaluacionMateria.getMaterias().add(materia);
										}
									}
									// materia.setEvaluacion(asignatura[2]);
									materia.setSigla(asignatura[3]);
									materia.setCarpeta(carpetaMateria);
									// REQUISITOS
									System.out.println("CARGANDO REQUISITOS..");
									Set<Requisito> requisitos = new HashSet<>(requisitoService.findAll());
									materia.setRequisitos(requisitos);
									// Set<Docente> docentes = new HashSet<>();
									// docentes.add(docente);
									// Inicializar el conjunto de asignaturas si es nulo
									if (docente.getAsignatura() == null) {
										docente.setAsignatura(new HashSet<>());
									}

									// Inicializar el conjunto de docentes si es nulo
									if (materia.getDocentes() == null) {
										materia.setDocentes(new HashSet<>());
									}
									materia.getDocentes().add(docente);
									// materia.setDocentes(docentes);
									materiaService.save(materia);
									// materias.add(materia);
									docente.getAsignatura().add(materia);
									docenteService.save(docente);
								}

							}
							// docente.setAsignatura(materias);
							// docenteService.save(docente);
						} else {
							Docente docente = personaService.personaCi(ci).getDocente();

							// CARPETA PRINCIPAL DOCENTE
							System.out.println("CREANDO CARPETA..");
							Carpeta carpeta = new Carpeta();
							carpeta.setRuta_icono("iconoPredeterminadoCarpeta.webp");
							carpeta.setNom_carpeta(nombreCompleto);
							carpeta.setDescripcion(gestion + " - " + periodo);
							carpeta.setCarpetaPadre(carpetaService.findOne(id_carpeta_anterior));
							carpeta.setEstado("A");
							carpeta.setDocente(docente);
							System.out.println("Docente:" + docente.getRd());
							carpeta.setFecha_registro(new Date());
							Set<Usuario> usuarios = new HashSet<>();
							usuarios.add(personaService.personaCi(ci).getUsuario());
							for (Usuario usuario : carpetaService.findOne(id_carpeta_anterior).getCarpetaPadre()
									.getUsuarios()) {
								usuarios.add(usuario);
							}
							carpeta.setUsuarios(usuarios);
							carpetaService.save(carpeta);

							// CARPETA MATERIAS

							Carpeta carpetaMateria = new Carpeta();
							carpetaMateria.setRuta_icono("iconoPredeterminadoCarpeta.webp");
							carpetaMateria.setNom_carpeta("MATERIAS");
							carpetaMateria.setDescripcion("MATERIAS");
							carpetaMateria.setCarpetaPadre(carpeta);
							carpetaMateria.setEstado("A");
							carpetaMateria.setFecha_registro(new Date());
							Set<Usuario> usuarios2 = new HashSet<>();
							for (Usuario usuario2 : carpeta.getCarpetaPadre().getUsuarios()) {
								usuarios2.add(usuario2);
							}
							carpetaMateria.setUsuarios(usuarios);
							carpetaService.save(carpetaMateria);

							// MATERIA
							System.out.println("CARGANDO MATERIAS..");
							// Set<Materia> materias = new HashSet<>();
							for (String[] asignatura : asignaturas) {
								System.out.println("MATERIA AAAAAA");
								if (materiaService.materiaSigla(asignatura[3]) == null) {
									System.out.println("MATERIA:" + asignatura);
									TipoEvaluacionMateria evaluacionMateria = evaluacionMateriaService
											.evaluacionMateriaPorNombre(asignatura[2]);
									if (evaluacionMateria == null) {
										evaluacionMateria = new TipoEvaluacionMateria();
										evaluacionMateria.setEstado("A");
										evaluacionMateria.setNombre(asignatura[2]);
										evaluacionMateriaService.save(evaluacionMateria);
									}
									System.out.println("MATERIA:" + asignatura);
									Materia materia = new Materia();
									materia.setEstado("A");
									materia.setNombre(asignatura[0]);
									materia.setPlan(asignatura[1]);
									if (materia.getTipoEvaluacionMaterias() == null) {
										materia.setTipoEvaluacionMaterias(new HashSet<>());
									}
									if (evaluacionMateria.getMaterias() == null) {
										evaluacionMateria.setMaterias(new HashSet<>());
									}
									materia.getTipoEvaluacionMaterias().add(evaluacionMateria);
									evaluacionMateria.getMaterias().add(materia);
									// materia.setEvaluacion(asignatura[2]);
									materia.setSigla(asignatura[3]);
									materia.setCarpeta(carpetaMateria);
									materia.setFecha_registro(new Date());
									// REQUISITOS
									System.out.println("CARGANDO REQUISITOS..");
									Set<Requisito> requisitos = new HashSet<>(requisitoService.findAll());
									materia.setRequisitos(requisitos);

									// Inicializar el conjunto de asignaturas si es nulo
									if (docente.getAsignatura() == null) {
										docente.setAsignatura(new HashSet<>());
									}

									// Inicializar el conjunto de docentes si es nulo
									if (materia.getDocentes() == null) {
										materia.setDocentes(new HashSet<>());
									}
									// CARGANDO TABLA INTERMEDIA
									materia.getDocentes().add(docente);
									// materia.setDocentes(docentes);
									materiaService.save(materia);
									// materias.add(materia);
									docente.getAsignatura().add(materia);
									docenteService.save(docente);
								} else {
									System.out.println("MATERIA:" + asignatura);
									Materia materia = materiaService.materiaSigla(asignatura[3]);
									materia.setEstado("A");
									materia.setNombre(asignatura[0]);
									materia.setPlan(asignatura[1]);
									for (TipoEvaluacionMateria tipoEvaluacionMateria : materia
											.getTipoEvaluacionMaterias()) {
										if (!tipoEvaluacionMateria.getNombre().equals(asignatura[2])) {
											if (materia.getTipoEvaluacionMaterias() == null) {
												materia.setTipoEvaluacionMaterias(new HashSet<>());
											}
											if (tipoEvaluacionMateria.getMaterias() == null) {
												tipoEvaluacionMateria.setMaterias(new HashSet<>());
											}
											materia.getTipoEvaluacionMaterias().add(tipoEvaluacionMateria);
											tipoEvaluacionMateria.getMaterias().add(materia);
										}
									}
									// materia.setEvaluacion(asignatura[2]);
									materia.setSigla(asignatura[3]);
									materia.setCarpeta(carpetaMateria);
									// REQUISITOS
									System.out.println("CARGANDO REQUISITOS..");
									Set<Requisito> requisitos = new HashSet<>(requisitoService.findAll());
									materia.setRequisitos(requisitos);

									// Inicializar el conjunto de asignaturas si es nulo
									if (docente.getAsignatura() == null) {
										docente.setAsignatura(new HashSet<>());
									}

									// Inicializar el conjunto de docentes si es nulo
									if (materia.getDocentes() == null) {
										materia.setDocentes(new HashSet<>());
									}
									materia.getDocentes().add(docente);
									// materia.setDocentes(docentes);
									materiaService.save(materia);
									// materias.add(materia);
									docente.getAsignatura().add(materia);
									docenteService.save(docente);
								}
							}
							// docente.setAsignatura(materias);
							// docenteService.save(docente);
						}
					} else {
						redirectAttrs
								.addFlashAttribute("mensaje", "Ya existe una carpeta de este docente en este Periodo")
								.addFlashAttribute("clase", "danger");
						return "redirect:/home/" + id_carpeta_anterior;
					}
				} else {
					System.out.println("RESPUESTA DE LA API: " + nombreCompleto);
					redirectAttrs
							.addFlashAttribute("mensaje",
									"No crear carpeta para este docente, pertenece a otra carrera")
							.addFlashAttribute("clase", "danger");
					return "redirect:/home/" + id_carpeta_anterior;
				}

				System.out.println("RESPUESTA DE LA API: " + nombreCompleto);
				redirectAttrs
						.addFlashAttribute("mensaje", "Carpeta y Registros Creados Correctamente")
						.addFlashAttribute("clase", "success");
				return "redirect:/home/" + id_carpeta_anterior;

			}
			if (data == null) {
				String mensaje = (String) responseBody.get("mensaje");
				System.out.println("Mensaje de la API: " + mensaje);
				redirectAttrs
						.addFlashAttribute("mensaje", mensaje)
						.addFlashAttribute("clase", "danger");
				return "redirect:/home/" + id_carpeta_anterior;
			}

			// System.out.println(responseBody);
		} else {
			System.out.println("Error en la solicitud. Código de respuesta: " + resp.getStatusCodeValue());

		}

		return "redirect:/home/" + id_carpeta_anterior;
	}

	public Long obtenerIdCarpetaPadreRecursivo(Carpeta carpetaActual) {
		// Verificar si la carpeta actual tiene una carpeta padre
		if (carpetaActual.getCarpetaPadre() != null) {
			// Obtener la carpeta padre de forma recursiva
			return obtenerIdCarpetaPadreRecursivo(carpetaActual.getCarpetaPadre());
		} else {
			// La carpeta actual no tiene una carpeta padre, devuelve su propio id
			return carpetaActual.getId_carpeta();
		}
	}

	// CARGAR SELECT ARCHIVO MATERIA
	@PostMapping("/CargarRequisito/{id_materia}")
	public ResponseEntity<String[][]> CargarRequisito(@PathVariable(value = "id_materia") Long id_materia) {
		// List<Carpeta> carpetas =
		// carpetaService.findOne(id_carpeta).getCarpetasHijos();
		List<Requisito> requisitos = new ArrayList<>(materiaService.findOne(id_materia).getRequisitos());

		String[][] materiaArray = new String[requisitos.size()][2];
		int index = 0;
		for (Requisito requisito : requisitos) {
			materiaArray[index][0] = String.valueOf(requisito.getId_requisito());
			materiaArray[index][1] = requisito.getNombre();
			index++;
		}
		return ResponseEntity.ok(materiaArray);
	}

	@PostMapping("/CargarParametro/{id_requisito}")
	public ResponseEntity<String[][]> CargarParametro(@PathVariable(value = "id_requisito") Long id_requisito) {
		// List<Carpeta> carpetas =
		// carpetaService.findOne(id_carpeta).getCarpetasHijos();
		List<Parametro> parametros = requisitoService.findOne(id_requisito).getParametros();
		String[][] materiaArray = new String[parametros.size()][2];
		int index = 0;
		for (Parametro parametro : parametros) {
			materiaArray[index][0] = String.valueOf(parametro.getId_parametro());
			materiaArray[index][1] = parametro.getNombre();
			index++;
		}
		return ResponseEntity.ok(materiaArray);
	}

	// @PostMapping("/cargarCarpetasDisponibles/{id_carpeta}/{id_archivo}")
	// public String cargarCarpetasDisponibles(@PathVariable(value = "id_carpeta")
	// Long id_carpeta,
	// @PathVariable(value = "id_archivo") Long id_archivo, Model model,
	// HttpServletRequest request) {
	// Persona p2 = (Persona) request.getSession().getAttribute("persona");
	// Persona persona = personaService.findOne(p2.getId_persona());
	// Usuario usuario = usuarioService.usuarioPorIdPersona(p2.getId_persona());
	// model.addAttribute("archivo", id_archivo);
	// if (persona.getTipoPersona().getNom_tipo_persona().equals("Administrador")) {
	// model.addAttribute("carpetas",
	// carpetaService.listaDireccionesParaMoverArchivoAdmin(id_carpeta));
	// } else {
	// model.addAttribute("carpetas",
	// carpetaService.listaDireccionesParaMoverArchivo(id_carpeta,
	// usuario.getId_usuario()));
	// }

	// return "Carpeta/carpetasDisponibles";
	// }

	@PostMapping("/cargarCarpetasDisponibles/{id_carpeta}")
	public String cargarCarpetasDisponibles(@PathVariable(value = "id_carpeta") Long id_carpeta,
			Model model, HttpServletRequest request) {
		Persona p2 = (Persona) request.getSession().getAttribute("persona");
		Persona persona = personaService.findOne(p2.getId_persona());
		Usuario usuario = usuarioService.usuarioPorIdPersona(p2.getId_persona());
		if (persona.getTipoPersona().getNom_tipo_persona().equals("Administrador")) {
			model.addAttribute("carpetas", carpetaService.listaDireccionesParaMoverArchivoAdmin(id_carpeta));
		} else {
			model.addAttribute("carpetas",
					carpetaService.listaDireccionesParaMoverArchivo(id_carpeta, usuario.getId_usuario()));
		}

		return "Carpeta/carpetasDisponibles";
	}
}
