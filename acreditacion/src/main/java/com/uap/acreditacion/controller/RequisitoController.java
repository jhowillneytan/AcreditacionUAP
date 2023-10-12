package com.uap.acreditacion.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.uap.acreditacion.entity.Parametro;
import com.uap.acreditacion.entity.Persona;
import com.uap.acreditacion.entity.Requisito;
import com.uap.acreditacion.service.IParametroService;
import com.uap.acreditacion.service.IPersonaService;
import com.uap.acreditacion.service.IRequisitoService;
import com.uap.acreditacion.service.ITipoPersonaService;

@Controller
public class RequisitoController {

@Autowired
    private ITipoPersonaService tipoPersonaService;

    @Autowired
    private IPersonaService personaService;

    @Autowired
    private IRequisitoService requisitoService;

    @Autowired
    private IParametroService parametroService;

    @GetMapping("/form-requisito")
    public String formCargo(ModelMap model, HttpServletRequest request) {
        if (request.getSession().getAttribute("persona") != null) {
            Persona p2 = (Persona) request.getSession().getAttribute("persona");
            Persona p = personaService.findOne(p2.getId_persona());
            model.addAttribute("personasession", p);
            model.addAttribute("tipoPersonasession",
                    tipoPersonaService.findOne(p.getTipoPersona().getId_tipo_persona()));
            List<Requisito> listpRequisitos = requisitoService.findAll();
            model.addAttribute("requisito", new Requisito());
            model.addAttribute("requisitos", listpRequisitos);
            model.addAttribute("parametros", parametroService.findAll());
            model.addAttribute("opcionRequisito", "true");
            model.addAttribute("subMenuSeleccionado", "true");
            return "/Requisito/formulario";
        } else {
            return "redirect:/login";
        }
    }

    @PostMapping("/RegistrarRequisito")
    public String agregarParametro(RedirectAttributes redirectAttrs, @Validated Requisito requisito,
        @RequestParam(value = "ListParametro")List<Parametro> listParametros) {
        requisito.setParametros(listParametros);
        requisito.setEstado("A");
        requisitoService.save(requisito);
        redirectAttrs
                .addFlashAttribute("mensaje", "Agregado correctamente")
                .addFlashAttribute("clase", "success");
        return "redirect:/form-requisito";
    }
}
