package com.uai.BuildLand.controllers;

import com.uai.BuildLand.models.Distribuidora;
import com.uai.BuildLand.models.Producto;
import com.uai.BuildLand.models.cotizacion.Cotizacion;
import com.uai.BuildLand.models.cotizacion.CotizacionResumen;
import com.uai.BuildLand.models.cotizacion.DetalleCotizacion;
import com.uai.BuildLand.models.dto.CotizacionDto;
import com.uai.BuildLand.models.dto.DetalleCotizacionDto;
import com.uai.BuildLand.repositories.*;
import com.uai.BuildLand.services.JwtTokenService;
import org.springframework.http.HttpStatus;
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

        this.cotizacionRepository.notificarDesplazamiento(idPresupuesto, distribuidoraOpt.get().getUsuario().getEmail());

        URI uri = ucb.path("/cotizaciones/{id}").buildAndExpand(cotizacionGuardada.getId()).toUri();
        cotizacionGuardada.setPresupuesto(null);
        cotizacionGuardada.setDetalles(null);
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

    @GetMapping("/enlistar/{IdPresupuesto}")
    public ResponseEntity<List<CotizacionDto>> enlistar(@PathVariable Long IdPresupuesto) {
        // Obtener el resumen de las 3 mejores cotizaciones (una por distribuidora)
        List<CotizacionResumen> resumen = cotizacionRepository.findTop3ByPresupuestoIdOrderByImporteTotalDesc(IdPresupuesto);

        if (resumen.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        // Extraer los IDs de las cotizaciones
        List<Long> cotizacionIds = resumen.stream()
                .map(CotizacionResumen::getIdCotizacion)
                .toList();

        // Cargar las cotizaciones completas
        // Se hace el reverse para preservar el orden de menor a mayor importe total, proveniente de 'resumen'
        List<Cotizacion> cotizaciones = cotizacionRepository.findAllById(cotizacionIds).reversed();

        // Cargar los detalles de las cotizaciones
        List<DetalleCotizacion> detalles = cotizacionRepository.findDetallesByCotizacionIds(cotizacionIds);

        // Mapear entidades a DTOs
        List<CotizacionDto> cotizacionDtos = cotizaciones.stream()
                .map(c -> {
                    CotizacionDto dto = new CotizacionDto();
                    dto.setId(c.getId());
                    dto.setFecha(c.getFecha());

                    // Calcular el importe total de los detalles
                    List<DetalleCotizacionDto> detallesDto = detalles.stream()
                            .filter(d -> d.getCotizacion().getId().equals(c.getId()))
                            .map(d -> {
                                DetalleCotizacionDto detalleDto = new DetalleCotizacionDto();
//                                detalleDto.setIdProducto(d.getProducto().getId());
                                detalleDto.setNombreProducto(d.getProducto().getNombre());
                                detalleDto.setNombreMarca(d.getProducto().getMarca().getNombre());
//                                detalleDto.setNombreCategoria(d.getProducto().getCategoria().getNombre());
                                detalleDto.setImporte(d.getImporte());
                                return detalleDto;
                            })
                            .toList();

                    dto.setDetalles(detallesDto);

                    dto.setImporteTotal(resumen.stream()
                            .filter(r -> r.getIdCotizacion().equals(c.getId()))
                            .findFirst()
                            .map(CotizacionResumen::getMontoTotal)
                            .orElse(0.0));
                    // Datos de distribuidora (acceso seguro sin activar proxy lazy)
                    if (c.getDistribuidora() != null) {
                        dto.setCuitDistribuidora(c.getDistribuidora().getCuit());
                        dto.setNombreDistribuidora(c.getDistribuidora().getNombre());
                    }

                    return dto;
                })
                .toList();

        return ResponseEntity.ok(cotizacionDtos);
    }
}