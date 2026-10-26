package com.brasasdoradas.sistema.service;

import com.brasasdoradas.sistema.dto.IngresoDiarioDTO;
import com.brasasdoradas.sistema.dto.ProductoVendidoDTO;
import com.brasasdoradas.sistema.repository.DetallePedidoRepository;
import com.brasasdoradas.sistema.repository.PedidoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Servicio del modulo de Reportes (Capitulo IV del informe QA):
 * consolida las metricas de ingresos diarios y rotacion de platos
 * a partir de los pedidos marcados como PAGADO.
 */
@Service
@RequiredArgsConstructor
public class ReporteService {

    private final PedidoRepository pedidoRepository;
    private final DetallePedidoRepository detallePedidoRepository;

    public List<IngresoDiarioDTO> obtenerIngresosPorDia() {
        return pedidoRepository.obtenerIngresosPorDia().stream()
                .map(fila -> new IngresoDiarioDTO(
                        convertirAFecha(fila[0]),
                        (BigDecimal) fila[1],
                        ((Number) fila[2]).longValue()
                ))
                .toList();
    }

    public List<ProductoVendidoDTO> obtenerProductosMasVendidos() {
        return detallePedidoRepository.obtenerProductosMasVendidos().stream()
                .map(fila -> new ProductoVendidoDTO(
                        (String) fila[0],
                        ((Number) fila[1]).longValue(),
                        (BigDecimal) fila[2]
                ))
                .toList();
    }

    public ResumenGeneral obtenerResumenGeneral() {
        List<Object[]> filas = pedidoRepository.obtenerResumenGeneral();
        Object[] fila = filas.get(0);
        long totalPedidos = ((Number) fila[0]).longValue();
        BigDecimal totalFacturado = (BigDecimal) fila[1];
        BigDecimal ticketPromedio = (BigDecimal) fila[2];
        return new ResumenGeneral(totalPedidos, totalFacturado, ticketPromedio);
    }

    private LocalDate convertirAFecha(Object valor) {
        if (valor instanceof java.sql.Date sqlDate) {
            return sqlDate.toLocalDate();
        }
        if (valor instanceof LocalDate localDate) {
            return localDate;
        }
        return LocalDate.parse(valor.toString());
    }

    public record ResumenGeneral(long totalPedidos, BigDecimal totalFacturado, BigDecimal ticketPromedio) {
    }
}