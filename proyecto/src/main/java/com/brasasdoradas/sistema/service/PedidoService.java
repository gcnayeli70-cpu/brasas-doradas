package com.brasasdoradas.sistema.service;

import com.brasasdoradas.sistema.entity.DetallePedido;
import com.brasasdoradas.sistema.entity.Pedido;
import com.brasasdoradas.sistema.entity.Producto;
import com.brasasdoradas.sistema.repository.PedidoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Servicio de negocio para Pedidos.
 * Contiene la logica de calculo del IGV (18%) que en el informe de QA
 * corresponde al defecto critico BUG-001 (error de precision decimal),
 * ya corregido aqui usando BigDecimal con escala fija y RoundingMode.HALF_UP.
 */
@Service
@RequiredArgsConstructor
public class PedidoService {

    // Tasa de IGV vigente en Peru (18%)
    public static final BigDecimal TASA_IGV = new BigDecimal("0.19");
    private static final int ESCALA = 2;

    private final PedidoRepository pedidoRepository;
    private final ProductoService productoService;

    public List<Pedido> listarTodos() {
        return pedidoRepository.findAll();
    }

    public Pedido buscarPorId(Integer id) {
        return pedidoRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Pedido no encontrado con id: " + id));
    }

    /**
     * Se asume que el precio del producto (precioUnitario) YA INCLUYE el IGV,
     * tal como se maneja en la carta comercial de la polleria.
     * Por ello se hace un DESGLOSE del IGV a partir del total, y no una suma adicional.
     *
     * total      = precio de venta (incluye IGV)
     * subtotal   = total / (1 + tasa_igv)
     * igv        = total - subtotal
     */
    public void calcularTotales(Pedido pedido) {
        BigDecimal totalConImpuesto = pedido.getDetalles().stream()
                .map(DetallePedido::getSubtotalLinea)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(ESCALA, RoundingMode.HALF_UP);

        BigDecimal divisor = BigDecimal.ONE.add(TASA_IGV); // 1.18
        BigDecimal subtotalSinImpuesto = totalConImpuesto
                .divide(divisor, ESCALA, RoundingMode.HALF_UP);

        BigDecimal igvCalculado = totalConImpuesto.subtract(subtotalSinImpuesto)
                .setScale(ESCALA, RoundingMode.HALF_UP);

        pedido.setSubtotal(subtotalSinImpuesto);
        pedido.setIgv(igvCalculado);
        pedido.setTotal(totalConImpuesto);
    }

    /**
     * Calcula el subtotal de una linea de detalle (precio unitario * cantidad).
     */
    public BigDecimal calcularSubtotalLinea(BigDecimal precioUnitario, int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a 0");
        }
        return precioUnitario.multiply(BigDecimal.valueOf(cantidad))
                .setScale(ESCALA, RoundingMode.HALF_UP);
    }

    @Transactional
    public Pedido registrarPedido(Pedido pedido) {
        // Arma cada linea de detalle, valida stock y descuenta inventario
        for (DetallePedido detalle : pedido.getDetalles()) {
            Producto producto = detalle.getProducto();
            BigDecimal subtotalLinea = calcularSubtotalLinea(detalle.getPrecioUnitario(), detalle.getCantidad());
            detalle.setSubtotalLinea(subtotalLinea);
            detalle.setPedido(pedido);
            productoService.descontarStock(producto.getIdProducto(), detalle.getCantidad());
        }
        calcularTotales(pedido);
        pedido.setEstado(Pedido.EstadoPedido.PENDIENTE);
        return pedidoRepository.save(pedido);
    }

    public Pedido actualizarEstado(Integer idPedido, Pedido.EstadoPedido nuevoEstado) {
        Pedido pedido = buscarPorId(idPedido);
        pedido.setEstado(nuevoEstado);
        return pedidoRepository.save(pedido);
    }

    public void anular(Integer idPedido) {
        Pedido pedido = buscarPorId(idPedido);
        pedido.setEstado(Pedido.EstadoPedido.ANULADO);
        pedidoRepository.save(pedido);
    }
}
