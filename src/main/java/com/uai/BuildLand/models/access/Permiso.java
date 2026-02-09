package com.uai.BuildLand.models.access;

import jakarta.persistence.*;

/**
 * Entidad que representa un permiso en el sistema BuildLand
 */
@Entity
@Table(name = "permisos")
@PrimaryKeyJoinColumn(name = "id")
public class Permiso extends ConjuntoGrupoPermiso {

    // Constructor por defecto
    public Permiso() {
    }

    // Constructor con parámetros
    public Permiso(String nombre) {
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
