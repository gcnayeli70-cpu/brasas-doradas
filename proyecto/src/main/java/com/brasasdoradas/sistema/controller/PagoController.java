package com.brasasdoradas.sistema.controller;

import com.brasasdoradas.sistema.entity.Empleado;
import com.brasasdoradas.sistema.entity.Pago;
import com.brasasdoradas.sistema.repository.EmpleadoRepository;
import com.brasasdoradas.sistema.service.PagoService;
import com.brasasdoradas.sistema.service.PedidoService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@Controller
@RequestMapping("/pagos")
@RequiredArgsConstructor
public class PagoController {

    private final PagoService pagoService;
    private final PedidoService pedidoService;
    private final EmpleadoRepository empleadoRepository;

    @GetMapping("/nuevo/{idPedido}")
    public String nuevoForm(@PathVariable Integer idPedido, Model model) {
        model.addAttribute("pedido", pedidoService.buscarPorId(idPedido));
        model.addAttribute("metodosPago", Pago.MetodoPago.values());
        return "pagos/formulario";
    }

    @PostMapping("/registrar")
    public String registrar(@RequestParam Integer idPedido,
                            @RequestParam Pago.MetodoPago metodoPago,
                            @RequestParam BigDecimal montoPagado,
                            Authentication authentication,
                            Model model) {

        Empleado cajero = empleadoRepository.findByUsuario(authentication.getName())
                .orElseThrow(() -> new IllegalStateException("Empleado no encontrado"));

        try {
            Pago pago = pagoService.registrarPago(idPedido, cajero, metodoPago, montoPagado);
            return "redirect:/pagos/boleta/" + pago.getIdPago();
        } catch (RuntimeException ex) {
            model.addAttribute("pedido", pedidoService.buscarPorId(idPedido));
            model.addAttribute("metodosPago", Pago.MetodoPago.values());
            model.addAttribute("error", ex.getMessage());
            return "pagos/formulario";
        }
    }

    @GetMapping("/boleta/{idPago}")
    @Transactional(readOnly = true)
    public String verBoleta(@PathVariable Integer idPago, Model model) {
        model.addAttribute("pago", pagoService.buscarPorId(idPago));
        return "pagos/boleta";
    }
}