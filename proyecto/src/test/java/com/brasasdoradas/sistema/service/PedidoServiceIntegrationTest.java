package com.brasasdoradas.sistema.service;

import com.brasasdoradas.sistema.entity.*;
import com.brasasdoradas.sistema.repository.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Prueba de INTEGRACION real usando @SpringBootTest.
 * A diferencia de PedidoServiceTest (que usa mocks con Mockito),
 * esta prueba levanta el contexto completo de Spring y se conecta
 * a la base de datos MySQL real, verificando la interoperabilidad
 * entre las capas de Servicio, Repositorio y Persistencia (JPA),
 * tal como se describe en el Capitulo III del informe.
 *
 * @Transactional hace que todos los cambios se reviertan (rollback)
 * automaticamente al terminar cada prueba, para no dejar basura
 * en la base de datos real.
 */
@SpringBootTest
@Transactional
@DirtiesContext
class PedidoServiceIntegrationTest {

    @Autowired
    private PedidoService pedidoService;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private MesaRepository mesaRepository;

    @Autowired
    private EmpleadoRepository empleadoRepository;

    @Autowired
    private PedidoRepository pedidoRepository;

    @Test
    void registrarPedido_debePersistirEnBaseDeDatosRealYDescontarStock() {
        // Arrange: se usan datos reales ya sembrados en MySQL (seed del script SQL)
        Producto producto = productoRepository.findById(1)
                .orElseThrow(() -> new IllegalStateException("Falta el producto seed con id=1"));
        Mesa mesa = mesaRepository.findById(1)
                .orElseThrow(() -> new IllegalStateException("Falta la mesa seed con id=1"));
        Empleado empleado = empleadoRepository.findByUsuario("mozo1")
                .orElseThrow(() -> new IllegalStateException("Falta el empleado seed 'mozo1'"));

        int stockAntes = producto.getStock();

        DetallePedido detalle = DetallePedido.builder()
                .producto(producto)
                .cantidad(2)
                .precioUnitario(producto.getPrecio())
                .build();

        List<DetallePedido> detalles = new ArrayList<>();
        detalles.add(detalle);

        Pedido pedido = Pedido.builder()
                .mesa(mesa)
                .empleado(empleado)
                .detalles(detalles)
                .build();

        // Act: se ejecuta la logica real contra la base de datos real
        Pedido pedidoGuardado = pedidoService.registrarPedido(pedido);

        // Assert: se verifica que quedo persistido correctamente
        assertNotNull(pedidoGuardado.getIdPedido());
        assertEquals(Pedido.EstadoPedido.PENDIENTE, pedidoGuardado.getEstado());

        BigDecimal totalEsperado = producto.getPrecio()
                .multiply(BigDecimal.valueOf(2))
                .setScale(2, java.math.RoundingMode.HALF_UP);
        assertEquals(totalEsperado, pedidoGuardado.getTotal());

        // Verifica que el pedido realmente se guardo en la tabla pedidos
        Pedido pedidoDesdeBD = pedidoRepository.findById(pedidoGuardado.getIdPedido()).orElseThrow();
        assertEquals(mesa.getIdMesa(), pedidoDesdeBD.getMesa().getIdMesa());

        // Verifica que el stock se descuento realmente en la tabla productos
        Producto productoActualizado = productoRepository.findById(1).orElseThrow();
        assertEquals(stockAntes - 2, productoActualizado.getStock());
    }
}