package com.buscador.Buscador;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Producto {
    private String nombre;
    private String manufacturador;
    private String categoria;
    private String descripcionCorta;
    private String descripcionLarga;
    private String imagen;
    private Integer cantidad;
    private Float precio;
}
