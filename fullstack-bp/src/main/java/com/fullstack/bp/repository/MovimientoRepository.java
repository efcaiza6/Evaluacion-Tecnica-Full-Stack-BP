package com.fullstack.bp.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fullstack.bp.model.Movimiento;

public interface MovimientoRepository extends JpaRepository<Movimiento, Long> {
    List<Movimiento> findByCuentaIdOrderByFechaAscIdAsc(Long cuentaId);

    List<Movimiento> findByCuentaClienteIdAndFechaBetweenOrderByFechaAscIdAsc(
        Long clienteId,
        LocalDate fechaDesde,
        LocalDate fechaHasta
    );
}
