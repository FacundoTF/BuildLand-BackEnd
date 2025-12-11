package com.uai.BuildLand.models;

import com.uai.BuildLand.models.auth.Usuario;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "personas_fisicas")
public class PersonaFisica {
    @Id
    @Column(name = "cuit", length = 11)
    private String cuit;

    @OneToOne(cascade = CascadeType.ALL)
    @MapsId
    @JoinColumn(name = "cuit")
    private Usuario usuario;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(name = "numero_telefono", length = 20)
    private String numeroTelefono;

    @Column(name = "codigo_postal", length = 10)
    private String codigoPostal;

    @Column(length = 100)
    private String ciudad;

    public PersonaFisica() {
    }

    public PersonaFisica(Usuario usuario, String cuit, String nombre, String numeroTelefono,
            String codigoPostal, String ciudad) {
        this.usuario = usuario;
        this.cuit = cuit;
        this.nombre = nombre;
        this.numeroTelefono = numeroTelefono;
        this.codigoPostal = codigoPostal;
        this.ciudad = ciudad;
    }

    // Getters y Setters
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
        return "PersonaFisica{" +
                ", usuario=" + (usuario != null ? usuario.getCuit() : "null") +
                ", cuit='" + cuit + '\'' +
                ", nombre='" + nombre + '\'' +
                ", numeroTelefono='" + numeroTelefono + '\'' +
                ", codigoPostal='" + codigoPostal + '\'' +
                ", ciudad='" + ciudad + '\'' +
                '}';
    }
}
