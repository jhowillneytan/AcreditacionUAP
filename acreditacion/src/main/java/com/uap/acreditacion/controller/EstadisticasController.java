package com.uap.acreditacion.controller;

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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.uap.acreditacion.Config;
import com.uap.acreditacion.entity.Persona;
import com.uap.acreditacion.entity.Usuario;
import com.uap.acreditacion.entity.dto.EstudianteDto;
import com.uap.acreditacion.entity.dto.TituladosDto;
import com.uap.acreditacion.service.IPersonaService;
import com.uap.acreditacion.service.ITipoPersonaService;
import com.uap.acreditacion.service.IUsuarioService;
import com.uap.acreditacion.service.JarperService;

import jakarta.servlet.http.HttpServletRequest;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@Controller
@RequestMapping(value = "/estadisticas")
public class EstadisticasController {

	Config config = new Config();

	@Autowired
	private ITipoPersonaService tipoPersonaService;

	@Autowired
	private IPersonaService personaService;

	@Autowired
	private IUsuarioService usuarioService;

	@Autowired
	private JarperService jarperService;

	@GetMapping("/inicio")
	public String inicio(Model model, HttpServletRequest request) {

		String periodo = periodoActual(LocalDate.now().getMonthValue());
		String gestion = String.valueOf(LocalDate.now().getYear());

		if (request.getSession().getAttribute("persona") != null) {

			// PARA UNICIALIZAR LA VENTANA
			Persona p2 = (Persona) request.getSession().getAttribute("persona");
			Persona p = personaService.findOne(p2.getId_persona());
			model.addAttribute("personasession", p);
			model.addAttribute("tipoPersonasession",
					tipoPersonaService.findOne(p.getTipoPersona().getId_tipo_persona()));
			model.addAttribute("opcionEstadistica", "true");

			model.addAttribute("periodoActual", periodo);

			return "/estadistica/estadisticas";
		} else {
			return "redirect:/login";
		}
	}

