package com.brasasdoradas.sistema.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClienteDTO {

    private Integer idCliente;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 80)
    private String nombres;

    @Size(max = 80)
    private String apellidos;

    @Size(max = 15)
    private String dni;

    @Size(max = 20)
    private String telefono;

    @Size(max = 100)
    private String correo;

    @Size(max = 150)
    private String direccion;
}