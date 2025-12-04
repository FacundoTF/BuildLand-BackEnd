package com.uai.BuildLand.models.dto;

import com.uai.BuildLand.models.auth.AuthResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthUserInfoDto {
    private AuthResponse authResponse;
    private List<String> grupos;
    private List<String> permisos;

    public AuthUserInfoDto(List<String> grupos, List<String> permisos) {
        this.grupos = grupos;
        this.permisos = permisos;
        this.authResponse = null;
    }
}
