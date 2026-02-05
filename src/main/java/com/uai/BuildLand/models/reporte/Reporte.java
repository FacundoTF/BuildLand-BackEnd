package com.uai.BuildLand.models.reporte;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter @Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class Reporte {
    Date fechaInicio;
    Date fechaFin;
}
