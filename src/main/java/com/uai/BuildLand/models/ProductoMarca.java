package com.uai.BuildLand.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entidad intermedia que representa la relación muchos-a-muchos entre Producto y Marca.
 * Cada registro indica que un producto está asociado a una marca específica.
 */
@Entity
@Table(name = "producto_marca")
@IdClass(ProductoMarca.ProductoMarcaId.class)
@Setter
@Getter
public class ProductoMarca {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_producto_marca", nullable = false, updatable = false)
    private Long idProductoMarca;

    @ManyToOne
    @JoinColumn(name = "id_producto", nullable = false, referencedColumnName = "id_producto")
    private Producto producto;

    @ManyToOne
    @JoinColumn(name = "id_marca", nullable = false, referencedColumnName = "id_marca")
    private Marca marca;

    // Constructores
    public ProductoMarca() {
    }

    public ProductoMarca(Producto producto, Marca marca, Long idProductoMarca) {
        this.idProductoMarca = idProductoMarca;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductoMarca that = (ProductoMarca) o;
        return Objects.equals(idProductoMarca, that.idProductoMarca);
    }

    @Override
    public int hashCode() {
        return Objects.hash(producto, marca, idProductoMarca);
    }

    /**
     * Clase interna para la clave compuesta de ProductoMarca.
     * Necesaria para @IdClass que permite usar múltiples @Id en JPA.
     */
    public static class ProductoMarcaId implements Serializable {
        private Long idProductoMarca;

        public ProductoMarcaId() {}

        public ProductoMarcaId(Long producto, Long marca, Long idProductoMarca) {
            this.idProductoMarca = idProductoMarca;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ProductoMarcaId that = (ProductoMarcaId) o;
            return Objects.equals(idProductoMarca, that.idProductoMarca);
        }

        @Override
        public int hashCode() {
            return Objects.hash(idProductoMarca);
        }
    }
}
