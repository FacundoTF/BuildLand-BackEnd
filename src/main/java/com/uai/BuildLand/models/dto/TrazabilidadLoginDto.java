package com.uai.BuildLand.models.dto;

import com.uai.BuildLand.models.auth.Usuario;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "trazabilidad_login")
@Getter @Setter
@RequiredArgsConstructor @AllArgsConstructor
public class TrazabilidadLoginDto {
    @Id
    private String cuit;

    @OneToOne
    @MapsId
    @JoinColumn(name = "cuit")
    private Usuario usuario;

    private String nombreUsuario;
    private Usuario.TipoUsuario tipoUsuario;

    private Date fecha;
    private AccionUsuario accion;

    public enum AccionUsuario {
        LOGIN,
        LOGOUT
    }
}
