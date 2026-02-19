package com.uai.BuildLand.models.access;

import jakarta.persistence.*;

/**
 * Entidad que representa un permiso en el sistema BuildLand
 */
@Entity
@Table(name = "permisos")
@PrimaryKeyJoinColumn(name = "id")
public class Permiso extends ConjuntoGrupoPermiso {

    @Column(length = 255)
    protected String descripcion;

    // Constructor por defecto
    public Permiso() {
    }

    // Constructor con parámetros
    public Permiso(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    @Override
    public String toString() {
        return "Permiso{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", descripcion='" + descripcion + '\'' +
                '}';
    }
}
