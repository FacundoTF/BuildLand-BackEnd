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
    public static Specification<Presupuesto> fechaLimiteAfter(java.util.Date fecha) {
        return (root, query, builder) -> {
            if (fecha == null) {
                return builder.conjunction();
            }
            return builder.lessThanOrEqualTo(root.get("fechaLimite"), fecha);
        };
    }
    public static Specification<Presupuesto> hechoPor(String cuit) {
        return (root, query, builder) -> {
            if (cuit == null || cuit.isEmpty()) {
                return builder.conjunction();
            }
            return builder.equal(root.get("persona").get("cuit"), cuit);
        };
    }

    public static Specification<Presupuesto> noRechazadoPor(String cuit) {
        return (root, query, builder) -> {
            if (cuit == null || cuit.isEmpty()) {
                return builder.conjunction();
            }
            if (query == null) {
                return builder.conjunction();
            }
            // Subquery para obtener presupuestos rechazados por este CUIT
            var subquery = query.subquery(Long.class);
            var subqueryRoot = subquery.from(com.uai.BuildLand.models.presupuesto.PresupuestoRechazado.class);
            subquery.select(subqueryRoot.get("id").get("idPresupuesto"))
                    .where(builder.equal(subqueryRoot.get("id").get("cuit"), cuit));

            // Excluir presupuestos que están en la subquery
            return builder.not(root.get("id").in(subquery));
        };
    }
}
