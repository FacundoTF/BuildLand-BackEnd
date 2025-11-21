package com.uai.BuildLand.controllers;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import com.uai.BuildLand.models.PersonaFisica;
import com.uai.BuildLand.models.Usuario;
import com.uai.BuildLand.repositories.PersonaFisicaRepository;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.Objects;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controlador REST para manejar las operaciones CRUD de personas físicas
 */
@RestController
@RequestMapping("api/personas-fisicas")
@CrossOrigin(origins = "http://localhost:4200")
public class PersonaFisicaController {
    private final PersonaFisicaRepository personaFisicaRepository;

    public PersonaFisicaController(PersonaFisicaRepository personaFisicaRepository) {
        this.personaFisicaRepository = personaFisicaRepository;
    }

    /**
     * Obtiene una persona física por ID (CUIT)
     * 
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
    public ResponseEntity<PersonaFisica> crear(@RequestBody PersonaFisica personaFisica,
            UriComponentsBuilder ucb) {

        Optional<PersonaFisica> persona = Optional.ofNullable(personaFisica);

        if (persona.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        Optional<Usuario> user = Optional.ofNullable(personaFisica.getUsuario());

        if (user.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        if (persona.get().getCuit() == null || persona.get().getCuit().trim().isEmpty()
                || persona.get().getCuit().length() != 11) {
            return ResponseEntity.badRequest().build();
        }

        if (persona.get().getNombre() == null || persona.get().getNombre().trim().isEmpty()
                || persona.get().getNombre().length() > 100) {
            return ResponseEntity.badRequest().build();
        }

        if (persona.get().getCiudad() == null || persona.get().getCiudad().trim().isEmpty()
                || persona.get().getCiudad().length() > 100) {
            return ResponseEntity.badRequest().build();
        }

        if (persona.get().getCodigoPostal() == null || persona.get().getCodigoPostal().trim().isEmpty()
                || persona.get().getCodigoPostal().length() > 10) {
            return ResponseEntity.badRequest().build();
        }

        if (persona.get().getNumeroTelefono() == null || persona.get().getNumeroTelefono().trim().isEmpty()
                || persona.get().getNumeroTelefono().length() > 15) {
            return ResponseEntity.badRequest().build();
        }

        if (user.get().getEmail() == null || user.get().getEmail().trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        if (user.get().getPassword() == null || user.get().getPassword().trim().isEmpty()
                || user.get().getPassword().length() > 100) {
            return ResponseEntity.badRequest().build();
        }

        PersonaFisica personaAGuardar = persona.get();

        PersonaFisica personaGuardada = personaFisicaRepository.save(personaAGuardar);

        URI uri = ucb.path("api/personas-fisicas/{cuit}").buildAndExpand(personaGuardada.getCuit()).toUri();
        return ResponseEntity.created(uri).body(personaGuardada);
    }

    @PatchMapping("/")
    public ResponseEntity<PersonaFisica> actualizar(@RequestBody PersonaFisica personaFisica) {
        String nonNullCuit = Objects.requireNonNull(personaFisica.getCuit(), "cuit must not be null");
        Optional<PersonaFisica> personaFisicaExistenteOpt = personaFisicaRepository.findById(nonNullCuit);

        if (personaFisicaExistenteOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        PersonaFisica personaFisicaExistente = personaFisicaExistenteOpt.get();

        // Actualizar solo los campos de PersonaFisica sin tocar el Usuario
        if (personaFisica.getNombre() != null) {
            personaFisicaExistente.setNombre(personaFisica.getNombre());
        }
        if (personaFisica.getCiudad() != null) {
            personaFisicaExistente.setCiudad(personaFisica.getCiudad());
        }
        if (personaFisica.getCodigoPostal() != null) {
            personaFisicaExistente.setCodigoPostal(personaFisica.getCodigoPostal());
        }
        if (personaFisica.getNumeroTelefono() != null) {
            personaFisicaExistente.setNumeroTelefono(personaFisica.getNumeroTelefono());
        }

        // Si se proporciona información del usuario, actualizar solo email y password
        // manteniendo el ID del usuario existente
        if (personaFisica.getUsuario() != null) {
            Usuario usuarioExistente = personaFisicaExistente.getUsuario();
            Usuario usuarioActualizado = personaFisica.getUsuario();

            if (usuarioActualizado.getEmail() != null && !usuarioActualizado.getEmail().trim().isEmpty()) {
                usuarioExistente.setEmail(usuarioActualizado.getEmail());
            }
            if (usuarioActualizado.getPassword() != null && !usuarioActualizado.getPassword().trim().isEmpty()) {
                usuarioExistente.setPassword(usuarioActualizado.getPassword());
            }
        }

        PersonaFisica personaActualizada = personaFisicaRepository.save(personaFisicaExistente);
        return ResponseEntity.ok(personaActualizada);
    }

    /**
     * Elimina una persona física por CUIT
     * 
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
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/enlistarPorNombre")
    public ResponseEntity<List<PersonaFisica>> buscarPorNombre(Pageable pageable) {
        Page<PersonaFisica> personas = personaFisicaRepository.findAll(PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                pageable.getSortOr(Sort.by(Sort.Direction.ASC, "nombre"))));
        return ResponseEntity.ok(personas.getContent());
    }

    @GetMapping("/enlistarPorCuit")
    public ResponseEntity<List<PersonaFisica>> buscarPorCuit(Pageable pageable) {
        Page<PersonaFisica> personas = personaFisicaRepository.findAll(PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                pageable.getSortOr(Sort.by(Sort.Direction.ASC, "cuit"))));
        return ResponseEntity.ok(personas.getContent());
    }

}
