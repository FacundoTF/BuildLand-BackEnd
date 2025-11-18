package com.uai.BuildLand.models;

import jakarta.persistence.*;

@Entity
@Table(name = "marcas")
public class Marca {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_marca", nullable = false, updatable = false)
    private Long id;

    @Column(nullable = false, name = "nombre")
    private String nombre;

    @ManyToOne
    @JoinColumn(name = "categoria_nombre", nullable = false)
    private Categoria categoria;

    // Constructors, Getters and Setters

    public Marca() {
    }

    public Marca(String nombre, Categoria categoria) {
        this.nombre = nombre;
        this.categoria = categoria;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }
}