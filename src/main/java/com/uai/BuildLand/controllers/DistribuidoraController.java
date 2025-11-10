package com.uai.BuildLand.controllers;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import com.uai.BuildLand.models.Distribuidora;
import com.uai.BuildLand.repositories.DistribuidoraRepository;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.Objects;

/**
 * Controlador REST para manejar las operaciones CRUD de distribuidoras
 */
@RestController
@RequestMapping("api/distribuidoras")
@CrossOrigin(origins = "*")
public class DistribuidoraController {
    private final DistribuidoraRepository distribuidoraRepository;

    public DistribuidoraController(DistribuidoraRepository distribuidoraRepository) {
        this.distribuidoraRepository = distribuidoraRepository;
    }

    /**
     * Obtiene una distribuidora por ID (CUIT)
     * @param cuit CUIT de la distribuidora
     * @return Distribuidora encontrada o 404 si no existe
     */
    @GetMapping("/{cuit}")
    public ResponseEntity<Distribuidora> obtenerPorId(@PathVariable String cuit) {
        String nonNullCuit = Objects.requireNonNull(cuit, "cuit must not be null");
        Optional<Distribuidora> distribuidora = distribuidoraRepository.findById(nonNullCuit);
        if (distribuidora.isPresent()) {
            return ResponseEntity.ok(distribuidora.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Crea una nueva distribuidora
     * @param distribuidora Distribuidora a crear
     * @param ucb UriComponentsBuilder para construir la URI de la respuesta
     * @return Distribuidora creada con status 201
     */
    @PostMapping
    public ResponseEntity<Distribuidora> crear(@RequestBody Distribuidora distribuidora, UriComponentsBuilder ucb) {
        
        Optional<Distribuidora> distrib = Optional.ofNullable(distribuidora);

        if (distrib.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        if (distribuidora.GetCuit() == null || distribuidora.GetCuit().trim().isEmpty() || distribuidora.GetCuit().length() != 11) {
            return ResponseEntity.badRequest().build();
        }

        if (distribuidora.GetNombre() == null || distribuidora.GetNombre().trim().isEmpty() || distribuidora.GetNombre().length() > 100) {
            return ResponseEntity.badRequest().build();
        }

        if (distribuidora.GetCiudad() == null || distribuidora.GetCiudad().trim().isEmpty() || distribuidora.GetCiudad().length() > 100) {
            return ResponseEntity.badRequest().build();
        }

        if (distribuidora.GetDireccion() == null || distribuidora.GetDireccion().trim().isEmpty() || distribuidora.GetDireccion().length() > 200) {
            return ResponseEntity.badRequest().build();
        }

        if (distribuidora.GetCodigoPostal() == null || distribuidora.GetCodigoPostal().trim().isEmpty() || distribuidora.GetCodigoPostal().length() > 10) {
            return ResponseEntity.badRequest().build();
        }

        if (distribuidora.GetNumeroTelefono() == null || distribuidora.GetNumeroTelefono().trim().isEmpty() || distribuidora.GetNumeroTelefono().length() > 20) {
            return ResponseEntity.badRequest().build();
        }

        if (distribuidora.GetDescripcion() != null && !distribuidora.GetDescripcion().trim().isEmpty() && distribuidora.GetDescripcion().length() > 255) {
            return ResponseEntity.badRequest().build();
        }

        Distribuidora distribuidoraGuardada = distribuidoraRepository.save(distribuidora);

        URI uri = ucb.path("/distribuidoras/{cuit}").buildAndExpand(distribuidoraGuardada.GetCuit()).toUri();
        return ResponseEntity.created(uri).body(distribuidoraGuardada);
    }

    /**
     * Actualiza una distribuidora existente
     * @param distribuidora Datos actualizados
     * @return Distribuidora actualizada o 404 si no existe
     */
    @PatchMapping("/{cuit}")
    public ResponseEntity<Distribuidora> actualizar(@RequestBody Distribuidora distribuidora) {
        String nonNullCuit = Objects.requireNonNull(distribuidora.GetCuit(), "cuit must not be null");
        Optional<Distribuidora> distribuidoraDesactualizada = distribuidoraRepository.findById(nonNullCuit);
        if (distribuidoraDesactualizada.isPresent()) {
            distribuidoraRepository.save(distribuidora);
            return ResponseEntity.ok(distribuidora);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Elimina una distribuidora por CUIT
     * @param cuit CUIT de la distribuidora a eliminar
     * @return 204 si se eliminó correctamente, 404 si no existía
     */
    @DeleteMapping("/{cuit}")
    public ResponseEntity<Void> eliminar(@PathVariable String cuit) {
        // Ensure cuit is non-null for repositories that expect @NonNull parameters
        String nonNullCuit = Objects.requireNonNull(cuit, "cuit must not be null");

        boolean distribuidoraExiste = distribuidoraRepository.existsById(nonNullCuit);

        if (distribuidoraExiste) {
            distribuidoraRepository.deleteById(nonNullCuit);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Lista todas las distribuidoras con paginación
     * @param pageable Parámetros de paginación y ordenamiento
     * @return Lista de distribuidoras
     */
    @GetMapping("/enlistar")
    public ResponseEntity<List<Distribuidora>> enlistar(Pageable pageable) {
        Page<Distribuidora> distribuidoras = distribuidoraRepository.findAll(PageRequest.of(
            pageable.getPageNumber(),
            pageable.getPageSize(),
            pageable.getSortOr(Sort.by(Sort.Direction.ASC, "nombre"))));
        return ResponseEntity.ok(distribuidoras.getContent());
    }
}
