package com.uai.BuildLand.controllers;

import com.uai.BuildLand.models.Cotizacion;
import com.uai.BuildLand.repositories.CotizacionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/cotizaciones")
@CrossOrigin(origins = "*")
public class CotizacionController {

    private final CotizacionRepository cotizacionRepository;

    public CotizacionController(CotizacionRepository cotizacionRepository) {
        this.cotizacionRepository = cotizacionRepository;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cotizacion> obtenerPorId(@PathVariable Long id) {
        Optional<Cotizacion> cotizacion = cotizacionRepository.findById(id);
        return cotizacion.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Cotizacion> crear(@RequestBody Cotizacion cotizacion, UriComponentsBuilder ucb) {
        if (cotizacion.getPresupuesto() == null || cotizacion.getDistribuidora() == null) {
            return ResponseEntity.badRequest().build();
        }

        cotizacion.setFecha(new Date());
        cotizacion.getDetalles().forEach(detalle -> detalle.setCotizacion(cotizacion));

        Cotizacion cotizacionGuardada = cotizacionRepository.save(cotizacion);
        URI uri = ucb.path("/cotizaciones/{id}").buildAndExpand(cotizacionGuardada.getId()).toUri();
        return ResponseEntity.created(uri).body(cotizacionGuardada);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Cotizacion> actualizar(@PathVariable Long id, @RequestBody Cotizacion cotizacionActualizada) {
        Optional<Cotizacion> cotizacionExistenteOpt = cotizacionRepository.findById(id);
        if (cotizacionExistenteOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Cotizacion cotizacionExistente = cotizacionExistenteOpt.get();

        if (cotizacionActualizada.getFecha() != null) {
            cotizacionExistente.setFecha(cotizacionActualizada.getFecha());
        }

        if (cotizacionActualizada.getDetalles() != null && !cotizacionActualizada.getDetalles().isEmpty()) {
            cotizacionExistente.getDetalles().clear();
            cotizacionActualizada.getDetalles().forEach(detalle -> {
                detalle.setCotizacion(cotizacionExistente);
                cotizacionExistente.getDetalles().add(detalle);
            });
        }

        Cotizacion cotizacionGuardada = cotizacionRepository.save(cotizacionExistente);
        return ResponseEntity.ok(cotizacionGuardada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (cotizacionRepository.existsById(id)) {
            cotizacionRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/enlistar")
    public ResponseEntity<List<Cotizacion>> enlistar(Pageable pageable) {
        Page<Cotizacion> cotizaciones = cotizacionRepository.findAll(PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                pageable.getSortOr(Sort.by(Sort.Direction.DESC, "fecha"))));
        return ResponseEntity.ok(cotizaciones.getContent());
    }
}