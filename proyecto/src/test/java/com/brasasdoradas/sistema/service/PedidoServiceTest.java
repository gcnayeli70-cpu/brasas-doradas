package com.brasasdoradas.sistema.service;

import com.brasasdoradas.sistema.entity.*;
import com.brasasdoradas.sistema.repository.PedidoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias del servicio de Pedidos.
 * Corresponde al caso BUG-001 documentado en el informe de QA:
 * error critico en el desglose decimal del IGV (18%).
 *
 * Se usa Mockito para aislar PedidoRepository y ProductoService,
 * validando unicamente la logica aritmetica del servicio.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("PedidoService - Calculo de IGV y totales")
class PedidoServiceTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private ProductoService productoService;

    @InjectMocks
    private PedidoService pedidoService;

    private Categoria categoria;
    private Producto producto;

    @BeforeEach
    void setUp() {
        categoria = Categoria.builder().idCategoria(1).nombreCategoria("Pollos a la Brasa").build();
        producto = Producto.builder()
                .idProducto(1)
                .nombreProducto("1/4 de Pollo con Papas y Ensalada")
                .precio(new BigDecimal("18.00"))
                .categoria(categoria)
                .stock(50)
                .estado(Producto.EstadoProducto.ACTIVO)
                .build();
    }

    @Test
    @DisplayName("Debe calcular correctamente subtotal, IGV y total para un pedido simple")
    void calcularTotales_pedidoSimple_calculaIgvCorrectamente() {
        // Arrange: 1 unidad de S/ 18.00 (precio incluye IGV)
        DetallePedido detalle = DetallePedido.builder()
                .producto(producto)
                .cantidad(1)
                .precioUnitario(new BigDecimal("18.00"))
                .subtotalLinea(new BigDecimal("18.00"))
                .build();

        List<DetallePedido> detalles = new ArrayList<>();
        detalles.add(detalle);

        Pedido pedido = Pedido.builder().detalles(detalles).build();

        // Act
        pedidoService.calcularTotales(pedido);

        // Assert: total = 18.00, subtotal = 18.00 / 1.18 = 15.25, igv = 2.75
        assertEquals(new BigDecimal("18.00"), pedido.getTotal());
        assertEquals(new BigDecimal("15.25"), pedido.getSubtotal());
        assertEquals(new BigDecimal("2.75"), pedido.getIgv());

        // La suma de subtotal + igv debe reconstruir exactamente el total (regresion de BUG-001)
        assertEquals(pedido.getTotal(), pedido.getSubtotal().add(pedido.getIgv()));
    }

    @Test
    @DisplayName("Debe sumar correctamente varias lineas de detalle antes de calcular el IGV")
    void calcularTotales_variasLineas_sumaCorrectamente() {
        DetallePedido linea1 = DetallePedido.builder()
                .producto(producto).cantidad(2).precioUnitario(new BigDecimal("18.00"))
                .subtotalLinea(new BigDecimal("36.00")).build();

        DetallePedido linea2 = DetallePedido.builder()
                .producto(producto).cantidad(1).precioUnitario(new BigDecimal("10.00"))
                .subtotalLinea(new BigDecimal("10.00")).build();

        List<DetallePedido> detalles = new ArrayList<>(List.of(linea1, linea2));
        Pedido pedido = Pedido.builder().detalles(detalles).build();

        pedidoService.calcularTotales(pedido);

        // total esperado = 46.00
        assertEquals(new BigDecimal("46.00"), pedido.getTotal());
        assertEquals(pedido.getTotal(), pedido.getSubtotal().add(pedido.getIgv()));
    }

    @Test
    @DisplayName("Debe calcular correctamente el subtotal de una linea (precio x cantidad)")
    void calcularSubtotalLinea_calculaProductoCantidad() {
        BigDecimal resultado = pedidoService.calcularSubtotalLinea(new BigDecimal("18.00"), 3);
        assertEquals(new BigDecimal("54.00"), resultado);
    }

    @Test
    @DisplayName("Debe lanzar excepcion si la cantidad es menor o igual a cero")
    void calcularSubtotalLinea_cantidadInvalida_lanzaExcepcion() {
        BigDecimal precio = new BigDecimal("18.00");
        assertThrows(IllegalArgumentException.class,
                () -> pedidoService.calcularSubtotalLinea(precio, 0));
    }

    @Test
    @DisplayName("registrarPedido debe descontar stock por cada linea del pedido")
    void registrarPedido_descuentaStockPorLinea() {
        DetallePedido detalle = DetallePedido.builder()
                .producto(producto).cantidad(2).precioUnitario(new BigDecimal("18.00")).build();

        List<DetallePedido> detalles = new ArrayList<>(List.of(detalle));
        Pedido pedido = Pedido.builder().detalles(detalles).build();

        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(inv -> inv.getArgument(0));

        pedidoService.registrarPedido(pedido);

        verify(productoService, times(1)).descontarStock(producto.getIdProducto(), 2);
        verify(pedidoRepository, times(1)).save(pedido);
        assertEquals(Pedido.EstadoPedido.PENDIENTE, pedido.getEstado());
    }
}