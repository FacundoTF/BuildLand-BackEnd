package com.uai.BuildLand.models.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponseDto {
    private String username;
    private List<String> grupos;
    private List<String> permisos;
}
