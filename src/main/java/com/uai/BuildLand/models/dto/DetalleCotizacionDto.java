package com.uai.BuildLand.models.dto;

public class DetalleCotizacionDto {
    private Long idProducto;
    private String nombreProducto;
    private String nombreMarca;
    private String nombreCategoria;
    private double importe;

    public DetalleCotizacionDto() {
    }

    public DetalleCotizacionDto(Long idProducto, String nombreProducto, String nombreMarca, String nombreCategoria, double importe) {
        this.idProducto = idProducto;
        this.nombreProducto = nombreProducto;
        this.nombreMarca = nombreMarca;
        this.nombreCategoria = nombreCategoria;
        this.importe = importe;
    }

    public Long getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(Long idProducto) {
        this.idProducto = idProducto;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

    public String getNombreMarca() {
        return nombreMarca;
    }

    public void setNombreMarca(String nombreMarca) {
        this.nombreMarca = nombreMarca;
    }

    public String getNombreCategoria() {
        return nombreCategoria;
    }

    public void setNombreCategoria(String nombreCategoria) {
        this.nombreCategoria = nombreCategoria;
    }

    public double getImporte() {
        return importe;
    }

    public void setImporte(double importe) {
        this.importe = importe;
    }
}

