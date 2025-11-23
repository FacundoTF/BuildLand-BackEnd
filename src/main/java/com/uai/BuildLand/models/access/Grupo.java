package com.uai.BuildLand.models.access;

import java.util.List;
import java.util.ArrayList;

import com.uai.BuildLand.models.auth.Usuario;
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
    
    @OneToMany(mappedBy = "grupoPadre", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GrupoSubgrupo> subgrupos = new ArrayList<>();
    
    @OneToMany(mappedBy = "grupoHijo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GrupoSubgrupo> gruposContenedores = new ArrayList<>();

    // Constructor por defecto
    public Grupo() {
    }

    // Constructor con parámetros
    public Grupo(String nombre, String descripcion, List<Permiso> permisos) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.permisos = permisos != null ? permisos : new ArrayList<>();
        this.subgrupos = new ArrayList<>();
        this.gruposContenedores = new ArrayList<>();
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

    public List<GrupoSubgrupo> getSubgrupos() {
        return subgrupos;
    }

    public void setSubgrupos(List<GrupoSubgrupo> subgrupos) {
        this.subgrupos = subgrupos;
    }

    public List<GrupoSubgrupo> getGruposContenedores() {
        return gruposContenedores;
    }

    public void setGruposContenedores(List<GrupoSubgrupo> gruposContenedores) {
        this.gruposContenedores = gruposContenedores;
    }

    @Override
    public String toString() {
        return "Grupo{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", permisos=" + permisos.size() +
                ", subgrupos=" + subgrupos.size() +
                '}';
    }
}
