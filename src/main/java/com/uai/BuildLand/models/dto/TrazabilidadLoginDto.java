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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String cuit;

    private String nombreUsuario;
    @Enumerated(EnumType.STRING)
    private Usuario.TipoUsuario tipoUsuario;

    private Date fecha;

    @Enumerated(EnumType.STRING)
    private AccionUsuario accion;

    public enum AccionUsuario {
        LOGIN,
        LOGOUT
    }
}
