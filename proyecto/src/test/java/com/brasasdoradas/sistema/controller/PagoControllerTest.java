package com.brasasdoradas.sistema.controller;

import com.brasasdoradas.sistema.entity.DetallePedido;
import com.brasasdoradas.sistema.entity.Empleado;
import com.brasasdoradas.sistema.entity.Mesa;
import com.brasasdoradas.sistema.entity.Pago;
import com.brasasdoradas.sistema.entity.Pedido;
import com.brasasdoradas.sistema.entity.Producto;
import com.brasasdoradas.sistema.repository.EmpleadoRepository;
import com.brasasdoradas.sistema.service.PagoService;
import com.brasasdoradas.sistema.service.PedidoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PagoController.class)
@AutoConfigureMockMvc(addFilters = false)
class PagoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PagoService pagoService;
    @MockBean
    private PedidoService pedidoService;
    @MockBean
    private EmpleadoRepository empleadoRepository;

    private final Principal cajeroAutenticado = new UsernamePasswordAuthenticationToken("cajero1", null);

    @Test
    void nuevoForm_debeResponder200YMostrarPedido() throws Exception {
        Mesa mesa = Mesa.builder().idMesa(1).numeroMesa(5).build();
        Empleado mozo = Empleado.builder().idEmpleado(3).nombres("Luis").build();
        Pedido pedido = Pedido.builder()
                .idPedido(1)
                .mesa(mesa)
                .empleado(mozo)
                .total(new BigDecimal("90.00"))
                .build();
        when(pedidoService.buscarPorId(1)).thenReturn(pedido);

        mockMvc.perform(get("/pagos/nuevo/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("pagos/formulario"))
                .andExpect(model().attributeExists("pedido", "metodosPago"));
    }

    @Test
    void registrar_pagoValido_debeRedirigirALaBoleta() throws Exception {
        Empleado cajero = Empleado.builder().idEmpleado(2).usuario("cajero1").build();
        Pago pagoGuardado = Pago.builder().idPago(7).build();

        when(empleadoRepository.findByUsuario("cajero1")).thenReturn(Optional.of(cajero));
        when(pagoService.registrarPago(eq(1), eq(cajero), eq(Pago.MetodoPago.EFECTIVO), any(BigDecimal.class)))
                .thenReturn(pagoGuardado);

        mockMvc.perform(post("/pagos/registrar")
                        .principal(cajeroAutenticado)
                        .param("idPedido", "1")
                        .param("metodoPago", "EFECTIVO")
                        .param("montoPagado", "90.00"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/pagos/boleta/7"));
    }

    @Test
    void registrar_montoInsuficiente_debeMostrarFormularioConError() throws Exception {
        Mesa mesa = Mesa.builder().idMesa(1).numeroMesa(5).build();
        Empleado mozo = Empleado.builder().idEmpleado(3).nombres("Luis").build();
        Empleado cajero = Empleado.builder().idEmpleado(2).usuario("cajero1").build();
        Pedido pedido = Pedido.builder()
                .idPedido(1)
                .mesa(mesa)
                .empleado(mozo)
                .total(new BigDecimal("90.00"))
                .build();

        when(empleadoRepository.findByUsuario("cajero1")).thenReturn(Optional.of(cajero));
        when(pagoService.registrarPago(eq(1), eq(cajero), any(), any(BigDecimal.class)))
                .thenThrow(new IllegalArgumentException("El monto pagado es menor al total del pedido."));
        when(pedidoService.buscarPorId(1)).thenReturn(pedido);

        mockMvc.perform(post("/pagos/registrar")
                        .principal(cajeroAutenticado)
                        .param("idPedido", "1")
                        .param("metodoPago", "EFECTIVO")
                        .param("montoPagado", "10.00"))
                .andExpect(status().isOk())
                .andExpect(view().name("pagos/formulario"))
                .andExpect(model().attributeExists("error"));
    }

    @Test
    void registrar_conYapePlin_debeRedirigirALaBoleta() throws Exception {
        Empleado cajero = Empleado.builder().idEmpleado(2).usuario("cajero1").build();
        Pago pagoGuardado = Pago.builder().idPago(9).build();

        when(empleadoRepository.findByUsuario("cajero1")).thenReturn(Optional.of(cajero));
        when(pagoService.registrarPago(eq(1), eq(cajero), eq(Pago.MetodoPago.YAPE_PLIN), any(BigDecimal.class)))
                .thenReturn(pagoGuardado);

        mockMvc.perform(post("/pagos/registrar")
                        .principal(cajeroAutenticado)
                        .param("idPedido", "1")
                        .param("metodoPago", "YAPE_PLIN")
                        .param("montoPagado", "45.50"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/pagos/boleta/9"));
    }

    @Test
    void verBoleta_debeResponder200YMostrarPago() throws Exception {
        Mesa mesa = Mesa.builder().idMesa(1).numeroMesa(5).build();
        Empleado mozo = Empleado.builder().idEmpleado(3).nombres("Luis").build();
        Producto producto = Producto.builder().idProducto(1).nombreProducto("Pollo Entero").build();
        DetallePedido detalle = DetallePedido.builder()
                .idDetalle(1)
                .producto(producto)
                .cantidad(1)
                .precioUnitario(new BigDecimal("78.00"))
                .subtotalLinea(new BigDecimal("78.00"))
                .build();
        Pedido pedido = Pedido.builder()
                .idPedido(1)
                .mesa(mesa)
                .empleado(mozo)
                .detalles(List.of(detalle))
                .subtotal(new BigDecimal("66.10"))
                .igv(new BigDecimal("11.90"))
                .total(new BigDecimal("78.00"))
                .build();
        Pago pago = Pago.builder()
                .idPago(7)
                .pedido(pedido)
                .metodoPago(Pago.MetodoPago.EFECTIVO)
                .montoPagado(new BigDecimal("78.00"))
                .fechaPago(LocalDateTime.now())
                .build();
        when(pagoService.buscarPorId(7)).thenReturn(pago);

        mockMvc.perform(get("/pagos/boleta/7"))
                .andExpect(status().isOk())
                .andExpect(view().name("pagos/boleta"))
                .andExpect(model().attributeExists("pago"));
    }
}