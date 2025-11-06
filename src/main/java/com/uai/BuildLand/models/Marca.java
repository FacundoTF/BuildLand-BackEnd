package com.uai.BuildLand.models;

// import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
// import jakarta.persistence.GeneratedValue;
// import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "marcas")
public class Marca {
    // @Id
    // @GeneratedValue(strategy = GenerationType.IDENTITY)
    // @Column(name = "id", nullable = false, updatable = false)
    // private Long idMarca;

    private enum marcas {
        GENERICA,
        NO_GENERICA
    }

    @Id
    @Enumerated(EnumType.STRING)
    private marcas tipoMarca;

    public marcas getTipoMarca() {
        return tipoMarca;
    }

    public void setTipoMarca(marcas tipoMarca) {
        this.tipoMarca = tipoMarca;
    }

    public Marca() {
        this.tipoMarca = marcas.GENERICA;
    }

    public Marca(String tipoMarca) {
        try {
            this.tipoMarca = marcas.valueOf(tipoMarca);
        } catch (IllegalArgumentException e) {
            this.tipoMarca = marcas.GENERICA;
        } catch (NullPointerException e2) {
            this.tipoMarca = marcas.GENERICA;
        }
    }

}