	@PostMapping("/matriculados/{gestion1}/{gestion2}/{periodo}")
	public ResponseEntity<List<String[]>> matriculados(@PathVariable("gestion1") int gestion1,
			@PathVariable("gestion2") int gestion2,
			@PathVariable("periodo") int per) {

		List<String[]> matriculadosGestion = new ArrayList<>();

		String url = "http://190.129.216.246:9993/v1/service/api/603cfb26571749a5aceb05506ee9a786";
		String key = "key 46bc2f9cface91d161e6bf4f6e27c1aeb67d40d157b082d7a7135a677f5df1fb";

		for (int i = gestion1; i <= gestion2; i++) {
			Map<String, Object> requests = new HashMap<String, Object>();
			// EL idCarrera 8 es la carrera de ing. industrial
			requests.put("idCarrera", "8");
			requests.put("periodo", per);
			requests.put("gestion", i);

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

					String cantidadMatriculados = String.valueOf((Integer) data.get("matriculados"));
					String[] matriculados = { cantidadMatriculados, String.valueOf(i) };
					matriculadosGestion.add(matriculados);

				}

			} else {
				System.out.println("Error en la solicitud. Código de respuesta: " + resp.getStatusCodeValue());
			}
		}

		return ResponseEntity.ok(matriculadosGestion);
	}

	@PostMapping("/detallesMatriculados/{gestion}/{periodo}")
	public String detallesMatriculados(Model model,
			@PathVariable("gestion") int gestion, @PathVariable("periodo") int periodo) {

		List<EstudianteDto> estudiantes = listaEstudianteDtos(periodo, gestion, "8");

		model.addAttribute("listaEstudiante", estudiantes);

		return "estadistica/detallesMatriculados";
	}

	@PostMapping("/titulados/{gestion1}/{gestion2}/{periodo}")
	public ResponseEntity<List<String[]>> titulados(@PathVariable("gestion1") int gestion1,
			@PathVariable("gestion2") int gestion2,
			@PathVariable("periodo") int per) {

		List<String[]> tituladosGestion = new ArrayList<>();

		String url = "http://190.129.216.246:9993/v1/service/api/603cfb26571749a5aceb05506ee9a786";
		String key = "key 46bc2f9cface91d161e6bf4f6e27c1aeb67d40d157b082d7a7135a677f5df1fb";

		for (int i = gestion1; i <= gestion2; i++) {
			Map<String, Object> requests = new HashMap<String, Object>();
			// EL idCarrera 8 es la carrera de ing. industrial
			requests.put("idCarrera", "8");
			requests.put("periodo", per);
			requests.put("gestion", i);

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

					List<Map<String, String>> tituladoTipoData = (List<Map<String, String>>) data
							.get("titulados");

					String cantidadTitulados = String.valueOf((Integer) tituladoTipoData.size());
					String[] titulados = { cantidadTitulados, String.valueOf(i) };
					tituladosGestion.add(titulados);
				}

			} else {
				System.out.println("Error en la solicitud. Código de respuesta: " + resp.getStatusCodeValue());
			}
		}

		return ResponseEntity.ok(tituladosGestion);
	}

	@PostMapping("/detallesTitulados/{gestion}/{periodo}")
	public String detallesTitulados(Model model,
			@PathVariable("gestion") int gestion, @PathVariable("periodo") int periodo) {

		List<EstudianteDto> estudiantes = new ArrayList<>();

		String url = "http://190.129.216.246:9993/v1/service/api/603cfb26571749a5aceb05506ee9a786";
		String key = "key 46bc2f9cface91d161e6bf4f6e27c1aeb67d40d157b082d7a7135a677f5df1fb";

		Map<String, Object> requests = new HashMap<String, Object>();
		// EL idCarrera 8 es la carrera de ing. industrial
		requests.put("idCarrera", "8");
		requests.put("periodo", periodo);
		requests.put("gestion", gestion);

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

				List<Map<String, String>> estudiantesData = (List<Map<String, String>>) data
						.get("titulados");
				for (Map<String, String> estudianteData : estudiantesData) {
					EstudianteDto estudiante = new EstudianteDto();
					estudiante.setRu(String.valueOf(estudianteData.get("ru")));
					estudiante.setCi(estudianteData.get("ci"));
					estudiante.setNombreCompleto(estudianteData.get("nombres") + " " + estudianteData.get("paterno")
							+ " " + estudianteData.get("materno"));
					estudiante.setTipoAdminision(estudianteData.get("tipo_admision"));
					estudiante.setModalidad(estudianteData.get("modalidad"));
					estudiantes.add(estudiante);
				}
			}

		} else {
			System.out.println("Error en la solicitud. Código de respuesta: " + resp.getStatusCodeValue());
		}
		model.addAttribute("listaEstudiante", estudiantes);

		return "estadistica/detallesTitulados";
	}

	@PostMapping("/tituladosPorModalidad/{gestion}/{periodo}")
	public ResponseEntity<List<String[]>> tituladosPorModalidad(@PathVariable("gestion") int gestion,
			@PathVariable("periodo") int per) {

		List<String[]> tituladosGestion = new ArrayList<>();

		String url = "http://190.129.216.246:9993/v1/service/api/603cfb26571749a5aceb05506ee9a786";
		String key = "key 46bc2f9cface91d161e6bf4f6e27c1aeb67d40d157b082d7a7135a677f5df1fb";

		Map<String, Object> requests = new HashMap<String, Object>();
		// EL idCarrera 8 es la carrera de ing. industrial
		requests.put("idCarrera", "8");
		requests.put("periodo", per);
		requests.put("gestion", gestion);

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

				List<Map<String, String>> tituladoTipoData = (List<Map<String, String>>) data
						.get("listaTituladosPorTipo");

				for (Map<String, String> estudianteData : tituladoTipoData) {

					Object totalObj = estudianteData.get("total");
					Integer cantidadTitulados = null;

					// Verificar si totalObj es un número
					if (totalObj instanceof Number) {
						cantidadTitulados = ((Number) totalObj).intValue();
					} else {
						// Manejo de error: asignar un valor por defecto si no es un número
						cantidadTitulados = 0; // O lo que consideres apropiado
					}
					String[] titulados = { String.valueOf(cantidadTitulados), estudianteData.get("modalidad") };
					tituladosGestion.add(titulados);
				}
			}

		} else {
			System.out.println("Error en la solicitud. Código de respuesta: " + resp.getStatusCodeValue());
		}

		return ResponseEntity.ok(tituladosGestion);
	}

	@PostMapping("/estudiantesProgramados/{gestion}/{periodo}")
	public ResponseEntity<List<String[]>> estudiantesProgramados(@PathVariable("gestion") int gestion,
			@PathVariable("periodo") int per) {

		List<String[]> tituladosGestion = new ArrayList<>();

		String url = "http://190.129.216.246:9993/v1/service/api/603cfb26571749a5aceb05506ee9a786";
		String key = "key 46bc2f9cface91d161e6bf4f6e27c1aeb67d40d157b082d7a7135a677f5df1fb";

		Map<String, Object> requests = new HashMap<String, Object>();
		// EL idCarrera 8 es la carrera de ing. industrial
		requests.put("idCarrera", "8");
		requests.put("periodo", per);
		requests.put("gestion", gestion);

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

				List<Map<String, String>> tituladoTipoData = (List<Map<String, String>>) data
						.get("titulados");

				Integer cantidadNoPogragramdos = (Integer) data.get("matriculados") - (Integer) data.get("programados");
				String cantidadNoProgramados = String.valueOf(cantidadNoPogragramdos);
				String[] noProgramados = { cantidadNoProgramados, "No programados" };
				tituladosGestion.add(noProgramados);

				String cantidadProgramados = String.valueOf((Integer) data.get("programados"));
				String[] programados = { cantidadProgramados, "Programados" };
				tituladosGestion.add(programados);
			}

		} else {
			System.out.println("Error en la solicitud. Código de respuesta: " + resp.getStatusCodeValue());
		}

		return ResponseEntity.ok(tituladosGestion);
	}

	String periodoActual(int nroMes) {
		if (nroMes <= 7) {
			return "1";
		} else {
			return "2";
		}
	}

	@GetMapping("/generarListaMatriculadosApi/{gestion}/{periodo}")
	public ResponseEntity<ByteArrayResource> GenerarReportePdfSolicitudesBecasFacultad(Model model,
			@PathVariable("gestion") int gestion, @PathVariable("periodo") int periodo,
			HttpServletRequest request)
			throws SQLException {

		// Usuario usuarioSession = (Usuario) request.getSession().getAttribute("usuario");
		// Usuario userLog = usuarioService.findOne(usuarioSession.getId_usuario());

		// Path jasperPath = Paths.get("acreditacion", "reportes", "reporteListaMatriculadosApi.jrxml");
		// String rutaJasper = jasperPath.toString();

		List<EstudianteDto> estudiantes = listaEstudianteDtos(periodo, gestion, "8");

		Map<String, Object> parametros = new HashMap<>();
		parametros.put("gestion", gestion);
		parametros.put("periodo", periodo);

		ByteArrayOutputStream stream;
		try {
			// Usar JRBeanCollectionDataSource para la lista
			JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(estudiantes);

			// Compilar y llenar el reporte
			stream = jarperService.compilarAndExportarReporteExcel("reporteListaMatriculadosApi.jrxml", parametros, dataSource);
			byte[] bytes = stream.toByteArray();
			ByteArrayResource resource = new ByteArrayResource(bytes);

			HttpHeaders headersRespuesta = new HttpHeaders();
			headersRespuesta.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=lista_matriculados.xlsx");

			return ResponseEntity.ok()
					.headers(headersRespuesta)
					.contentType(MediaType
							.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
					.body(resource);
		} catch (IOException | JRException e) {
			System.out.println("ERROR: " + e.getMessage());
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	private List<EstudianteDto> listaEstudianteDtos(int periodo, int gestion, String idCarrera) {

		List<EstudianteDto> estudiantes = new ArrayList<>();

		String url = "http://190.129.216.246:9993/v1/service/api/603cfb26571749a5aceb05506ee9a786";
		String key = "key 46bc2f9cface91d161e6bf4f6e27c1aeb67d40d157b082d7a7135a677f5df1fb";

		Map<String, Object> requests = new HashMap<String, Object>();
		// EL idCarrera 8 es la carrera de ing. industrial
		requests.put("idCarrera", idCarrera);
		requests.put("periodo", periodo);
		requests.put("gestion", gestion);

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

				List<Map<String, String>> estudiantesData = (List<Map<String, String>>) data
						.get("estudiantes");
				for (Map<String, String> estudianteData : estudiantesData) {
					EstudianteDto estudiante = new EstudianteDto();
					estudiante.setRu(String.valueOf(estudianteData.get("ru")));
					estudiante.setCi(estudianteData.get("ci"));
					estudiante.setNombreCompleto(estudianteData.get("nombres") + " " + estudianteData.get("paterno")
							+ " " + estudianteData.get("materno"));
					estudiante.setTipoAdminision(estudianteData.get("tipo_admision"));
					estudiantes.add(estudiante);
				}
			}

		} else {
			System.out.println("Error en la solicitud. Código de respuesta: " + resp.getStatusCodeValue());
		}

		return estudiantes;

	}

}
