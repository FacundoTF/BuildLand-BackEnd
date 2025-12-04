package com.uai.BuildLand.models.auth;

import com.uai.BuildLand.models.access.Permiso;
import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entidad intermedia que representa la relación muchos-a-muchos entre Usuario y Permiso.
 * Cada registro indica que un usuario tiene un permiso específico asignado directamente.
 */
@Entity
@Table(name = "usuario_permiso")
@IdClass(UsuarioPermiso.UsuarioPermisoId.class)
public class UsuarioPermiso {
    
    @Id
    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @Id
    @ManyToOne
    @JoinColumn(name = "id_permiso", nullable = false)
    private Permiso permiso;
    
    @Column(name = "fecha_asignacion")
    private LocalDateTime fechaAsignacion;

    // Constructores
    public UsuarioPermiso() {
        this.fechaAsignacion = LocalDateTime.now();
    }

    public UsuarioPermiso(Usuario usuario, Permiso permiso) {
        this.usuario = usuario;
        this.permiso = permiso;
        this.fechaAsignacion = LocalDateTime.now();
    }

    // Getters y Setters
    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Permiso getPermiso() {
        return permiso;
    }

    public void setPermiso(Permiso permiso) {
        this.permiso = permiso;
    }

    public LocalDateTime getFechaAsignacion() {
        return fechaAsignacion;
    }

    public void setFechaAsignacion(LocalDateTime fechaAsignacion) {
        this.fechaAsignacion = fechaAsignacion;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UsuarioPermiso that = (UsuarioPermiso) o;
        return Objects.equals(usuario, that.usuario) && Objects.equals(permiso, that.permiso);
    }

    @Override
    public int hashCode() {
        return Objects.hash(usuario, permiso);
    }

    /**
     * Clase interna para la clave compuesta de UsuarioPermiso.
     * Necesaria para @IdClass que permite usar múltiples @Id en JPA.
     */
    public static class UsuarioPermisoId implements Serializable {
        private Long usuario;
        private Long permiso;

        public UsuarioPermisoId() {}

        public UsuarioPermisoId(Long usuario, Long permiso) {
            this.usuario = usuario;
            this.permiso = permiso;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            UsuarioPermisoId that = (UsuarioPermisoId) o;
            return Objects.equals(usuario, that.usuario) && Objects.equals(permiso, that.permiso);
        }

        @Override
        public int hashCode() {
            return Objects.hash(usuario, permiso);
        }
    }
}
