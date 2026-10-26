package com.brasasdoradas.sistema.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "proveedores")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Proveedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_proveedor")
    private Integer idProveedor;

    @Column(name = "razon_social", nullable = false, length = 120)
    private String razonSocial;

    @Column(unique = true, length = 20)
    private String ruc;

    @Column(length = 20)
    private String telefono;

    @Column(length = 150)
    private String direccion;
}
