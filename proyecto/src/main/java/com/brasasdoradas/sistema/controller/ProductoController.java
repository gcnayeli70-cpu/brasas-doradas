package com.brasasdoradas.sistema.controller;

import com.brasasdoradas.sistema.dto.ProductoDTO;
import com.brasasdoradas.sistema.entity.Categoria;
import com.brasasdoradas.sistema.entity.Producto;
import com.brasasdoradas.sistema.repository.CategoriaRepository;
import com.brasasdoradas.sistema.service.ProductoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/productos")
@RequiredArgsConstructor
public class ProductoController {

    private static final String VISTA_FORMULARIO = "productos/formulario";
    private static final String ATRIBUTO_CATEGORIAS = "categorias";

    private final ProductoService productoService;
    private final CategoriaRepository categoriaRepository;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("productos", productoService.listarTodos());
        return "productos/lista";
    }

    @GetMapping("/nuevo")
    public String nuevoForm(Model model) {
        model.addAttribute("producto", new ProductoDTO());
        model.addAttribute(ATRIBUTO_CATEGORIAS, categoriaRepository.findAll());
        return VISTA_FORMULARIO;
    }

    @GetMapping("/editar/{id}")
    public String editarForm(@PathVariable Integer id, Model model) {
        Producto producto = productoService.buscarPorId(id);
        model.addAttribute("producto", toDTO(producto));
        model.addAttribute(ATRIBUTO_CATEGORIAS, categoriaRepository.findAll());
        return VISTA_FORMULARIO;
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute("producto") ProductoDTO productoDTO,
                          BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute(ATRIBUTO_CATEGORIAS, categoriaRepository.findAll());
            return VISTA_FORMULARIO;
        }
        productoService.guardar(toEntity(productoDTO));
        return "redirect:/productos";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Integer id) {
        productoService.eliminar(id);
        return "redirect:/productos";
    }

    private ProductoDTO toDTO(Producto producto) {
        ProductoDTO dto = new ProductoDTO();
        dto.setIdProducto(producto.getIdProducto());
        dto.setNombreProducto(producto.getNombreProducto());
        dto.setDescripcion(producto.getDescripcion());
        dto.setPrecio(producto.getPrecio());
        dto.setIdCategoria(producto.getCategoria().getIdCategoria());
        dto.setStock(producto.getStock());
        dto.setEstado(producto.getEstado());
        return dto;
    }

    private Producto toEntity(ProductoDTO dto) {
        Categoria categoria = categoriaRepository.findById(dto.getIdCategoria())
                .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada: " + dto.getIdCategoria()));

        Producto producto = new Producto();
        producto.setIdProducto(dto.getIdProducto());
        producto.setNombreProducto(dto.getNombreProducto());
        producto.setDescripcion(dto.getDescripcion());
        producto.setPrecio(dto.getPrecio());
        producto.setCategoria(categoria);
        if (dto.getStock() != null) {
            producto.setStock(dto.getStock());
        }
        if (dto.getEstado() != null) {
            producto.setEstado(dto.getEstado());
        }
        return producto;
    }
}