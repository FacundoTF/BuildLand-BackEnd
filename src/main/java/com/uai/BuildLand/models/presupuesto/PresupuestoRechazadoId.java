package com.uai.BuildLand.models.presupuesto;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@EqualsAndHashCode
@Embeddable
public class PresupuestoRechazadoId implements Serializable {
    private static final long serialVersionUID = -5151152699701217151L;
    @Column(name = "usuario_cuit", nullable = false, length = 11)
    private String cuit;

    @Column(name = "id_presupuesto", nullable = false)
    private Long idPresupuesto;


}