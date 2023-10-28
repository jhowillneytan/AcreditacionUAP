package com.uap.acreditacion.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.uap.acreditacion.entity.Facultad;
import com.uap.acreditacion.entity.Persona;
import com.uap.acreditacion.service.IFacultadService;
import com.uap.acreditacion.service.IPersonaService;
import com.uap.acreditacion.service.ITipoPersonaService;

@Controller
@RequestMapping("/facultad")
public class FacultadController {

    @Autowired
    private IFacultadService facultadService;
    @Autowired
    private ITipoPersonaService tipoPersonaService;

    @Autowired
    private IPersonaService personaService;

    @GetMapping("/formulario")
    public String form(Model model, @RequestParam(name = "success", required = false) String success,
            HttpServletRequest request) {
        if (request.getSession().getAttribute("persona") != null) {

            Persona p2 = (Persona) request.getSession().getAttribute("persona");
            Persona p = personaService.findOne(p2.getId_persona());
            model.addAttribute("personasession", p);
            model.addAttribute("tipoPersonasession",
                    tipoPersonaService.findOne(p.getTipoPersona().getId_tipo_persona()));
            model.addAttribute("facultad", new Facultad());
            model.addAttribute("facultads", facultadService.findAll());

            if (success != null) {
                model.addAttribute("success", success);
            }
            model.addAttribute("opcionFacultad", "true");
            model.addAttribute("subMenuSeleccionado", "true");
            return "Facultad/formulario";
        } else {
            return "redirect:/login";
        }
    }

    @GetMapping("/editar-facultad/{id}")
    public String editar(@PathVariable("id") Long id, Model model,
            @RequestParam(name = "success", required = false) String success, HttpServletRequest request) {
        if (request.getSession().getAttribute("persona") != null) {
            Persona p2 = (Persona) request.getSession().getAttribute("persona");
            Persona p = personaService.findOne(p2.getId_persona());
            model.addAttribute("personasession", p);
            model.addAttribute("tipoPersonasession",
                    tipoPersonaService.findOne(p.getTipoPersona().getId_tipo_persona()));
            model.addAttribute("facultad", facultadService.findOne(id));
            model.addAttribute("facultads", facultadService.findAll());

            if (success != null) {
                model.addAttribute("success", success);
            }
            model.addAttribute("opcionFacultad", "true");
            model.addAttribute("subMenuSeleccionado", "true");
            return "Facultad/formulario";
        } else {
            return "redirect:/login";
        }
    }

    @GetMapping("/eliminar-facultad/{id}")
    public String eliminar(@PathVariable("id") Long id, RedirectAttributes flash) {

        Facultad facultad = facultadService.findOne(id);
        facultad.setEstado("X");
        facultadService.save(facultad);

        flash.addAttribute("success", "Facultad Eliminado con Exito!");

        return "redirect:/facultad/formulario";
    }

    @PostMapping("/formulario")
    public String guardar(@Validated Facultad facultad, RedirectAttributes flash) {

        facultad.setEstado("A");
        facultadService.save(facultad);

        flash.addAttribute("success", "Registro Exitoso!");

        return "redirect:/facultad/formulario";
    }

    @GetMapping("/cargarFacultades")
    public String cargarFacultades(HttpServletRequest request) {
        Map<String, Object> requests = new HashMap<String, Object>();

        String url = "http://181.115.188.250:9993/v1/service/api/89b5b361047f40edb5d75dce872e8bf1";
        String key = "key 46bc2f9cface91d161e6bf4f6e27c1aeb67d40d157b082d7a7135a677f5df1fb";

        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-api-key", key);

        HttpEntity<HashMap> req = new HttpEntity(requests, headers);

        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<Map> resp = restTemplate.exchange(url, HttpMethod.GET, req, Map.class);

        if (resp.getStatusCode() == HttpStatus.OK) {
            Map<String, Object> responseMap = resp.getBody();

            if (responseMap != null) {
                List<Map<String, String>> dataList = (List<Map<String, String>>) responseMap.get("data");

                if (dataList != null && !dataList.isEmpty()) {
                    List<String> facultades = new ArrayList<>();
                    for (Map<String, String> carreraData : dataList) {
                        String facultad = carreraData.get("facultad");
                        facultades.add(facultad);
                    }
                    // Utiliza un HashSet para eliminar duplicados
                    Set<String> facultadesSinDuplicados = new HashSet<>(facultades);

                    // Convierte el HashSet nuevamente en una lista
                    facultades.clear(); // Limpia la lista original
                    facultades.addAll(facultadesSinDuplicados);
                    // Ahora tienes la lista de facultades
                    for (String facultad : facultades) {
                        Facultad facultad2 = new Facultad();
                        facultad2.setNom_facultad(facultad);
                        facultad2.setFecha_registro(new Date());
                        facultad2.setDescripcion(facultad);
                        facultad2.setEstado("A");
                        facultadService.save(facultad2);
                        System.out.println("Facultad: " + facultad);
                    }
                } else {
                    System.out.println("No se encontraron datos de facultades.");
                }
            } else {
                System.out.println("La respuesta del API es nula.");
            }
        } else {
            System.out.println("La solicitud al API no fue exitosa (código de estado: " + resp.getStatusCode() + ")");
        }

        System.out.println("SE CARGO LAS MATERIAS ");

        return "redirect:/facultad/formulario";
    }

}
