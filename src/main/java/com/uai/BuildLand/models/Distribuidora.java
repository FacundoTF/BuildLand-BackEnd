package com.uai.BuildLand.models;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "distribuidoras")
public class Distribuidora {
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;
    
    @Id
    @Column(name = "cuit", length = 11)
    private String cuit;
    
    @Column(nullable = false, length = 100)
    private String nombre;
    
    @Column(length = 200)
    private String direccion;
    
    @Column(name = "numero_telefono", length = 20)
    private String numeroTelefono;
    
    @Column(length = 255)
    private String descripcion;
    
    @Column(name = "codigo_postal", length = 10)
    private String codigoPostal;
    
    @Column(length = 100)
    private String ciudad;
    
    @ManyToMany
    @JoinTable(
        name = "distribuidora_categoria",
        joinColumns = @JoinColumn(name = "cuit"),
        inverseJoinColumns = @JoinColumn(name = "nombre_categoria")
    )
    private List<Categoria> categorias;

    public Distribuidora() {
    }

    public Distribuidora(Usuario usuario, String cuit, String nombre, String direccion, String numeroTelefono, String descripcion, String codigoPostal, String ciudad, List<Categoria> categorias) {
        this.usuario = usuario;
        this.cuit = cuit;
        this.nombre = nombre;
        this.direccion = direccion;
        this.numeroTelefono = numeroTelefono;
        this.descripcion = descripcion;
        this.codigoPostal = codigoPostal;
        this.ciudad = ciudad;
        this.categorias = categorias;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public String getCuit() {
        return cuit;
    }

    public void setCuit(String cuit) {
        this.cuit = cuit;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getNumeroTelefono() {
        return numeroTelefono;
    }

    public void setNumeroTelefono(String numeroTelefono) {
        this.numeroTelefono = numeroTelefono;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getCodigoPostal() {
        return codigoPostal;
    }

    public void setCodigoPostal(String codigoPostal) {
        this.codigoPostal = codigoPostal;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public List<Categoria> getCategorias() {
        return categorias;
    }

    public void setCategorias(List<Categoria> categorias) {
        this.categorias = categorias;
    }

    @Override
    public String toString() {
        return "Distribuidora{" +
                "id_usuario=" + usuario.getId() +
                ", cuit='" + cuit + '\'' +
                ", nombre='" + nombre + '\'' +
                ", direccion='" + direccion + '\'' +
                ", numeroTelefono='" + numeroTelefono + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", codigoPostal='" + codigoPostal + '\'' +
                ", ciudad='" + ciudad + '\'' +
                ", categorias=" + (categorias != null ? categorias.size() : "0") + " categorías" +
                '}';
    }
}
