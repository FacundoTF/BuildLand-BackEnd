package com.uai.BuildLand.controllers.specifications;

import com.uai.BuildLand.models.cotizacion.Cotizacion;
import org.springframework.data.jpa.domain.Specification;

public class CotizacionSpecs {
    public static Specification<Cotizacion> filtrarPorIdPresupuesto(Long idPresupuesto) {
        return (root, query, builder) -> {
            if (idPresupuesto == null) {
                return builder.conjunction();
            }
            return builder.equal(root.get("presupuesto").get("id"), idPresupuesto);
        };
    }
}
