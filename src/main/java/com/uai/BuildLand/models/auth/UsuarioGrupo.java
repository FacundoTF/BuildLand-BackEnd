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
    @JoinColumn(name = "cuit", nullable = false, referencedColumnName = "cuit", columnDefinition = "varchar(11)")
    private Usuario cuit;

    @Id
    @ManyToOne
    @JoinColumn(name = "id_grupo", nullable = false)
    private Grupo grupo;
    
    @Column(name = "fecha_asignacion")
    private LocalDateTime fechaAsignacion;

    @PrePersist
    protected void onCreate() {
        this.fechaAsignacion = LocalDateTime.now();
    }

    // Constructores
    public UsuarioGrupo() {
        this.fechaAsignacion = LocalDateTime.now();
    }

    public UsuarioGrupo(Usuario usuario, Grupo grupo) {
        this.cuit = usuario;
        this.grupo = grupo;
        this.fechaAsignacion = LocalDateTime.now();
    }

    // Getters y Setters
    public Usuario getUsuario() {
        return cuit;
    }

    public void setUsuario(Usuario usuario) {
        this.cuit = usuario;
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
        return Objects.equals(cuit, that.cuit) && Objects.equals(grupo, that.grupo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cuit, grupo);
    }

    /**
     * Clase interna para la clave compuesta de UsuarioGrupo.
     * Necesaria para @IdClass que permite usar múltiples @Id en JPA.
     */
    public static class UsuarioGrupoId implements Serializable {
        private String cuit;
        private Long grupo;

        public UsuarioGrupoId() {}

        public UsuarioGrupoId(String cuit, Long grupo) {
            this.cuit = cuit;
            this.grupo = grupo;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            UsuarioGrupoId that = (UsuarioGrupoId) o;
            return Objects.equals(cuit, that.cuit) && Objects.equals(grupo, that.grupo);
        }

        @Override
        public int hashCode() {
            return Objects.hash(cuit, grupo);
        }
    }
}
