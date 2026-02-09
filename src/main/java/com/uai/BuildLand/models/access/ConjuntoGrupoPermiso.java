package com.uai.BuildLand.models.access;

import jakarta.persistence.*;

/**
 * Clase base abstracta para implementar el patrón Composite.
 * Representa tanto a Grupos como a Permisos.
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "conjunto_grupo_permiso")
public abstract class ConjuntoGrupoPermiso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @Column(nullable = false, length = 100)
    protected String nombre;

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
}
