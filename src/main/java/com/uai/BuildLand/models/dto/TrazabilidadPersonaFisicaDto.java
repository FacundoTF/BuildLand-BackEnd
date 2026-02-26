package com.uai.BuildLand.models.dto;

import com.uai.BuildLand.models.auth.Usuario;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "trazabilidad_persona_fisica")
@Getter @Setter
@RequiredArgsConstructor @AllArgsConstructor
public class TrazabilidadPersonaFisicaDto {
    @Id
    private String cuit;

    @OneToOne
    @MapsId
    @JoinColumn(name = "cuit")
    private Usuario usuario;

    private String nombre;
    private String numeroTelefono;
    private String codigoPostal;
    private String ciudad;

    private String email;
    private String password;
    private boolean estado;

    @Enumerated(EnumType.STRING)
    private TipoAccion accion;
    private Date fechaAccion;

    public enum TipoAccion {
        REGISTRO,
        MODIFICACION,
        ELIMINACION
    }
}
