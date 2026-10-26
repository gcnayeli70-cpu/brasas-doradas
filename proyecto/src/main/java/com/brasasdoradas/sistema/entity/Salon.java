package com.brasasdoradas.sistema.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "salones")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Salon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_salon")
    private Integer idSalon;

    @Column(name = "nombre_salon", nullable = false, length = 50)
    private String nombreSalon;

    @Column(length = 150)
    private String descripcion;
}
