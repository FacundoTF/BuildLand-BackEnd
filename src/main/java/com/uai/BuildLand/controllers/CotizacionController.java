package com.uai.BuildLand.controllers;

import com.uai.BuildLand.models.Distribuidora;
import com.uai.BuildLand.models.Marca;
import com.uai.BuildLand.models.Producto;
import com.uai.BuildLand.models.cotizacion.Cotizacion;
import com.uai.BuildLand.models.cotizacion.DetalleCotizacion;
import com.uai.BuildLand.repositories.*;
import com.uai.BuildLand.services.JwtTokenService;
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
import java.util.Optional;

@RestController
@RequestMapping("api/cotizaciones")
@CrossOrigin(origins = "*")
public class CotizacionController {

    private final CotizacionRepository cotizacionRepository;
    private final JwtTokenService jwtTokenService;
    private final DistribuidoraRepository distribuidoraRepository;
    private final PresupuestoRepository presupuestoRepository;
    private final ProductoRepository productoRepository;

    public CotizacionController(CotizacionRepository cotizacionRepository, JwtTokenService jwtTokenService, DistribuidoraRepository distribuidoraRepository, PresupuestoRepository presupuestoRepository, ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
        this.presupuestoRepository = presupuestoRepository;
        this.distribuidoraRepository = distribuidoraRepository;
        this.jwtTokenService = jwtTokenService;
        this.cotizacionRepository = cotizacionRepository;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cotizacion> obtenerPorId(@PathVariable Long id) {
        Optional<Cotizacion> cotizacion = cotizacionRepository.findById(id);
        return cotizacion.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/{idPresupuesto}")
    public ResponseEntity<Cotizacion> crear(@RequestBody Cotizacion cotizacion, @PathVariable Long idPresupuesto, @CookieValue (name = "accessToken") String jwtToken, UriComponentsBuilder ucb) {
        String correo = jwtTokenService.extractUsername(jwtToken);
        Optional<Distribuidora> distribuidoraOpt = distribuidoraRepository.findByUsuario_Email(correo);
        if (distribuidoraOpt.isEmpty()) {
            return ResponseEntity.status(403).build();
        }
        for (DetalleCotizacion detalle : cotizacion.getDetalles()) {
            Optional<Producto> productoOpt = productoRepository.findByNombre(detalle.getProducto().getNombre());
            if (productoOpt.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            detalle.setProducto(productoOpt.get());
            detalle.setCotizacion(cotizacion);
        }
        cotizacion.setPresupuesto(presupuestoRepository.findById(idPresupuesto).orElse(null));
        cotizacion.setDistribuidora(distribuidoraOpt.get());
        if (cotizacion.getPresupuesto() == null || cotizacion.getDistribuidora() == null) {
            return ResponseEntity.badRequest().build();
        }
        cotizacion.setFecha(new Date());
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