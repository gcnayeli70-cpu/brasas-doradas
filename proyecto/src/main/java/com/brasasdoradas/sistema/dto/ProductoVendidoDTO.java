package com.brasasdoradas.sistema.dto;

import java.math.BigDecimal;

/**
 * Representa un producto con su total de unidades vendidas e ingresos generados,
 * usado para la rotacion de platos mencionada en el Capitulo IV del informe.
 */
public record ProductoVendidoDTO(String nombreProducto, Long unidadesVendidas, BigDecimal totalGenerado) {
}
