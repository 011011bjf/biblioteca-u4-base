package ec.edu.uteq.appweb.biblioteca.web.controller;

import ec.edu.uteq.appweb.biblioteca.domain.Libro;
import ec.edu.uteq.appweb.biblioteca.domain.Prestamo;
import ec.edu.uteq.appweb.biblioteca.service.LibroService;
import ec.edu.uteq.appweb.biblioteca.service.PrestamoService;
import ec.edu.uteq.appweb.biblioteca.web.dto.*;
import ec.edu.uteq.appweb.biblioteca.web.mapper.LibroMapper;
import ec.edu.uteq.appweb.biblioteca.web.mapper.PrestamoMapper;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

/**
 * TODO-U4-1: API REST de prestamos.
 *
 *   GET  /api/v1/prestamos?estado=ACTIVO   paginado con meta
 *   POST /api/v1/prestamos                 201 + Location, rol BIBLIOTECARIO o ADMIN
 *   POST /api/v1/prestamos/{id}/devolucion 200, rol BIBLIOTECARIO o ADMIN
 *
 * Observe que PrestamoService ya lanza ReglaNegocioException cuando el socio
 * supera los tres prestamos activos o cuando no hay ejemplares: eso debe salir
 * como 409 Conflict en formato ProblemDetail, y ya lo hace el manejador global.
 * No lo capture usted en el controlador.
 */
@RestController
@RequestMapping("/api/v1/prestamos")
public class PrestamoController {


    private final PrestamoService servicio;
    private final PrestamoMapper mapper;

    public PrestamoController(PrestamoService servicio, PrestamoMapper mapper) {
        this.servicio = servicio;
        this.mapper = mapper;
    }
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PrestamoResponse>> crear(@Valid @RequestBody PrestamoRequest solicitud) {
        Prestamo creado = servicio.registrar(solicitud.libroId(), solicitud.socioId(), solicitud.diasPrestamo());
        PrestamoResponse cuerpo = mapper.aRespuesta(creado);
        return ResponseEntity
                .created(URI.create("/api/v1/prestamos/" + creado.getId()))
                .body(ApiResponse.ok(cuerpo, "Prestamo registrado"));
    }
}
