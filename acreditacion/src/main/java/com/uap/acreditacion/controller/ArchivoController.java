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
import javax.servlet.http.HttpServletRequest;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.tomcat.jni.OS;
import org.dom4j.DocumentException;
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
import org.springframework.web.multipart.commons.CommonsMultipartFile;
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

import java.awt.image.BufferedImage;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;

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
public class ArchivoController {

	Config config = new Config();

	@Autowired
	private ICarpetaService carpetaService;

	@Autowired
	private IArchivoService archivoService;

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

	@PostMapping("/guardar-archivo")
	public String guardarArchivo(@Validated Archivo archivo, RedirectAttributes redirectAttrs,
			@RequestParam(name = "archivo", required = false) List<MultipartFile> file,
			@RequestParam(value = "auxiliar") Long id_carpeta_anterior,
			@RequestParam(value = "idParametro", required = false) Long id_parametro,
			@RequestParam(value = "idMateria", required = false) Long id_materia,
			@RequestParam(value = "idRequisito", required = false) Long id_requisito) throws IOException {

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
				// System.out.println("id recibido parametro: "+id_parametro);
				if (parametroService.findOne(id_parametro) != null) {
					Parametro parametro = parametroService.findOne(id_parametro);
					Set<Parametro> parametros = new HashSet<>();
					parametros.add(parametro);
					archivo2.setParametros(parametros);
					System.out.println("parametros " + parametro.getNombre());
				}
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

	// @GetMapping("/eliminar-archivo/{id_archivo}")
	// public String eliminarArchivo(@PathVariable(value = "id_archivo") Long
	// id_archivo,
	// RedirectAttributes redirectAttrs) {
	// Archivo archivo = archivoService.findOne(id_archivo);
	// archivo.setEstado("X");
	// archivoService.save(archivo);
	// Carpeta carpeta =
	// carpetaService.findOne(archivo.getCarpeta().getId_carpeta());
	// redirectAttrs
	// .addFlashAttribute("mensaje", "Archivo Eliminado correctamente")
	// .addFlashAttribute("clase", "danger");

	// return "redirect:/home/" + carpeta.getId_carpeta();
	// }

	@PostMapping("/eliminar-archivos")
	public String eliminarArchivos(@RequestParam(value = "archivos") List<Long> idArchivos,
			@RequestParam(value = "idCarpeta") Long idCarpeta,
			RedirectAttributes redirectAttrs) {
		for (Long id_archivo : idArchivos) {
			Archivo archivo = archivoService.findOne(id_archivo);
			archivo.setEstado("X");
			archivoService.save(archivo);
		}
		redirectAttrs
				.addFlashAttribute("mensaje", "Archivo Eliminado correctamente")
				.addFlashAttribute("clase", "danger");

		return "redirect:/home/" + idCarpeta;
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
		if (request.getSession().getAttribute("persona") != null) {
			Persona p2 = (Persona) request.getSession().getAttribute("persona");
			Persona p = personaService.findOne(p2.getId_persona());
			TipoPersona tipoPersona = tipoPersonaService.findOne(p.getTipoPersona().getId_tipo_persona());
			model.addAttribute("tipoPersonasession", tipoPersona);

			if (p.getTipoPersona().getNom_tipo_persona().equals("Personal")) {
				model.addAttribute("archivos", archivoService.listaArchivosPorDescripcion(descripcion.toUpperCase(),
						p.getUsuario().getId_usuario()));
			}
			if (p.getTipoPersona().getNom_tipo_persona().equals("Administrador")) {
				model.addAttribute("archivos",
						archivoService.listaArchivosPorDescripcionAdmin(descripcion.toUpperCase()));
			}

			return "content :: content1";
		} else {
			return "redirect:/";
		}

	}

	@PostMapping("/mover-archivos")
	public String moverArchivos(@RequestParam(value = "carpeta") Long id_carpeta,
			@RequestParam(value = "archivos") List<Long> idArchivos,
			RedirectAttributes redirectAttrs) {
		Carpeta carpeta = carpetaService.findOne(id_carpeta);
		for (Long id : idArchivos) {
			Archivo archivo = archivoService.findOne(id);
			archivo.setCarpeta(carpeta);
			archivoService.save(archivo);
		}
		redirectAttrs
				.addFlashAttribute("mensaje", "Se movio el archivo correctamente.")
				.addFlashAttribute("clase", "success");

		return "redirect:/home/" + id_carpeta;
	}
}
