package com.uai.BuildLand.models;

import java.util.List;
import java.util.ArrayList;
import jakarta.persistence.*;

/**
 * Entidad que representa un grupo en el sistema BuildLand
 */
@Entity
@Table(name = "grupos")
public class Grupo implements ConjuntoGrupoPermiso {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 100)
    private String nombre;
    
    @Column(length = 255)
    private String descripcion;
    
    @ManyToMany
    @JoinTable(
        name = "grupo_permisos",
        joinColumns = @JoinColumn(name = "id_grupo"),
        inverseJoinColumns = @JoinColumn(name = "id_permiso")
    )
    private List<Permiso> permisos = new ArrayList<>();
    
    @ManyToMany
    @JoinTable(
        name = "grupo_subgrupos",
        joinColumns = @JoinColumn(name = "id_grupo_padre"),
        inverseJoinColumns = @JoinColumn(name = "id_grupo_hijo")
    )
    private List<Grupo> grupos = new ArrayList<>();

    // Constructor por defecto
    public Grupo() {
    }

    // Constructor con parámetros
    public Grupo(String nombre, String descripcion, List<Permiso> permisos, List<Grupo> grupos) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.permisos = permisos != null ? permisos : new ArrayList<>();
        this.grupos = grupos != null ? grupos : new ArrayList<>();
    }

    // Getters y Setters
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

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public List<Permiso> getPermisos() {
        return permisos;
    }

    public void setPermisos(List<Permiso> permisos) {
        this.permisos = permisos;
    }

    public List<Grupo> getGrupos() {
        return grupos;
    }

    public void setGrupos(List<Grupo> grupos) {
        this.grupos = grupos;
    }

    @Override
    public String toString() {
        return "Grupo{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", permisos=" + permisos.size() +
                ", grupos=" + grupos.size() +
                '}';
    }
}
