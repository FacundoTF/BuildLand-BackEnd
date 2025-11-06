package com.uai.BuildLand.controllers.DTOs;

import com.uai.BuildLand.models.PersonaFisica;
import com.uai.BuildLand.models.Usuario;

public class PersonaFisicaCreacionDTO {
    private PersonaFisica personaFisica;
    private Usuario usuario;

    public PersonaFisica getPersonaFisica() {
        return personaFisica;
    }

    public void setPersonaFisica(PersonaFisica personaFisica) {
        this.personaFisica = personaFisica;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}
