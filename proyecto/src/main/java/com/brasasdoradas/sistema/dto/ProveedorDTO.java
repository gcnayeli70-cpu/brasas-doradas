package com.brasasdoradas.sistema.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProveedorDTO {

    private Integer idProveedor;

    @NotBlank(message = "La razón social es obligatoria")
    @Size(max = 120)
    private String razonSocial;

    @Size(max = 20)
    private String ruc;

    @Size(max = 20)
    private String telefono;

    @Size(max = 150)
    private String direccion;
}