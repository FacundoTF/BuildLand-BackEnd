package com.uai.BuildLand.controllers;

import com.uai.BuildLand.controllers.DTOs.DetallePresupuesto_Producto_DTO;
import com.uai.BuildLand.models.*;
import com.uai.BuildLand.repositories.MarcaRepository;
import com.uai.BuildLand.repositories.PresupuestoRepository;
import com.uai.BuildLand.repositories.ProductoRepository;
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
@RequestMapping("api/presupuestos")
@CrossOrigin(origins = "*")
public class PresupuestoController {

    private final PresupuestoRepository presupuestoRepository;
    private final ProductoRepository productoRepository;
    private final MarcaRepository marcaRepository;

    public PresupuestoController(PresupuestoRepository presupuestoRepository, ProductoRepository productoRepository, MarcaRepository marcaRepository) {
        this.presupuestoRepository = presupuestoRepository;
        this.productoRepository = productoRepository;
        this.marcaRepository = marcaRepository;
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

        for (DetallePresupuesto detalle : presupuesto.getDetalles()) {
            if (detalle.getProducto() == null) {
                return ResponseEntity.badRequest().build();
            }else {
                Optional<Producto> producto = productoRepository.findByNombre((detalle.getProducto().getNombre()));
                Optional<Marca> marca = marcaRepository.findByNombre((detalle.getProducto().getMarca().getNombre()));
                if(producto.isEmpty() || marca.isEmpty()) {
                    return ResponseEntity.badRequest().build();
                }
                detalle.setProducto(producto.get());
                detalle.getProducto().setMarca(marca.get());
                boolean valido = verificarDatosInternos(detalle.getProducto(), detalle);

                if (!valido) {
                    return ResponseEntity.badRequest().build();
                }
            }
            detalle.setPresupuesto(presupuesto);
        }

        presupuesto.setFecha(new Date());
        presupuesto.setCotizaciones(new ArrayList<Cotizacion>());

        Presupuesto presupuestoGuardado = presupuestoRepository.save(presupuesto);
        URI uri = ucb.path("api/presupuestos/{id}").buildAndExpand(presupuestoGuardado.getId()).toUri();
        return ResponseEntity.created(uri).body(presupuestoGuardado);
    }

    private boolean verificarDatosInternos(Producto producto, DetallePresupuesto detallePresupuesto) {
        if (detallePresupuesto.getProducto().getMarca() == null) {
            return false;
        }

        if (detallePresupuesto.getCantidad() <= 0) {
            return false;
        }

        if(detallePresupuesto.getProducto() == null) {
            return false;
        }

        return true;
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
