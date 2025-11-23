package com.uai.BuildLand.models.auth;

import com.uai.BuildLand.models.access.Grupo;
import com.uai.BuildLand.models.access.Permiso;
import jakarta.persistence.*;

/**
 * Entidad que representa un usuario del sistema BuildLand.
 * Puede ser de tipo Distribuidora, Empleado o PersonaFisica.
 */
@Entity
@Table(name = "usuarios")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_usuario", nullable = false)
    private TipoUsuario tipoUsuario;

    @Column(name = "id_referencia", length = 50)
    private String idReferencia; // ID de la distribuidora, empleado o persona física

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
    public Usuario(TipoUsuario tipoUsuario, String idReferencia, String email, String password) {
        this.tipoUsuario = tipoUsuario;
        this.idReferencia = idReferencia;
        this.email = email;
        this.password = password;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TipoUsuario getTipoUsuario() {
        return tipoUsuario;
    }

    public void setTipoUsuario(TipoUsuario tipoUsuario) {
        this.tipoUsuario = tipoUsuario;
    }

    public String getIdReferencia() {
        return idReferencia;
    }

    public void setIdReferencia(String idReferencia) {
        this.idReferencia = idReferencia;
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
                "id=" + id +
                ", tipoUsuario=" + tipoUsuario +
                ", idReferencia='" + idReferencia + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
