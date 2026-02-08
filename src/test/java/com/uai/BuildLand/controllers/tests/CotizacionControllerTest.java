package com.uai.BuildLand.controllers.tests;

import com.uai.BuildLand.controllers.CotizacionController;
import com.uai.BuildLand.models.*;
import com.uai.BuildLand.models.auth.Usuario;
import com.uai.BuildLand.models.cotizacion.Cotizacion;
import com.uai.BuildLand.models.cotizacion.DetalleCotizacion;
import com.uai.BuildLand.models.presupuesto.Presupuesto;
import com.uai.BuildLand.repositories.*;
import com.uai.BuildLand.services.JwtTokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CotizacionControllerTest {

    @Mock
    private CotizacionRepository cotizacionRepository;

    @Mock
    private JwtTokenService jwtTokenService;

    @Mock
    private DistribuidoraRepository distribuidoraRepository;

    @Mock
    private PresupuestoRepository presupuestoRepository;

    @Mock
    private ProductoMarcaRepository productoMarcaRepository;

    @InjectMocks
    private CotizacionController cotizacionController;

    private Cotizacion cotizacion;
    private Distribuidora distribuidora;
    private Presupuesto presupuesto;
    private ProductoMarca productoMarca;
    private Producto producto;
    private Marca marca;
    private Categoria categoria;
    private DetalleCotizacion detalleCotizacion;
    private String jwtToken;
    private Long idPresupuesto;
    private UriComponentsBuilder uriComponentsBuilder;

    @BeforeEach
    void setUp() {
        jwtToken = "valid-jwt-token";
        idPresupuesto = 1L;
        uriComponentsBuilder = UriComponentsBuilder.newInstance();

        // Setup Categoria
        categoria = new Categoria();
        categoria.setNombre("Construcción");

        // Setup Marca
        marca = new Marca();
        marca.setId(1L);
        marca.setNombre("Marca Test");
        marca.setCategoria(categoria);

        // Setup Producto
        producto = new Producto();
        producto.setId(1L);
        producto.setNombre("Producto Test");
        producto.setCategoria(categoria);

        // Setup ProductoMarca
        productoMarca = new ProductoMarca();
        productoMarca.setIdProductoMarca(1L);
        productoMarca.setProducto(producto);
        productoMarca.setMarca(marca);

        // Setup Usuario
        Usuario usuario = new Usuario();
        usuario.setEmail("distribuidora@test.com");

        // Setup Distribuidora
        distribuidora = new Distribuidora();
        distribuidora.setCuit("12345678901");
        distribuidora.setNombre("Distribuidora Test");
        distribuidora.setUsuario(usuario);

        // Setup Presupuesto
        presupuesto = new Presupuesto();
        presupuesto.setId(idPresupuesto);
        presupuesto.setFecha(new Date());

        // Setup DetalleCotizacion
        detalleCotizacion = new DetalleCotizacion();
        detalleCotizacion.setProductoMarca(productoMarca);
        detalleCotizacion.setImporte(1000.0);

        // Setup Cotizacion
        cotizacion = new Cotizacion();
        List<DetalleCotizacion> detalles = new ArrayList<>();
        detalles.add(detalleCotizacion);
        cotizacion.setDetalles(detalles);
    }

    @Test
    void crear_DeberiaCrearCotizacionCorrectamente_CuandoTodosLosDatosSonValidos() {
        // Arrange
        when(jwtTokenService.extractUsername(jwtToken)).thenReturn("distribuidora@test.com");
        when(distribuidoraRepository.findByUsuario_Email("distribuidora@test.com"))
                .thenReturn(Optional.of(distribuidora));
        when(productoMarcaRepository.findAllByMarca_NombreAndProducto_Nombre(
                marca.getNombre(), producto.getNombre()))
                .thenReturn(Optional.of(productoMarca));
        when(presupuestoRepository.findById(idPresupuesto)).thenReturn(Optional.of(presupuesto));

        Cotizacion cotizacionGuardada = new Cotizacion();
        cotizacionGuardada.setId(1L);
        cotizacionGuardada.setFecha(new Date());
        when(cotizacionRepository.save(any(Cotizacion.class))).thenReturn(cotizacionGuardada);

        // Act
        ResponseEntity<Cotizacion> response = cotizacionController.crear(
                cotizacion, idPresupuesto, jwtToken, uriComponentsBuilder);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
        assertNull(response.getBody().getPresupuesto(), "El presupuesto debe ser null en la respuesta");
        assertNull(response.getBody().getDetalles(), "Los detalles deben ser null en la respuesta");

        verify(jwtTokenService, times(1)).extractUsername(jwtToken);
        verify(distribuidoraRepository, times(1)).findByUsuario_Email("distribuidora@test.com");
        verify(productoMarcaRepository, times(1))
                .findAllByMarca_NombreAndProducto_Nombre(marca.getNombre(), producto.getNombre());
        verify(presupuestoRepository, times(1)).findById(idPresupuesto);
        verify(cotizacionRepository, times(1)).save(any(Cotizacion.class));
        verify(cotizacionRepository, times(1)).sp_NotificarDesplazamiento(idPresupuesto);
    }

    @Test
    void crear_DeberiaRetornar403_CuandoDistribuidoraNoExiste() {
        // Arrange
        when(jwtTokenService.extractUsername(jwtToken)).thenReturn("distribuidora@test.com");
        when(distribuidoraRepository.findByUsuario_Email("distribuidora@test.com"))
                .thenReturn(Optional.empty());

        // Act
        ResponseEntity<Cotizacion> response = cotizacionController.crear(
                cotizacion, idPresupuesto, jwtToken, uriComponentsBuilder);

        // Assert
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNull(response.getBody());

        verify(jwtTokenService, times(1)).extractUsername(jwtToken);
        verify(distribuidoraRepository, times(1)).findByUsuario_Email("distribuidora@test.com");
        verify(cotizacionRepository, never()).save(any(Cotizacion.class));
    }

    @Test
    void crear_DeberiaRetornar400_CuandoProductoMarcaNoExiste() {
        // Arrange
        when(jwtTokenService.extractUsername(jwtToken)).thenReturn("distribuidora@test.com");
        when(distribuidoraRepository.findByUsuario_Email("distribuidora@test.com"))
                .thenReturn(Optional.of(distribuidora));
        when(productoMarcaRepository.findAllByMarca_NombreAndProducto_Nombre(
                marca.getNombre(), producto.getNombre()))
                .thenReturn(Optional.empty());

        // Act
        ResponseEntity<Cotizacion> response = cotizacionController.crear(
                cotizacion, idPresupuesto, jwtToken, uriComponentsBuilder);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());

        verify(productoMarcaRepository, times(1))
                .findAllByMarca_NombreAndProducto_Nombre(marca.getNombre(), producto.getNombre());
        verify(cotizacionRepository, never()).save(any(Cotizacion.class));
    }

    @Test
    void crear_DeberiaRetornar400_CuandoPresupuestoNoExiste() {
        // Arrange
        when(jwtTokenService.extractUsername(jwtToken)).thenReturn("distribuidora@test.com");
        when(distribuidoraRepository.findByUsuario_Email("distribuidora@test.com"))
                .thenReturn(Optional.of(distribuidora));
        when(productoMarcaRepository.findAllByMarca_NombreAndProducto_Nombre(
                marca.getNombre(), producto.getNombre()))
                .thenReturn(Optional.of(productoMarca));
        when(presupuestoRepository.findById(idPresupuesto)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<Cotizacion> response = cotizacionController.crear(
                cotizacion, idPresupuesto, jwtToken, uriComponentsBuilder);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());

        verify(presupuestoRepository, times(1)).findById(idPresupuesto);
        verify(cotizacionRepository, never()).save(any(Cotizacion.class));
    }

    @Test
    void crear_DeberiaAsignarFechaYRelaciones_CuandoSeCreaLaCotizacion() {
        // Arrange
        when(jwtTokenService.extractUsername(jwtToken)).thenReturn("distribuidora@test.com");
        when(distribuidoraRepository.findByUsuario_Email("distribuidora@test.com"))
                .thenReturn(Optional.of(distribuidora));
        when(productoMarcaRepository.findAllByMarca_NombreAndProducto_Nombre(
                marca.getNombre(), producto.getNombre()))
                .thenReturn(Optional.of(productoMarca));
        when(presupuestoRepository.findById(idPresupuesto)).thenReturn(Optional.of(presupuesto));

        Cotizacion cotizacionGuardada = new Cotizacion();
        cotizacionGuardada.setId(1L);
        when(cotizacionRepository.save(any(Cotizacion.class))).thenReturn(cotizacionGuardada);

        // Act
        cotizacionController.crear(cotizacion, idPresupuesto, jwtToken, uriComponentsBuilder);

        // Assert
        verify(cotizacionRepository).save(argThat(c -> {
            assertNotNull(c.getFecha(), "La fecha debe ser asignada");
            assertEquals(presupuesto, c.getPresupuesto(), "El presupuesto debe estar asignado");
            assertEquals(distribuidora, c.getDistribuidora(), "La distribuidora debe estar asignada");

            // Verificar que los detalles tienen la cotización asignada
            for (DetalleCotizacion detalle : c.getDetalles()) {
                assertEquals(c, detalle.getCotizacion(), "Cada detalle debe tener la cotización asignada");
            }
            return true;
        }));
    }

    @Test
    void crear_DeberiaLlamarSPNotificarDesplazamiento_CuandoCotizacionEsCreada() {
        // Arrange
        when(jwtTokenService.extractUsername(jwtToken)).thenReturn("distribuidora@test.com");
        when(distribuidoraRepository.findByUsuario_Email("distribuidora@test.com"))
                .thenReturn(Optional.of(distribuidora));
        when(productoMarcaRepository.findAllByMarca_NombreAndProducto_Nombre(
                marca.getNombre(), producto.getNombre()))
                .thenReturn(Optional.of(productoMarca));
        when(presupuestoRepository.findById(idPresupuesto)).thenReturn(Optional.of(presupuesto));

        Cotizacion cotizacionGuardada = new Cotizacion();
        cotizacionGuardada.setId(1L);
        when(cotizacionRepository.save(any(Cotizacion.class))).thenReturn(cotizacionGuardada);

        // Act
        cotizacionController.crear(cotizacion, idPresupuesto, jwtToken, uriComponentsBuilder);

        // Assert
        verify(cotizacionRepository, times(1)).sp_NotificarDesplazamiento(idPresupuesto);
    }

    @Test
    void crear_DeberiaRetornarURICorrecta_CuandoCotizacionEsCreada() {
        // Arrange
        when(jwtTokenService.extractUsername(jwtToken)).thenReturn("distribuidora@test.com");
        when(distribuidoraRepository.findByUsuario_Email("distribuidora@test.com"))
                .thenReturn(Optional.of(distribuidora));
        when(productoMarcaRepository.findAllByMarca_NombreAndProducto_Nombre(
                marca.getNombre(), producto.getNombre()))
                .thenReturn(Optional.of(productoMarca));
        when(presupuestoRepository.findById(idPresupuesto)).thenReturn(Optional.of(presupuesto));

        Cotizacion cotizacionGuardada = new Cotizacion();
        cotizacionGuardada.setId(99L);
        when(cotizacionRepository.save(any(Cotizacion.class))).thenReturn(cotizacionGuardada);

        // Act
        ResponseEntity<Cotizacion> response = cotizacionController.crear(
                cotizacion, idPresupuesto, jwtToken, uriComponentsBuilder);

        // Assert
        assertNotNull(response.getHeaders().getLocation());
        assertTrue(response.getHeaders().getLocation().toString().contains("/cotizaciones/99"));
    }
}