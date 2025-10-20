package com.uai.BuildLand.models;

/**
 * Entidad que representa una persona física en el sistema BuildLand
 */
public class PersonaFisica {
    private Long id_usuario;

    private Usuario usuario;

    private String cuit;

    private String nombre;

    private String numeroTelefono;

    private String direccion;

    private String codigoPostal;

    private String ciudad;

    /**
     * Constructor por defecto.
     */
    public PersonaFisica() {
    }

    // Constructor con parámetros
    public PersonaFisica(Usuario usuario, String cuit, String nombre, String numeroTelefono, String direccion, String codigoPostal, String ciudad) {
        this.usuario = usuario;
        this.cuit = cuit;
        this.nombre = nombre;
        this.numeroTelefono = numeroTelefono;
        this.direccion = direccion;
        this.codigoPostal = codigoPostal;
        this.ciudad = ciudad;
    }

    // Getters y Setters
    public Long getIdUsuario() {
        return id_usuario;
    }

    public void setIdUsuario(Long id_usuario) {
        this.id_usuario = id_usuario;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public String getCuit() {
        return cuit;
    }

    public void setCuit(String cuit) {
        this.cuit = cuit;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getNumeroTelefono() {
        return numeroTelefono;
    }

    public void setNumeroTelefono(String numeroTelefono) {
        this.numeroTelefono = numeroTelefono;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getCodigoPostal() {
        return codigoPostal;
    }

    public void setCodigoPostal(String codigoPostal) {
        this.codigoPostal = codigoPostal;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    @Override
    public String toString() {
        return "PersonaFisica{" +
                "id_usuario=" + id_usuario +
                ", usuario=" + (usuario != null ? usuario.getId() : "null") +
                ", cuit='" + cuit + '\'' +
                ", nombre='" + nombre + '\'' +
                ", numeroTelefono='" + numeroTelefono + '\'' +
                ", direccion='" + direccion + '\'' +
                ", codigoPostal='" + codigoPostal + '\'' +
                ", ciudad='" + ciudad + '\'' +
                '}';
    }
}
