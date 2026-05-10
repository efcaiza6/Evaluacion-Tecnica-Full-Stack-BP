package com.fullstack.bp.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ReporteMovimientoDto(
    LocalDate fecha,
    String tipoMovimiento,
    BigDecimal valor,
    BigDecimal saldo
) {
}
