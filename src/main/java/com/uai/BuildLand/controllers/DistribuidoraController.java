package com.uai.BuildLand.controllers;

import com.uai.BuildLand.models.auth.Usuario;
import com.uai.BuildLand.repositories.UsuarioRepository;
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
    private final UsuarioRepository usuarioRepository;

    public DistribuidoraController(DistribuidoraRepository distribuidoraRepository, UsuarioRepository usuarioRepository) {
        this.distribuidoraRepository = distribuidoraRepository;
        this.usuarioRepository = usuarioRepository;
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

        boolean validarDatos = verificarDatosInternos(distribuidora);
        boolean existePorNombre = distribuidoraRepository.existsByNombreIgnoreCase(distribuidora.getNombre());
        boolean existePorCuit = distribuidoraRepository.existsById(distribuidora.getCuit());
        Optional<Usuario> existePorMail = usuarioRepository.findByEmail(distribuidora.getUsuario().getEmail());

        if (existePorNombre || existePorCuit || existePorMail.isPresent()) {
            return ResponseEntity.badRequest().build();
        }

        if (!validarDatos) {
            return ResponseEntity.badRequest().build();
        }

        try {
            // El cascade guardará el usuario automáticamente
            Distribuidora distribuidoraGuardada = distribuidoraRepository.save(distribuidora);
            URI uri = ucb.path("/distribuidoras/{cuit}").buildAndExpand(distribuidoraGuardada.getCuit()).toUri();
            return ResponseEntity.created(uri).body(distribuidoraGuardada);
        }catch(Exception e){
            return ResponseEntity.badRequest().build();
        }
    }

    private boolean verificarDatosInternos(Distribuidora distribuidora) {
        if (distribuidora.getCuit() == null || distribuidora.getCuit().trim().isEmpty() || distribuidora.getCuit().length() != 11) {
            return false;
        }

        if (distribuidora.getNombre() == null || distribuidora.getNombre().trim().isEmpty() || distribuidora.getNombre().length() > 100) {
            return false;
        }

        if (distribuidora.getCiudad() == null || distribuidora.getCiudad().trim().isEmpty() || distribuidora.getCiudad().length() > 100) {
            return false;
        }

        if (distribuidora.getDireccion() == null || distribuidora.getDireccion().trim().isEmpty() || distribuidora.getDireccion().length() > 200) {
            return false;
        }

        if (distribuidora.getCodigoPostal() == null || distribuidora.getCodigoPostal().trim().isEmpty() || distribuidora.getCodigoPostal().length() > 10) {
            return false;
        }

        if (distribuidora.getNumeroTelefono() == null || distribuidora.getNumeroTelefono().trim().isEmpty() || distribuidora.getNumeroTelefono().length() > 20) {
            return false;
        }

        if (distribuidora.getDescripcion() != null && !distribuidora.getDescripcion().trim().isEmpty() && distribuidora.getDescripcion().length() > 255) {
            return false;
        }
        return true;
    }

    /**
     * Actualiza una distribuidora existente
     * @param distribuidora Datos actualizados
     * @return Distribuidora actualizada o 404 si no existe
     */
    @PatchMapping()
    public ResponseEntity<Distribuidora> actualizar(@RequestBody Distribuidora distribuidora) {
        String nonNullCuit = Objects.requireNonNull(distribuidora.getCuit(), "cuit must not be null");
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
