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
@PrimaryKeyJoinColumn(name = "id")
public class Grupo extends ConjuntoGrupoPermiso {

    @Column(length = 255)
    private String descripcion;

    @ManyToMany(fetch = FetchType.EAGER) // TODO: Consider LAZY if performance issues arise
    @JoinTable(
        name = "grupo_elementos",
        joinColumns = @JoinColumn(name = "id_grupo"),
        inverseJoinColumns = @JoinColumn(name = "id_elemento")
    )
    private List<ConjuntoGrupoPermiso> elementos = new ArrayList<>();

    // Constructor por defecto
    public Grupo() {
    }

    // Constructor con parámetros
    public Grupo(String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.elementos = new ArrayList<>();
    }

    public void agregarElemento(ConjuntoGrupoPermiso elemento) {
        if (!elementos.contains(elemento)) {
            elementos.add(elemento);
        }
    }

    public void removerElemento(ConjuntoGrupoPermiso elemento) {
        elementos.remove(elemento);
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public List<ConjuntoGrupoPermiso> getElementos() {
        return elementos;
    }

    public void setElementos(List<ConjuntoGrupoPermiso> elementos) {
        this.elementos = elementos;
    }

    @Override
    public String toString() {
        return "Grupo{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", elementos=" + elementos.size() +
                '}';
    }
}
