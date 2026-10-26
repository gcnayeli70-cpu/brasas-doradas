package com.brasasdoradas.sistema.service;

import com.brasasdoradas.sistema.entity.Empleado;
import com.brasasdoradas.sistema.entity.Pago;
import com.brasasdoradas.sistema.entity.Pedido;
import com.brasasdoradas.sistema.repository.PagoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PagoService {

    private final PagoRepository pagoRepository;
    private final PedidoService pedidoService;

    public List<Pago> listarTodos() {
        return pagoRepository.findAll();
    }

    public Pago buscarPorId(Integer id) {
        return pagoRepository.findById(id)
                .orElseThrow(() -> new java.util.NoSuchElementException("Pago no encontrado con id: " + id));
    }

    @Transactional
    public Pago registrarPago(Integer idPedido, Empleado empleadoCajero,
                              Pago.MetodoPago metodoPago, BigDecimal montoPagado) {

        Pedido pedido = pedidoService.buscarPorId(idPedido);

        if (pedido.getEstado() == Pedido.EstadoPedido.PAGADO) {
            throw new IllegalStateException("Este pedido ya fue pagado anteriormente.");
        }
        if (pedido.getEstado() == Pedido.EstadoPedido.ANULADO) {
            throw new IllegalStateException("No se puede cobrar un pedido anulado.");
        }
        if (montoPagado.compareTo(pedido.getTotal()) < 0) {
            throw new IllegalArgumentException(
                    "El monto pagado (S/ " + montoPagado + ") es menor al total del pedido (S/ " + pedido.getTotal() + ").");
        }

        Pago pago = Pago.builder()
                .pedido(pedido)
                .empleado(empleadoCajero)
                .metodoPago(metodoPago)
                .montoPagado(montoPagado)
                .build();

        Pago pagoGuardado = pagoRepository.save(pago);

        pedidoService.actualizarEstado(idPedido, Pedido.EstadoPedido.PAGADO);

        return pagoGuardado;
    }
}