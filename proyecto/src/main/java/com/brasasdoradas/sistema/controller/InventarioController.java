package com.brasasdoradas.sistema.controller;

import com.brasasdoradas.sistema.entity.MovimientoInventario;
import com.brasasdoradas.sistema.repository.ProveedorRepository;
import com.brasasdoradas.sistema.service.MovimientoInventarioService;
import com.brasasdoradas.sistema.service.ProductoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/inventario")
@RequiredArgsConstructor
public class InventarioController {

    private final MovimientoInventarioService movimientoService;
    private final ProductoService productoService;
    private final ProveedorRepository proveedorRepository;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("movimientos", movimientoService.listarTodos());
        return "inventario/lista";
    }

    @GetMapping("/nuevo")
    public String nuevoForm(Model model) {
        model.addAttribute("productos", productoService.listarTodos());
        model.addAttribute("proveedores", proveedorRepository.findAll());
        model.addAttribute("tipos", MovimientoInventario.TipoMovimiento.values());
        return "inventario/formulario";
    }

    @PostMapping("/registrar")
    public String registrar(@RequestParam Integer idProducto,
                            @RequestParam(required = false) Integer idProveedor,
                            @RequestParam MovimientoInventario.TipoMovimiento tipoMovimiento,
                            @RequestParam Integer cantidad,
                            @RequestParam(required = false) String observacion,
                            Model model) {
        try {
            var producto = productoService.buscarPorId(idProducto);
            var proveedor = (idProveedor != null) ? proveedorRepository.findById(idProveedor).orElse(null) : null;

            MovimientoInventario movimiento = MovimientoInventario.builder()
                    .producto(producto)
                    .proveedor(proveedor)
                    .tipoMovimiento(tipoMovimiento)
                    .cantidad(cantidad)
                    .observacion(observacion)
                    .build();

            movimientoService.registrarMovimiento(movimiento);
            return "redirect:/inventario";
        } catch (RuntimeException ex) {
            model.addAttribute("productos", productoService.listarTodos());
            model.addAttribute("proveedores", proveedorRepository.findAll());
            model.addAttribute("tipos", MovimientoInventario.TipoMovimiento.values());
            model.addAttribute("error", ex.getMessage());
            return "inventario/formulario";
        }
    }
}