package com.uai.BuildLand.controllers;

import com.uai.BuildLand.config.PasswordEncoder;
import com.uai.BuildLand.models.Distribuidora;
import com.uai.BuildLand.models.dto.PaginationResultDto;
import com.uai.BuildLand.models.dto.TrazabilidadLoginDto;
import com.uai.BuildLand.models.dto.TrazabilidadPersonaFisicaDto;
import com.uai.BuildLand.models.reporte.mensajesRechazoCotizacion.ReporteRechazoCotizacionDatos;
import com.uai.BuildLand.models.reporte.productosSolicitados.ReporteProductosSolicitados;
import com.uai.BuildLand.models.reporte.productosSolicitados.ReporteProductosSolicitadosDatos;
import com.uai.BuildLand.models.reporte.productosSolicitados.ReporteProductosSolicitadosEspecifico;
import com.uai.BuildLand.repositories.*;
import com.uai.BuildLand.services.JwtTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/reporte")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ReporteController {
    private final PresupuestoRepository presupuestoRepository;
    private final CotizacionRepository cotizacionRepository;
    private final DistribuidoraRepository distribuidoraRepository;
    private final TrazLogin trazLoginRepository;
    private final TrazPersonaFisica trazPersonaFisicaRepository;
    private final JwtTokenService jwtTokenService;

    @PostMapping("/productos-solicitados")
    @Transactional
    public ResponseEntity<List<ReporteProductosSolicitadosDatos>> reportarProductosSolicitados(@RequestBody ReporteProductosSolicitados re) {
        List<Object> datosEnObjeto;
        try {
            datosEnObjeto = presupuestoRepository.sp_ReporteProductosSolicitadosGral(re.getCategoria(), re.getFechaInicio(), re.getFechaFin());
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        List<ReporteProductosSolicitadosDatos> reporte = datosEnObjeto.stream().map((obj) -> {
            Object[] fila = (Object[]) obj;
            ReporteProductosSolicitadosDatos datosSolicitados = new ReporteProductosSolicitadosDatos();

            datosSolicitados.setCantidad(fila[0] != null ? ((Number) fila[0]).doubleValue() : 0.0);
            datosSolicitados.setProducto(fila[1] != null ? fila[1].toString() : "");
            datosSolicitados.setMarca(fila[2] != null ? fila[2].toString() : "");
            datosSolicitados.setPorcentaje(fila[3] != null ? ((Number) fila[3]).doubleValue() : 0.0);

            return datosSolicitados;
        }).toList();

        return ResponseEntity.status(HttpStatus.OK).body(reporte);
    }
    @PostMapping("/productos-solicitados/marca")
    @Transactional
    public ResponseEntity<List<ReporteProductosSolicitadosDatos>> reportarProductosSolicitadosPorMarca(@RequestBody ReporteProductosSolicitadosEspecifico re) {
        List<Object> datosEnObjeto;
        try {
            datosEnObjeto = presupuestoRepository.sp_ReporteProductosSolicitadosMarca(re.getCategoria(), re.getNombre(), re.getFechaInicio(), re.getFechaFin());
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        List<ReporteProductosSolicitadosDatos> reporte = datosEnObjeto.stream().map((obj) -> {
            Object[] fila = (Object[]) obj;
            ReporteProductosSolicitadosDatos datosSolicitados = new ReporteProductosSolicitadosDatos();

            datosSolicitados.setCantidad(fila[0] != null ? ((Number) fila[0]).doubleValue() : 0.0);
            datosSolicitados.setProducto(fila[1] != null ? fila[1].toString() : "");
            datosSolicitados.setPorcentaje(fila[2] != null ? ((Number) fila[2]).doubleValue() : 0.0);

            return datosSolicitados;
        }).toList();
        return ResponseEntity.status(HttpStatus.OK).body(reporte);
    }
    @PostMapping("/productos-solicitados/producto")
    @Transactional
    public ResponseEntity<List<ReporteProductosSolicitadosDatos>> reportarProductosSolicitadosPorProducto(@RequestBody ReporteProductosSolicitadosEspecifico re) {
        List<Object> datosEnObjeto;
        try {
            datosEnObjeto = presupuestoRepository.sp_ReporteProductosSolicitadosProducto(re.getCategoria(), re.getNombre(), re.getFechaInicio(), re.getFechaFin());
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        List<ReporteProductosSolicitadosDatos> reporte = datosEnObjeto.stream().map((obj) -> {
            Object[] fila = (Object[]) obj;
            ReporteProductosSolicitadosDatos datosSolicitados = new ReporteProductosSolicitadosDatos();

            datosSolicitados.setCantidad(fila[0] != null ? ((Number) fila[0]).doubleValue() : 0.0);
            datosSolicitados.setMarca(fila[1] != null ? fila[1].toString() : "");
            datosSolicitados.setPorcentaje(fila[2] != null ? ((Number) fila[2]).doubleValue() : 0.0);

            return datosSolicitados;
        }).toList();
        return ResponseEntity.status(HttpStatus.OK).body(reporte);
    }
    @PostMapping("/cotizaciones-ganadas-perdidas")
    @Transactional
    public ResponseEntity<List<ReporteRechazoCotizacionDatos>> reportarCotizacionesGanadasPerdidas(@RequestBody ReporteProductosSolicitados re, @CookieValue("accessToken") String token) {
        Optional<Distribuidora> distribuidoraOpt = distribuidoraRepository.findByUsuario_Email(jwtTokenService.extractUsername(token));
        if (distribuidoraOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        Distribuidora distribuidora = distribuidoraOpt.get();

        List<Object> datosEnObjeto;
        try {
            datosEnObjeto = cotizacionRepository.sp_ReporteCotizacionesPerdidasGanadas(distribuidora.getCuit(), re.getFechaInicio(), re.getFechaFin());

            List<ReporteRechazoCotizacionDatos> datosList = datosEnObjeto.stream().map((obj) -> {
                Object[] fila = (Object[]) obj;
                ReporteRechazoCotizacionDatos datos = new ReporteRechazoCotizacionDatos();
                datos.setPorcentaje(fila[1] != null ? ((Number) fila[1]).doubleValue() : 0.0);
                datos.setMensaje(fila[0] != null ? fila[0].toString() : "");
                return datos;
            }).toList();

            return ResponseEntity.ok(datosList);

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @PostMapping("/cotizaciones-rechazadas-mensajes")
    @Transactional
    public ResponseEntity<List<ReporteRechazoCotizacionDatos>> reportarCotizacionesRechazadasMensajes(@RequestBody ReporteProductosSolicitados re, @CookieValue("accessToken") String token) {
        Optional<Distribuidora> distribuidoraOpt = distribuidoraRepository.findByUsuario_Email(jwtTokenService.extractUsername(token));
        if (distribuidoraOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        Distribuidora distribuidora = distribuidoraOpt.get();

        List<Object> datosEnObjeto;
        try {
            datosEnObjeto = cotizacionRepository.sp_ReporteMensajeCotizacionesRechazadas(distribuidora.getCuit(), re.getFechaInicio(), re.getFechaFin());

            List<ReporteRechazoCotizacionDatos> datosList = datosEnObjeto.stream().map((obj) -> {
                Object[] fila = (Object[]) obj;
                ReporteRechazoCotizacionDatos datos = new ReporteRechazoCotizacionDatos();
                datos.setPorcentaje(fila[0] != null ? ((Number) fila[0]).doubleValue() : 0.0);
                datos.setMensaje(fila[1] != null ? fila[1].toString() : "");
                datos.setCategoria(fila[2] != null ? fila[2].toString() : "");
                return datos;
            }).toList();

            return ResponseEntity.ok(datosList);

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @PostMapping("/cotizaciones-rechazadas-mensajes-general")
    @Transactional
    public ResponseEntity<List<ReporteRechazoCotizacionDatos>> reportarCotizacionesRechazadasMensajesGeneral(@RequestBody ReporteProductosSolicitados re, @CookieValue("accessToken") String token) {
        Optional<Distribuidora> distribuidoraOpt = distribuidoraRepository.findByUsuario_Email(jwtTokenService.extractUsername(token));
        if (distribuidoraOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        Distribuidora distribuidora = distribuidoraOpt.get();

        List<Object> datosEnObjeto;
        try {
            datosEnObjeto = cotizacionRepository.sp_ReporteMensajeCotizacionesRechazadasGral(distribuidora.getCuit(), re.getFechaInicio(), re.getFechaFin());
            List<ReporteRechazoCotizacionDatos> datosList = datosEnObjeto.stream().map((obj) -> {
                Object[] fila = (Object[]) obj;
                ReporteRechazoCotizacionDatos datos = new ReporteRechazoCotizacionDatos();
                datos.setPorcentaje(fila[0] != null ? ((Number) fila[0]).doubleValue() : 0.0);
                datos.setMensaje(fila[1] != null ? fila[1].toString() : "");
                return datos;
            }).toList();
            return ResponseEntity.ok(datosList);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @GetMapping("/trazabilidad-persona-fisica/{cuit}")
    public PaginationResultDto reportarTrazabilidadPersonaFisica(@PathVariable String cuit, Pageable pageable, @Param(value = "pagina") Integer pagina) {
        try{
            String cuitNoNulo = Optional.ofNullable(cuit).orElseThrow(() -> new IllegalArgumentException("El CUIT no puede ser nulo"));
             if (cuitNoNulo.isEmpty()) {
                throw new IllegalArgumentException("El CUIT no puede estar vacío");
            }
            Specification<TrazabilidadPersonaFisicaDto> spec = (root, query, builder) -> builder.equal(root.get("cuit"), cuitNoNulo);

            Page<TrazabilidadPersonaFisicaDto> page = trazPersonaFisicaRepository.findAll(spec, PageRequest.of(
                    pagina,
                    5,
                    pageable.getSortOr(Sort.by(Sort.Direction.DESC, "fechaAccion"))
            ));

            return new PaginationResultDto(page.getContent(), page.hasPrevious(), page.hasNext());
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
        return null;
    }
    @GetMapping("/trazabilidad-login-logout/{cuit}")
    public PaginationResultDto reportarTrazabilidadLoginLogout(@PathVariable String cuit, Pageable pageable, @Param(value = "pagina") Integer pagina) {
        try {
            String cuitNoNulo = Optional.ofNullable(cuit).orElseThrow(() -> new IllegalArgumentException("El CUIT no puede ser nulo"));
            if (cuitNoNulo.isEmpty()) {
                throw new IllegalArgumentException("El CUIT no puede estar vacío");
            }
            Specification<TrazabilidadLoginDto> spec = (root, query, builder) -> builder.equal(root.get("cuit"), cuitNoNulo);

            Page<TrazabilidadLoginDto> page = trazLoginRepository.findAll(spec, PageRequest.of(
                    pagina,
                    5,
                    pageable.getSortOr(Sort.by(Sort.Direction.DESC, "fecha"))
            ));
            return new PaginationResultDto(page.getContent(), page.hasPrevious(), page.hasNext());
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
        return null;
    }
}
