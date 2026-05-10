package com.fullstack.bp.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.fullstack.bp.enums.TipoMovimiento;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import jakarta.validation.constraints.DecimalMin;

@Data
public class MovimientoRequest {

    @NotNull(message = "La fecha es obligatoria")
    private LocalDate fecha;

    @NotNull(message = "El tipo de movimiento es obligatorio")
    private TipoMovimiento tipoMovimiento;

    @NotNull(message = "El valor es obligatorio")
    @DecimalMin(value = "0.01", inclusive = true, message = "El valor debe ser positivo")
    private BigDecimal valor;

    @NotNull(message = "La cuentaId es obligatoria")
    private Long cuentaId;
}
