package com.fullstack.bp.dto;

import java.time.LocalDate;
import java.util.List;

public record ReporteResponse(
    Long clienteId,
    String cliente,
    LocalDate fechaDesde,
    LocalDate fechaHasta,
    List<ReporteCuentaDto> cuentas
) {
}
