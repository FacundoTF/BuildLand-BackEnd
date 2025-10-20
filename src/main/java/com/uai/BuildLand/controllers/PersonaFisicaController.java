package com.uai.BuildLand.controllers;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import com.uai.BuildLand.models.PersonaFisica;
import com.uai.BuildLand.repositories.PersonaFisicaRepository;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.Objects;

/**
 * Controlador REST para manejar las operaciones CRUD de personas físicas
 */
@RestController
@RequestMapping("/personas-fisicas")
@CrossOrigin(origins = "*")
public class PersonaFisicaController {
    private final PersonaFisicaRepository personaFisicaRepository;

    public PersonaFisicaController(PersonaFisicaRepository personaFisicaRepository) {
        this.personaFisicaRepository = personaFisicaRepository;
    }
    
        
    /**
     * Obtiene una persona física por ID (CUIT)
     * @param cuit CUIT de la persona física
     * @return Persona física encontrada o 404 si no existe
     */
    @GetMapping("/{cuit}")
    public ResponseEntity<PersonaFisica> obtenerPorId(@PathVariable String cuit) {
        String nonNullCuit = Objects.requireNonNull(cuit, "cuit must not be null");
        Optional<PersonaFisica> personaFisica = personaFisicaRepository.findById(nonNullCuit);
        if (personaFisica.isPresent()) {
            return ResponseEntity.ok(personaFisica.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<PersonaFisica> crear(@RequestBody PersonaFisica personaFisica, UriComponentsBuilder ucb) {
        
        Optional<PersonaFisica> persona = Optional.ofNullable(personaFisica);

        if(persona.isEmpty()){
            return ResponseEntity.badRequest().build();
        }

        if(personaFisica.getCuit() == null || personaFisica.getCuit().trim().isEmpty() || personaFisica.getCuit().length() != 11){
            return ResponseEntity.badRequest().build();
        }

        if(personaFisica.getNombre() == null || personaFisica.getNombre().trim().isEmpty() || personaFisica.getNombre().length() > 100){
            return ResponseEntity.badRequest().build();
        }

        if(personaFisica.getCiudad() == null || personaFisica.getCiudad().trim().isEmpty() || personaFisica.getCiudad().length() > 100){
            return ResponseEntity.badRequest().build();
        }

        if(personaFisica.getDireccion() == null || personaFisica.getDireccion().trim().isEmpty() || personaFisica.getDireccion().length() > 200){
            return ResponseEntity.badRequest().build();
        }

        if(personaFisica.getCodigoPostal() == null || personaFisica.getCodigoPostal().trim().isEmpty() || personaFisica.getCodigoPostal().length() > 10){
            return ResponseEntity.badRequest().build();
        }

        if(personaFisica.getNumeroTelefono() == null || personaFisica.getNumeroTelefono().trim().isEmpty() || personaFisica.getNumeroTelefono().length() > 15){
            return ResponseEntity.badRequest().build();
        }

        PersonaFisica personaGuardada = personaFisicaRepository.save(personaFisica);

        URI uri = ucb.path("/personas-fisicas/{cuit}").buildAndExpand(personaGuardada.getCuit()).toUri();
        return ResponseEntity.created(uri).body(personaGuardada);
    }
    
    /**
     * Actualiza una persona física existente
     * @param cuit CUIT de la persona física a actualizar
     * @param personaFisica Datos actualizados
     * @return Persona física actualizada o 404 si no existe
     */
    @PatchMapping("/{cuit}")
    public ResponseEntity<PersonaFisica> actualizar(@RequestBody PersonaFisica personaFisica) {
        String nonNullCuit = Objects.requireNonNull(personaFisica.getCuit(), "cuit must not be null");
        Optional<PersonaFisica> personaFisicaDesactualizada = personaFisicaRepository.findById(nonNullCuit);
        if (personaFisicaDesactualizada.isPresent()) {
                personaFisicaRepository.save(personaFisica);
                return ResponseEntity.ok(personaFisica);
            } else {
                return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Elimina una persona física por CUIT
     * @param cuit CUIT de la persona física a eliminar
     * @return 204 si se eliminó correctamente, 404 si no existía
     */
    @DeleteMapping("/{cuit}")
    public ResponseEntity<Void> eliminar(@PathVariable String cuit) {
        // Ensure cuit is non-null for repositories that expect @NonNull parameters
        String nonNullCuit = Objects.requireNonNull(cuit, "cuit must not be null");

        boolean personaExiste = personaFisicaRepository.existsById(nonNullCuit);

        if (personaExiste) {
            personaFisicaRepository.deleteById(nonNullCuit);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Busca distribuidoras por nombre
     * @param nombre Texto a buscar en el nombre
     * @return Lista de distribuidoras que contienen el texto
     */
    @GetMapping("/enlistar")
    public ResponseEntity<List<PersonaFisica>> buscarPorNombre(Pageable pageable) {
        Page<PersonaFisica> personas = personaFisicaRepository.findAll(PageRequest.of(
            pageable.getPageNumber(), 
            pageable.getPageSize(),
            pageable.getSortOr(Sort.by(Sort.Direction.ASC, "nombre"))));
        return ResponseEntity.ok(personas.getContent());
    }
}
