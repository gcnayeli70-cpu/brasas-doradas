package com.brasasdoradas.sistema.controller;

import com.brasasdoradas.sistema.entity.*;
import com.brasasdoradas.sistema.repository.*;
import com.brasasdoradas.sistema.service.MesaService;
import com.brasasdoradas.sistema.service.PedidoService;
import com.brasasdoradas.sistema.service.ProductoService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/pedidos")
@RequiredArgsConstructor
public class PedidoController {

    private static final String REDIRECT_PEDIDOS = "redirect:/pedidos";

    private final PedidoService pedidoService;
    private final MesaService mesaService;
    private final ProductoService productoService;
    private final ClienteRepository clienteRepository;
    private final EmpleadoRepository empleadoRepository;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("pedidos", pedidoService.listarTodos());
        return "pedidos/lista";
    }

    @GetMapping("/nuevo")
    public String nuevoForm(Model model) {
        model.addAttribute("mesas", mesaService.listarDisponibles());
        model.addAttribute("clientes", clienteRepository.findAll());
        model.addAttribute("productos", productoService.listarActivos());
        return "pedidos/formulario";
    }

    /**
     * Registra un pedido con N lineas de detalle.
     * Recibe arreglos paralelos productoId[] y cantidad[] desde el formulario.
     */
    @PostMapping("/registrar")
    public String registrar(@RequestParam Integer idMesa,
                            @RequestParam(required = false) Integer idCliente,
                            @RequestParam("productoId") List<Integer> productoIds,
                            @RequestParam("cantidad") List<Integer> cantidades,
                            Authentication authentication) {

        Empleado empleado = empleadoRepository.findByUsuario(authentication.getName())
                .orElseThrow(() -> new IllegalStateException("Empleado no encontrado para el usuario autenticado"));

        Mesa mesa = mesaService.buscarPorId(idMesa);
        Cliente cliente = (idCliente != null) ? clienteRepository.findById(idCliente).orElse(null) : null;

        Pedido pedido = Pedido.builder()
                .mesa(mesa)
                .empleado(empleado)
                .cliente(cliente)
                .detalles(new ArrayList<>())
                .build();

        for (int i = 0; i < productoIds.size(); i++) {
            Producto producto = productoService.buscarPorId(productoIds.get(i));
            int cantidad = cantidades.get(i);

            DetallePedido detalle = DetallePedido.builder()
                    .producto(producto)
                    .cantidad(cantidad)
                    .precioUnitario(producto.getPrecio())
                    .pedido(pedido)
                    .build();

            pedido.getDetalles().add(detalle);
        }

        pedidoService.registrarPedido(pedido);
        mesaService.cambiarEstado(idMesa, Mesa.EstadoMesa.OCUPADA);

        return REDIRECT_PEDIDOS;
    }

    @GetMapping("/estado/{id}/{estado}")
    public String cambiarEstado(@PathVariable Integer id, @PathVariable Pedido.EstadoPedido estado) {
        pedidoService.actualizarEstado(id, estado);
        return REDIRECT_PEDIDOS;
    }

    @GetMapping("/anular/{id}")
    public String anular(@PathVariable Integer id) {
        pedidoService.anular(id);
        return REDIRECT_PEDIDOS;
    }
}