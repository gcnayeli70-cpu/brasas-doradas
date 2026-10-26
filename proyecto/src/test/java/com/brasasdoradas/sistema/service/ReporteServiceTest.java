package com.brasasdoradas.sistema.service;

import com.brasasdoradas.sistema.dto.IngresoDiarioDTO;
import com.brasasdoradas.sistema.dto.ProductoVendidoDTO;
import com.brasasdoradas.sistema.repository.DetallePedidoRepository;
import com.brasasdoradas.sistema.repository.PedidoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * Pruebas del modulo de Reportes (Capitulo IV del informe QA). Este servicio
 * no tenia ninguna prueba en la entrega original a pesar de consolidar las
 * metricas de ingresos y rotacion de platos que el negocio usa para decidir.
 */
@ExtendWith(MockitoExtension.class)
class ReporteServiceTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private DetallePedidoRepository detallePedidoRepository;

    @InjectMocks
    private ReporteService reporteService;

    @Test
    void obtenerIngresosPorDia_debeMapearFilasNativasADTO() {
        Object[] fila = new Object[] { Date.valueOf(LocalDate.of(2026, 8, 1)), new BigDecimal("450.00"), 5L };
        when(pedidoRepository.obtenerIngresosPorDia()).thenReturn(Collections.singletonList(fila));

        List<IngresoDiarioDTO> resultado = reporteService.obtenerIngresosPorDia();

        assertEquals(1, resultado.size());
        assertEquals(LocalDate.of(2026, 8, 1), resultado.get(0).fecha());
        assertEquals(new BigDecimal("450.00"), resultado.get(0).totalIngresos());
        assertEquals(5L, resultado.get(0).cantidadPedidos());
    }

    @Test
    void obtenerProductosMasVendidos_debeMapearFilasNativasADTO() {
        Object[] fila = new Object[] { "Pollo Entero", 30L, new BigDecimal("2340.00") };
        when(detallePedidoRepository.obtenerProductosMasVendidos()).thenReturn(Collections.singletonList(fila));

        List<ProductoVendidoDTO> resultado = reporteService.obtenerProductosMasVendidos();

        assertEquals(1, resultado.size());
        assertEquals("Pollo Entero", resultado.get(0).nombreProducto());
        assertEquals(30L, resultado.get(0).unidadesVendidas());
        assertEquals(new BigDecimal("2340.00"), resultado.get(0).totalGenerado());
    }

    @Test
    void obtenerResumenGeneral_debeMapearFilaNativaAResumen() {
        Object[] fila = new Object[] { 12L, new BigDecimal("1080.00"), new BigDecimal("90.00") };
        when(pedidoRepository.obtenerResumenGeneral()).thenReturn(Collections.singletonList(fila));

        ReporteService.ResumenGeneral resultado = reporteService.obtenerResumenGeneral();

        assertEquals(12L, resultado.totalPedidos());
        assertEquals(new BigDecimal("1080.00"), resultado.totalFacturado());
        assertEquals(new BigDecimal("90.00"), resultado.ticketPromedio());
    }

    @Test
    void obtenerIngresosPorDia_sinPedidosPagados_debeRetornarListaVacia() {
        when(pedidoRepository.obtenerIngresosPorDia()).thenReturn(List.of());

        List<IngresoDiarioDTO> resultado = reporteService.obtenerIngresosPorDia();

        assertTrue(resultado.isEmpty());
    }
}