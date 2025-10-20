package com.uai.BuildLand.models;

import jakarta.persistence.*;

/**
 * Entidad que representa una distribuidora en el sistema BuildLand
 */
// @Entity
// public record Distribuidora(
    
//     // @Column(name = "cuit", length = 11)
//     @Id
//     String cuit,

//     // @Column(nullable = false, length = 100)
//     String nombre,

//     // @Column(length = 200)
//     String direccion,

//     // @Column(name = "numero_telefono", length = 20)
//     String numeroTelefono,

//     // @Column(length = 255)
//     String descripcion,

//     // @Column(name = "codigo_postal", length = 10)
//     String codigoPostal,

//     // @Column(length = 100)
//     String ciudad
// ) {}
@Entity
@Table(name = "distribuidoras")
public class Distribuidora {
    @OneToOne
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;
    
    @Id
    @Column(name = "cuit", length = 11)
    private String id;
    
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

    public Distribuidora() {
    }

    public Distribuidora(Usuario usuario, String cuit, String nombre, String direccion, String numeroTelefono, String descripcion, String codigoPostal, String ciudad) {
        this.usuario = usuario;
        this.id = cuit;
        this.nombre = nombre;
        this.direccion = direccion;
        this.numeroTelefono = numeroTelefono;
        this.descripcion = descripcion;
        this.codigoPostal = codigoPostal;
        this.ciudad = ciudad;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public String GetCuit() {
        return id;
    }

    public void SetCuit(String cuit) {
        this.id = cuit;
    }

    public String GetNombre() {
        return nombre;
    }

    public void SetNombre(String nombre) {
        this.nombre = nombre;
    }

    public String GetDireccion() {
        return direccion;
    }

    public void SetDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String GetNumeroTelefono() {
        return numeroTelefono;
    }

    public void SetNumeroTelefono(String numeroTelefono) {
        this.numeroTelefono = numeroTelefono;
    }

    public String GetDescripcion() {
        return descripcion;
    }

    public void SetDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String GetCodigoPostal() {
        return codigoPostal;
    }

    public void SetCodigoPostal(String codigoPostal) {
        this.codigoPostal = codigoPostal;
    }

    public String GetCiudad() {
        return ciudad;
    }

    public void SetCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    @Override
    public String toString() {
        return "Distribuidora{" +
                "id_usuario=" + usuario.getId() +
                ", cuit='" + id + '\'' +
                ", nombre='" + nombre + '\'' +
                ", direccion='" + direccion + '\'' +
                ", numeroTelefono='" + numeroTelefono + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", codigoPostal='" + codigoPostal + '\'' +
                ", ciudad='" + ciudad + '\'' +
                '}';
    }
}
