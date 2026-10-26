package com.brasasdoradas.sistema.dto;

import com.brasasdoradas.sistema.entity.Producto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ProductoDTO {

    private Integer idProducto;

    @NotBlank(message = "El nombre del producto es obligatorio")
    @Size(max = 100)
    private String nombreProducto;

    @Size(max = 255)
    private String descripcion;

    @NotNull(message = "El precio es obligatorio")
    private BigDecimal precio;

    @NotNull(message = "Debe seleccionar una categoría")
    private Integer idCategoria;

    private Integer stock;

    private Producto.EstadoProducto estado;
}