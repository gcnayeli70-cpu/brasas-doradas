package com.brasasdoradas.sistema.controller;

import com.brasasdoradas.sistema.service.ReporteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/reportes")
@RequiredArgsConstructor
public class ReporteController {

    private final ReporteService reporteService;

    @GetMapping
    public String verReportes(Model model) {
        model.addAttribute("ingresosPorDia", reporteService.obtenerIngresosPorDia());
        model.addAttribute("productosMasVendidos", reporteService.obtenerProductosMasVendidos());
        model.addAttribute("resumen", reporteService.obtenerResumenGeneral());
        return "reportes/lista";
    }
}
