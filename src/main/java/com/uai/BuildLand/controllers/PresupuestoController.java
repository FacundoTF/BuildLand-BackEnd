package com.uai.BuildLand.controllers;

import com.uai.BuildLand.models.Presupuesto;
import com.uai.BuildLand.repositories.PresupuestoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/presupuestos")
@CrossOrigin(origins = "*")
public class PresupuestoController {

    private final PresupuestoRepository presupuestoRepository;

    public PresupuestoController(PresupuestoRepository presupuestoRepository) {
        this.presupuestoRepository = presupuestoRepository;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Presupuesto> obtenerPorId(@PathVariable Long id) {
        Optional<Presupuesto> presupuesto = presupuestoRepository.findById(id);
        return presupuesto.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Presupuesto> crear(@RequestBody Presupuesto presupuesto, UriComponentsBuilder ucb) {
        if (presupuesto.getPersona() == null || presupuesto.getCategoria() == null) {
            return ResponseEntity.badRequest().build();
        }

        presupuesto.setFecha(new Date());
        if (presupuesto.getDetalles() != null) {
            presupuesto.getDetalles().forEach(detalle -> detalle.setPresupuesto(presupuesto));
        }
        presupuesto.setCotizaciones(new ArrayList<>());

        Presupuesto presupuestoGuardado = presupuestoRepository.save(presupuesto);
        URI uri = ucb.path("/presupuestos/{id}").buildAndExpand(presupuestoGuardado.getId()).toUri();
        return ResponseEntity.created(uri).body(presupuestoGuardado);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Presupuesto> actualizar(@PathVariable Long id, @RequestBody Presupuesto presupuestoActualizado) {
        Optional<Presupuesto> presupuestoExistenteOpt = presupuestoRepository.findById(id);
        if (presupuestoExistenteOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Presupuesto presupuestoExistente = presupuestoExistenteOpt.get();

        if (presupuestoActualizado.getPersona() != null) {
            presupuestoExistente.setPersona(presupuestoActualizado.getPersona());
        }
        if (presupuestoActualizado.getCategoria() != null) {
            presupuestoExistente.setCategoria(presupuestoActualizado.getCategoria());
        }
        if (presupuestoActualizado.getFecha() != null) {
            presupuestoExistente.setFecha(presupuestoActualizado.getFecha());
        }
        if (presupuestoActualizado.getDetalles() != null && !presupuestoActualizado.getDetalles().isEmpty()) {
            presupuestoExistente.getDetalles().clear();
            presupuestoActualizado.getDetalles().forEach(detalle -> {
                detalle.setPresupuesto(presupuestoExistente);
                presupuestoExistente.getDetalles().add(detalle);
            });
        }

        Presupuesto presupuestoGuardado = presupuestoRepository.save(presupuestoExistente);
        return ResponseEntity.ok(presupuestoGuardado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (presupuestoRepository.existsById(id)) {
            presupuestoRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/enlistar")
    public ResponseEntity<List<Presupuesto>> enlistar(Pageable pageable) {
        Page<Presupuesto> presupuestos = presupuestoRepository.findAll(PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                pageable.getSortOr(Sort.by(Sort.Direction.DESC, "fecha"))));
        return ResponseEntity.ok(presupuestos.getContent());
    }
}
