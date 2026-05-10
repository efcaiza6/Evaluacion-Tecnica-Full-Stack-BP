package com.fullstack.bp.controller;

import java.time.LocalDate;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fullstack.bp.service.ReporteService;

@RestController
@RequestMapping("/reportes")
public class ReporteController {

    private final ReporteService reporteService;

    public ReporteController(ReporteService reporteService) {
        this.reporteService = reporteService;
    }

    @GetMapping
    public Object generarReporte(
        @RequestParam Long clienteId,
        @RequestParam LocalDate fechaDesde,
        @RequestParam LocalDate fechaHasta,
        @RequestParam(defaultValue = "json") String formato
    ) {
        if ("pdf".equalsIgnoreCase(formato)) {
            return Map.of(
                "clienteId", clienteId,
                "fechaDesde", fechaDesde,
                "fechaHasta", fechaHasta,
                "archivoBase64", reporteService.generarReportePdfBase64(clienteId, fechaDesde, fechaHasta)
            );
        }

        return reporteService.generarReporte(clienteId, fechaDesde, fechaHasta);
    }
}
