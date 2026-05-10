package com.fullstack.bp.dto;

import java.math.BigDecimal;
import java.util.List;

public record ReporteCuentaDto(
    String numeroCuenta,
    String tipoCuenta,
    BigDecimal saldoDisponible,
    BigDecimal totalCreditos,
    BigDecimal totalDebitos,
    List<ReporteMovimientoDto> movimientos
) {
}
