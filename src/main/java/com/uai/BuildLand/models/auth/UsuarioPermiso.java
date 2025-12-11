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
    @JoinColumn(name = "cuit", nullable = false, referencedColumnName = "cuit", columnDefinition = "varchar(11)")
    private Usuario cuit;

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
        this.cuit = usuario;
        this.permiso = permiso;
        this.fechaAsignacion = LocalDateTime.now();
    }

    // Getters y Setters
    public Usuario getUsuario() {
        return cuit;
    }

    public void setUsuario(Usuario usuario) {
        this.cuit = usuario;
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
        return Objects.equals(cuit, that.cuit) && Objects.equals(permiso, that.permiso);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cuit, permiso);
    }

    /**
     * Clase interna para la clave compuesta de UsuarioPermiso.
     * Necesaria para @IdClass que permite usar múltiples @Id en JPA.
     */
    public static class UsuarioPermisoId implements Serializable {
        private String cuit;
        private Long permiso;

        public UsuarioPermisoId() {}

        public UsuarioPermisoId(String cuit, Long permiso) {
            this.cuit = cuit;
            this.permiso = permiso;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            UsuarioPermisoId that = (UsuarioPermisoId) o;
            return Objects.equals(cuit, that.cuit) && Objects.equals(permiso, that.permiso);
        }

        @Override
        public int hashCode() {
            return Objects.hash(cuit, permiso);
        }
    }
}
