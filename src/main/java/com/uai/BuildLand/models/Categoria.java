package com.uai.BuildLand.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "categorias")
public class Categoria {
    @Id
    @Column(name = "nombre_categoria", length = 100)
    private String nombre;

    // Constructor, getters y setters
    public Categoria(String nombre) {
        this.nombre = nombre;
    }

    public Categoria() {

    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
