package com.uai.BuildLand.models.auth;

import com.uai.BuildLand.models.access.ConjuntoGrupoPermiso;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Entidad que representa un usuario del sistema BuildLand.
 * Puede ser de tipo Distribuidora, Empleado o PersonaFisica.
 */
@Setter @Getter
@Entity
@Table(name = "usuarios")
public class Usuario {
    // Getters y Setters
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

    @Column(name = "estado")
    private boolean estado;

    @ManyToMany
    private final List<ConjuntoGrupoPermiso> conjunto = new ArrayList<>();

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
    public Usuario(String cuit, TipoUsuario tipoUsuario, String email, String password, boolean estado) {
        this.cuit = cuit;
        this.tipoUsuario = tipoUsuario;
        this.email = email;
        this.password = password;
        this.estado = estado;
    }

    public boolean getEstado() {
        return estado;
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
