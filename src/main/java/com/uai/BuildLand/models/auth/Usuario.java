package com.uai.BuildLand.models.auth;

import jakarta.persistence.*;

/**
 * Entidad que representa un usuario del sistema BuildLand.
 * Puede ser de tipo Distribuidora, Empleado o PersonaFisica.
 */
@Entity
@Table(name = "usuarios")
public class Usuario {
    @Id
    @Column(name = "cuit", nullable = false, length = 11)
    private String cuit;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_usuario", nullable = false)
    private TipoUsuario tipoUsuario;


    @Column(name = "email", unique = true, nullable = false, length = 100)
    private String email;

    @Column(nullable = false, length = 255)
    private String password;

    // Enum para tipos de usuario
    public enum TipoUsuario {
        DISTRIBUIDORA,
        EMPLEADO,
        PERSONA_FISICA
    }

    // Constructor por defecto
    public Usuario() {
    }

    // Constructor con parámetros
    public Usuario(String cuit, TipoUsuario tipoUsuario, String email, String password) {
        this.cuit = cuit;
        this.tipoUsuario = tipoUsuario;
        this.email = email;
        this.password = password;
    }

    // Getters y Setters
    public String getCuit() {
        return cuit;
    }

    public void setCuit(String cuit) {
        this.cuit = cuit;
    }

    public TipoUsuario getTipoUsuario() {
        return tipoUsuario;
    }

    public void setTipoUsuario(TipoUsuario tipoUsuario) {
        this.tipoUsuario = tipoUsuario;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "cuit='" + cuit + '\'' +
                ", tipoUsuario=" + tipoUsuario +
                ", email='" + email + '\'' +
                '}';
    }
}
