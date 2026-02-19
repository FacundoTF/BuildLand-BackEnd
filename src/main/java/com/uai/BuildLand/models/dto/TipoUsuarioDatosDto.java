package com.uai.BuildLand.models.dto;

import com.uai.BuildLand.models.Categoria;
import com.uai.BuildLand.models.auth.Usuario;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter @Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class TipoUsuarioDatosDto {
    private String nombre;
    private String ciudad;
    private String codigoPostal;
    private String numeroTelefono;
    private String direccion;
    private String descripcion;
    private Usuario usuario;
    private List<Categoria> categorias;
}
