package com.uai.BuildLand.controllers.specifications;

import com.uai.BuildLand.models.access.Grupo;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public class GrupoSpecs {
    public static Specification<Grupo> filtrarGruposProhibidos() {
        List<String> nombresProhibidos = List.of("ADMIN", "PERSONA FISICA", "DISTRIBUIDORAS", "EMPLEADO");
        return (root, query, builder) -> root.get("nombre").in(nombresProhibidos).not();
    }
}
