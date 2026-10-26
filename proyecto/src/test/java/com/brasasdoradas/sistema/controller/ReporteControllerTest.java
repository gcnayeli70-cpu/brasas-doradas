package com.brasasdoradas.sistema.controller;

import com.brasasdoradas.sistema.dto.IngresoDiarioDTO;
import com.brasasdoradas.sistema.dto.ProductoVendidoDTO;
import com.brasasdoradas.sistema.service.ReporteService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReporteController.class)
@AutoConfigureMockMvc(addFilters = false)
class ReporteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReporteService reporteService;

    @Test
    void verReportes_debeResponder200YCargarLosTresIndicadores() throws Exception {
        when(reporteService.obtenerIngresosPorDia()).thenReturn(
                List.of(new IngresoDiarioDTO(LocalDate.now(), new BigDecimal("450.00"), 5L)));
        when(reporteService.obtenerProductosMasVendidos()).thenReturn(
                List.of(new ProductoVendidoDTO("Pollo Entero", 30L, new BigDecimal("2340.00"))));
        when(reporteService.obtenerResumenGeneral()).thenReturn(
                new ReporteService.ResumenGeneral(12L, new BigDecimal("1080.00"), new BigDecimal("90.00")));

        mockMvc.perform(get("/reportes"))
                .andExpect(status().isOk())
                .andExpect(view().name("reportes/lista"))
                .andExpect(model().attributeExists("ingresosPorDia", "productosMasVendidos", "resumen"));
    }

    @Test
    void verReportes_sinDatos_debeResponder200ConListasVacias() throws Exception {
        when(reporteService.obtenerIngresosPorDia()).thenReturn(List.of());
        when(reporteService.obtenerProductosMasVendidos()).thenReturn(List.of());
        when(reporteService.obtenerResumenGeneral()).thenReturn(
                new ReporteService.ResumenGeneral(0L, BigDecimal.ZERO, BigDecimal.ZERO));

        mockMvc.perform(get("/reportes"))
                .andExpect(status().isOk())
                .andExpect(view().name("reportes/lista"));
    }
}
