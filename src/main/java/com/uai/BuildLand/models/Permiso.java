package com.uai.BuildLand.models;

import jakarta.persistence.*;

/**
 * Entidad que representa un permiso en el sistema BuildLand
 */
@Entity
@Table(name = "permisos")
public class Permiso implements ConjuntoGrupoPermiso {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true, length = 100)
    private String nombre;

    // Constructor por defecto
    public Permiso() {
    }

    // Constructor con parámetros
    public Permiso(String nombre) {
        this.nombre = nombre;
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

    @Override
    public String toString() {
        return "Permiso{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                '}';
    }
}
