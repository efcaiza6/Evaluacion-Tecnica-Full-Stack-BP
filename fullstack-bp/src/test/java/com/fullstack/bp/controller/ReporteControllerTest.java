package com.fullstack.bp.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fullstack.bp.dto.ReporteCuentaDto;
import com.fullstack.bp.dto.ReporteMovimientoDto;
import com.fullstack.bp.dto.ReporteResponse;
import com.fullstack.bp.service.ReporteService;

@WebMvcTest(ReporteController.class)
class ReporteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReporteService reporteService;

    @Test
    void shouldReturnReporteJson() throws Exception {
        ReporteResponse reporte = new ReporteResponse(
            1L,
            "Juan Perez",
            LocalDate.of(2026, 5, 1),
            LocalDate.of(2026, 5, 31),
            List.of(new ReporteCuentaDto(
                "12345",
                "AHORROS",
                new BigDecimal("1200.00"),
                new BigDecimal("300.00"),
                new BigDecimal("100.00"),
                List.of(new ReporteMovimientoDto(
                    LocalDate.of(2026, 5, 10),
                    "CREDITO",
                    new BigDecimal("300.00"),
                    new BigDecimal("1200.00")
                ))
            ))
        );

        when(reporteService.generarReporte(
            1L,
            LocalDate.of(2026, 5, 1),
            LocalDate.of(2026, 5, 31)
        )).thenReturn(reporte);

        mockMvc.perform(get("/reportes")
                .param("clienteId", "1")
                .param("fechaDesde", "2026-05-01")
                .param("fechaHasta", "2026-05-31")
                .param("formato", "json"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.cliente").value("Juan Perez"))
            .andExpect(jsonPath("$.cuentas[0].numeroCuenta").value("12345"));
    }
}
