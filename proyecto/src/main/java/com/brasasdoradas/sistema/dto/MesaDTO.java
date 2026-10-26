package com.brasasdoradas.sistema.dto;

import com.brasasdoradas.sistema.entity.Mesa;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MesaDTO {

    private Integer idMesa;

    @NotNull(message = "El número de mesa es obligatorio")
    private Integer numeroMesa;

    @NotNull(message = "Debe seleccionar un salón")
    private Integer idSalon;

    private Integer capacidad;

    private Mesa.EstadoMesa estado;
}