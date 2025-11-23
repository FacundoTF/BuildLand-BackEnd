package com.uai.BuildLand.models.auth;

import com.uai.BuildLand.models.access.Grupo;
import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entidad intermedia que representa la relación muchos-a-muchos entre Usuario y Grupo.
 * Cada registro indica que un usuario pertenece a un grupo específico.
 */
@Entity
@Table(name = "usuario_grupo")
@IdClass(UsuarioGrupo.UsuarioGrupoId.class)
public class UsuarioGrupo {
    
    @Id
    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @Id
    @ManyToOne
    @JoinColumn(name = "id_grupo", nullable = false)
    private Grupo grupo;
    
    @Column(name = "fecha_asignacion")
    private LocalDateTime fechaAsignacion;

    // Constructores
    public UsuarioGrupo() {
        this.fechaAsignacion = LocalDateTime.now();
    }

    public UsuarioGrupo(Usuario usuario, Grupo grupo) {
        this.usuario = usuario;
        this.grupo = grupo;
        this.fechaAsignacion = LocalDateTime.now();
    }

    // Getters y Setters
    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Grupo getGrupo() {
        return grupo;
    }

    public void setGrupo(Grupo grupo) {
        this.grupo = grupo;
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
        UsuarioGrupo that = (UsuarioGrupo) o;
        return Objects.equals(usuario, that.usuario) && Objects.equals(grupo, that.grupo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(usuario, grupo);
    }

    /**
     * Clase interna para la clave compuesta de UsuarioGrupo.
     * Necesaria para @IdClass que permite usar múltiples @Id en JPA.
     */
    public static class UsuarioGrupoId implements Serializable {
        private Long usuario;
        private Long grupo;

        public UsuarioGrupoId() {}

        public UsuarioGrupoId(Long usuario, Long grupo) {
            this.usuario = usuario;
            this.grupo = grupo;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            UsuarioGrupoId that = (UsuarioGrupoId) o;
            return Objects.equals(usuario, that.usuario) && Objects.equals(grupo, that.grupo);
        }

        @Override
        public int hashCode() {
            return Objects.hash(usuario, grupo);
        }
    }
}
