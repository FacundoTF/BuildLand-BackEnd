package com.uai.BuildLand.controllers.specifications;

import com.uai.BuildLand.models.Categoria;
import com.uai.BuildLand.models.presupuesto.Presupuesto;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public class PresupuestoSpecs {

    public static Specification<Presupuesto> categoriasIn(List<Categoria> categorias) {
        return (root, query, builder) -> {
            if (categorias == null || categorias.isEmpty()) {
                return builder.conjunction();
            }
            return root.get("categoria").in(categorias);
        };
    }
    public static Specification<Presupuesto> fechaLimiteBefore(java.util.Date fecha) {
        return (root, query, builder) -> {
            if (fecha == null) {
                return builder.conjunction();
            }
            return builder.greaterThanOrEqualTo(root.get("fechaLimite"), fecha);
        };
    }
}
