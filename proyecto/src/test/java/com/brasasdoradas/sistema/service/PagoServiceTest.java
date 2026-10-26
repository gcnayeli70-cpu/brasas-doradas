package com.brasasdoradas.sistema.service;

import com.brasasdoradas.sistema.entity.Empleado;
import com.brasasdoradas.sistema.entity.Pago;
import com.brasasdoradas.sistema.entity.Pedido;
import com.brasasdoradas.sistema.repository.PagoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Pruebas del flujo de cobro (modulo de Pagos). No estaba cubierto por el
 * informe original a pesar de ser un modulo transaccional critico: valida
 * pedidos ya pagados/anulados y montos insuficientes.
 */
@ExtendWith(MockitoExtension.class)
class PagoServiceTest {

    @Mock
    private PagoRepository pagoRepository;

    @Mock
    private PedidoService pedidoService;

    @InjectMocks
    private PagoService pagoService;

    @Test
    void buscarPorId_pagoExistente_debeRetornarPago() {
        Pago pago = Pago.builder().idPago(1).build();
        when(pagoRepository.findById(1)).thenReturn(Optional.of(pago));

        Pago resultado = pagoService.buscarPorId(1);

        assertEquals(1, resultado.getIdPago());
    }

    @Test
    void buscarPorId_pagoInexistente_debeLanzarExcepcion() {
        when(pagoRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(java.util.NoSuchElementException.class, () -> pagoService.buscarPorId(99));
    }

    @Test
    void listarTodos_debeRetornarListaDePagos() {
        when(pagoRepository.findAll()).thenReturn(List.of(Pago.builder().idPago(1).build()));

        List<Pago> resultado = pagoService.listarTodos();

        assertEquals(1, resultado.size());
    }

    @Test
    void registrarPago_conDatosValidos_debeGuardarPagoYActualizarPedidoAPagado() {
        Pedido pedido = Pedido.builder()
                .idPedido(1)
                .estado(Pedido.EstadoPedido.EN_PROCESO)
                .total(new BigDecimal("90.00"))
                .build();
        Empleado cajero = Empleado.builder().idEmpleado(2).usuario("cajero1").build();

        when(pedidoService.buscarPorId(1)).thenReturn(pedido);
        when(pagoRepository.save(any(Pago.class))).thenAnswer(inv -> inv.getArgument(0));

        Pago resultado = pagoService.registrarPago(1, cajero, Pago.MetodoPago.EFECTIVO, new BigDecimal("90.00"));

        assertEquals(Pago.MetodoPago.EFECTIVO, resultado.getMetodoPago());
        assertEquals(cajero, resultado.getEmpleado());
        verify(pedidoService, times(1)).actualizarEstado(1, Pedido.EstadoPedido.PAGADO);
        verify(pagoRepository, times(1)).save(any(Pago.class));
    }

    @Test
    void registrarPago_conYapePlin_debeGuardarPagoYActualizarPedidoAPagado() {
        Pedido pedido = Pedido.builder()
                .idPedido(2)
                .estado(Pedido.EstadoPedido.EN_PROCESO)
                .total(new BigDecimal("45.50"))
                .build();
        Empleado cajero = Empleado.builder().idEmpleado(2).usuario("cajero1").build();

        when(pedidoService.buscarPorId(2)).thenReturn(pedido);
        when(pagoRepository.save(any(Pago.class))).thenAnswer(inv -> inv.getArgument(0));

        Pago resultado = pagoService.registrarPago(2, cajero, Pago.MetodoPago.YAPE_PLIN, new BigDecimal("45.50"));

        assertEquals(Pago.MetodoPago.YAPE_PLIN, resultado.getMetodoPago());
        assertEquals(new BigDecimal("45.50"), resultado.getMontoPagado());
        verify(pedidoService, times(1)).actualizarEstado(2, Pedido.EstadoPedido.PAGADO);
    }

    @Test
    void registrarPago_conTarjeta_debeGuardarPagoYActualizarPedidoAPagado() {
        Pedido pedido = Pedido.builder()
                .idPedido(3)
                .estado(Pedido.EstadoPedido.EN_PROCESO)
                .total(new BigDecimal("120.00"))
                .build();
        Empleado cajero = Empleado.builder().idEmpleado(2).usuario("cajero1").build();

        when(pedidoService.buscarPorId(3)).thenReturn(pedido);
        when(pagoRepository.save(any(Pago.class))).thenAnswer(inv -> inv.getArgument(0));

        Pago resultado = pagoService.registrarPago(3, cajero, Pago.MetodoPago.TARJETA, new BigDecimal("120.00"));

        assertEquals(Pago.MetodoPago.TARJETA, resultado.getMetodoPago());
        verify(pedidoService, times(1)).actualizarEstado(3, Pedido.EstadoPedido.PAGADO);
    }

    @Test
    void registrarPago_montoMayorAlTotal_debePermitirVueltoYRegistrarPago() {
        // Caso de negocio real: el cliente paga en efectivo con un billete mayor
        // al total (ej. total 45.50, paga con 50.00). El sistema debe aceptarlo:
        // solo rechaza cuando el monto pagado es MENOR al total (montoPagado < total).
        Pedido pedido = Pedido.builder().idPedido(4).estado(Pedido.EstadoPedido.EN_PROCESO)
                .total(new BigDecimal("45.50")).build();
        Empleado cajero = Empleado.builder().idEmpleado(2).usuario("cajero1").build();

        when(pedidoService.buscarPorId(4)).thenReturn(pedido);
        when(pagoRepository.save(any(Pago.class))).thenAnswer(inv -> inv.getArgument(0));

        Pago resultado = pagoService.registrarPago(4, cajero, Pago.MetodoPago.EFECTIVO, new BigDecimal("50.00"));

        assertEquals(new BigDecimal("50.00"), resultado.getMontoPagado());
        verify(pedidoService, times(1)).actualizarEstado(4, Pedido.EstadoPedido.PAGADO);
    }

    @Test
    void registrarPago_pedidoYaPagado_debeLanzarExcepcion() {
        Pedido pedido = Pedido.builder().idPedido(1).estado(Pedido.EstadoPedido.PAGADO)
                .total(new BigDecimal("50.00")).build();
        when(pedidoService.buscarPorId(1)).thenReturn(pedido);

        assertThrows(IllegalStateException.class, () ->
                pagoService.registrarPago(1, new Empleado(), Pago.MetodoPago.EFECTIVO, new BigDecimal("50.00")));

        verify(pagoRepository, never()).save(any());
    }

    @Test
    void registrarPago_pedidoAnulado_debeLanzarExcepcion() {
        Pedido pedido = Pedido.builder().idPedido(1).estado(Pedido.EstadoPedido.ANULADO)
                .total(new BigDecimal("50.00")).build();
        when(pedidoService.buscarPorId(1)).thenReturn(pedido);

        assertThrows(IllegalStateException.class, () ->
                pagoService.registrarPago(1, new Empleado(), Pago.MetodoPago.TARJETA, new BigDecimal("50.00")));

        verify(pagoRepository, never()).save(any());
    }

    @Test
    void registrarPago_montoMenorAlTotal_debeLanzarExcepcion() {
        Pedido pedido = Pedido.builder().idPedido(1).estado(Pedido.EstadoPedido.EN_PROCESO)
                .total(new BigDecimal("90.00")).build();
        when(pedidoService.buscarPorId(1)).thenReturn(pedido);

        assertThrows(IllegalArgumentException.class, () ->
                pagoService.registrarPago(1, new Empleado(), Pago.MetodoPago.EFECTIVO, new BigDecimal("50.00")));

        verify(pagoRepository, never()).save(any());
        verify(pedidoService, never()).actualizarEstado(any(), any());
    }
}
