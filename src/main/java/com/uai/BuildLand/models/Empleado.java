package com.uai.BuildLand.models;

import com.uai.BuildLand.models.auth.Usuario;
import jakarta.persistence.*;

/**
 * Entidad que representa un empleado en el sistema BuildLand
 */
@Entity
@Table(name = "empleados")
public class Empleado {
    @Id
    @JoinColumn(name = "usuario_cuit")
    private String cuit;

    @OneToOne
    @MapsId
    private Usuario usuario;
    
    @Column(nullable = false, length = 100)
    private String nombre;
    
    @Column(name = "numero_telefono", length = 20)
    private String numeroTelefono;
    
    @Column(length = 200)
    private String direccion;
    
    @Column(name = "codigo_postal", length = 10)
    private String codigoPostal;
    
    @Column(length = 100)
    private String ciudad;

    // Constructor por defecto
    public Empleado() {
    }

    // Constructor con parámetros
    public Empleado(Usuario usuario, String cuit, String nombre, String numeroTelefono, String direccion, String codigoPostal, String ciudad) {
        this.usuario = usuario;
//        this.cuit = cuit;
        this.nombre = nombre;
        this.numeroTelefono = numeroTelefono;
        this.direccion = direccion;
        this.codigoPostal = codigoPostal;
        this.ciudad = ciudad;
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

    public String getNumeroTelefono() {
        return numeroTelefono;
    }
    public void setNumeroTelefono(String numeroTelefono) {
        this.numeroTelefono = numeroTelefono;
    }

    public String getDireccion() {
        return direccion;
    }
    public void setDireccion(String direccion) {
        this.direccion = direccion;
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

   @Override
    public String toString() {
        return "Empleado{" +
                "cuit='" + '\'' +
                ", nombre='" + nombre + '\'' +
                ", numeroTelefono='" + numeroTelefono + '\'' +
                ", direccion='" + direccion + '\'' +
                ", codigoPostal='" + codigoPostal + '\'' +
                ", ciudad='" + ciudad + '\'' +
                '}';
    }
}
