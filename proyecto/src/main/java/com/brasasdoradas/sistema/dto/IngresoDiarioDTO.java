package com.brasasdoradas.sistema.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Representa el total de ingresos (pedidos pagados) agrupados por dia,
 * para el modulo de Reportes descrito en el Capitulo IV del informe.
 */
public record IngresoDiarioDTO(LocalDate fecha, BigDecimal totalIngresos, Long cantidadPedidos) {
}
