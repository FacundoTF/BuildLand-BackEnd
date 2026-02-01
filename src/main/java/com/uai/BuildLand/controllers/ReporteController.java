package com.uai.BuildLand.controllers;

import com.uai.BuildLand.models.reporte.productosSolicitados.ReporteProductosSolicitados;
import com.uai.BuildLand.models.reporte.productosSolicitados.ReporteProductosSolicitadosDatos;
import com.uai.BuildLand.models.reporte.productosSolicitados.ReporteProductosSolicitadosEspecifico;
import com.uai.BuildLand.models.reporte.productosSolicitados.ReporteProductosSolicitadosResponse;
import com.uai.BuildLand.repositories.PresupuestoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/reporte")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ReporteController {
    private final PresupuestoRepository presupuestoRepository;

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
}
