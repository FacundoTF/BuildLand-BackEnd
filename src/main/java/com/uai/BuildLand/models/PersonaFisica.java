package com.uai.BuildLand.models;

import com.uai.BuildLand.models.auth.Usuario;
import jakarta.persistence.*;
import org.apache.kafka.common.protocol.types.Field;
import org.springframework.beans.factory.annotation.Value;

@Entity
@Table(name = "personas_fisicas")
public class PersonaFisica implements ActoresSistema {
    @Id
    private String cuit;

    @OneToOne
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

    @Transient
    @Value("${mock.usuario.personaFisica.email}")
    private String correo;

    @Transient
    @Value("${mock.usuario.personaFisica.password}")
    private String password;

    @Transient
    @Value("${mock.usuario.personaFisica.cuit}")
    private String cuitMock;

    @Transient
    @Value("${mock.usuario.personaFisica.nombre}")
    private String nombreMock;

    public PersonaFisica() {
        this.cuit = cuitMock;
        this.nombre = nombreMock;
        this.numeroTelefono = null;
        this.codigoPostal = null;
        this.ciudad = null;
        this.usuario = new Usuario(
                cuitMock,
                Usuario.TipoUsuario.PERSONA_FISICA,
                correo,
                password
        );
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
