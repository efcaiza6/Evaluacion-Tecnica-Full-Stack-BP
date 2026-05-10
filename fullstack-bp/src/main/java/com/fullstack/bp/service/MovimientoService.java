package com.fullstack.bp.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fullstack.bp.constants.AppMessages;
import com.fullstack.bp.config.AppProperties;
import com.fullstack.bp.dto.MovimientoRequest;
import com.fullstack.bp.enums.TipoMovimiento;
import com.fullstack.bp.exception.BusinessException;
import com.fullstack.bp.exception.ResourceNotFoundException;
import com.fullstack.bp.model.Cuenta;
import com.fullstack.bp.model.Movimiento;
import com.fullstack.bp.repository.MovimientoRepository;

@Service
public class MovimientoService {

    private final MovimientoRepository movimientoRepository;
    private final CuentaService cuentaService;
    private final AppProperties appProperties;

    public MovimientoService(
        MovimientoRepository movimientoRepository,
        CuentaService cuentaService,
        AppProperties appProperties
    ) {
        this.movimientoRepository = movimientoRepository;
        this.cuentaService = cuentaService;
        this.appProperties = appProperties;
    }

    public List<Movimiento> findAll() {
        return movimientoRepository.findAll();
    }

    public Movimiento findById(Long id) {
        return movimientoRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(AppMessages.MOVIMIENTO_NO_ENCONTRADO));
    }

    @Transactional
    public Movimiento create(MovimientoRequest request) {
        Cuenta cuenta = cuentaService.findById(request.getCuentaId());
        Movimiento movimiento = new Movimiento();
        movimiento.setCuenta(cuenta);
        movimiento.setFecha(request.getFecha());
        movimiento.setTipoMovimiento(request.getTipoMovimiento());
        movimiento.setValor(normalizeValue(request.getTipoMovimiento(), request.getValor()));
        movimiento.setSaldo(cuenta.getSaldoDisponible());
        Movimiento saved = movimientoRepository.save(movimiento);
        recalculateAccount(cuenta.getId());
        return findById(saved.getId());
    }

    @Transactional
    public Movimiento update(Long id, MovimientoRequest request) {
        Movimiento movimiento = findById(id);
        Long cuentaAnteriorId = movimiento.getCuenta().getId();
        Cuenta cuenta = cuentaService.findById(request.getCuentaId());
        movimiento.setCuenta(cuenta);
        movimiento.setFecha(request.getFecha());
        movimiento.setTipoMovimiento(request.getTipoMovimiento());
        movimiento.setValor(normalizeValue(request.getTipoMovimiento(), request.getValor()));
        movimientoRepository.save(movimiento);
        if (!cuentaAnteriorId.equals(cuenta.getId())) {
            recalculateAccount(cuentaAnteriorId);
        }
        recalculateAccount(cuenta.getId());
        return findById(id);
    }

    @Transactional
    public void delete(Long id) {
        Movimiento movimiento = findById(id);
        Long cuentaId = movimiento.getCuenta().getId();
        movimientoRepository.delete(movimiento);
        recalculateAccount(cuentaId);
    }

    public List<Movimiento> findByCuenta(Long cuentaId) {
        cuentaService.findById(cuentaId);
        return movimientoRepository.findByCuentaIdOrderByFechaAscIdAsc(cuentaId);
    }

    @Transactional
    public void recalculateAccount(Long cuentaId) {
        Cuenta cuenta = cuentaService.findById(cuentaId);
        List<Movimiento> movimientos = movimientoRepository.findByCuentaIdOrderByFechaAscIdAsc(cuentaId);
        BigDecimal saldo = cuenta.getSaldoInicial();
        Map<LocalDate, BigDecimal> debitosPorDia = new HashMap<>();

        for (Movimiento movimiento : movimientos) {
            BigDecimal nuevoSaldo = saldo.add(movimiento.getValor());
            if (nuevoSaldo.compareTo(BigDecimal.ZERO) < 0) {
                throw new BusinessException(AppMessages.SALDO_NO_DISPONIBLE);
            }

            if (movimiento.getTipoMovimiento() == TipoMovimiento.DEBITO) {
                validateDailyLimit(movimiento, debitosPorDia);
            }

            saldo = nuevoSaldo;
            movimiento.setSaldo(saldo);
        }

        cuenta.setSaldoDisponible(saldo);
    }

    private void validateDailyLimit(Movimiento movimientoActual, Map<LocalDate, BigDecimal> debitosPorDia) {
        BigDecimal totalDebitosDelDia = debitosPorDia
            .getOrDefault(movimientoActual.getFecha(), BigDecimal.ZERO)
            .add(movimientoActual.getValor().abs());

        if (totalDebitosDelDia.compareTo(getDailyLimit()) > 0) {
            throw new BusinessException(AppMessages.CUPO_DIARIO_EXCEDIDO);
        }

        debitosPorDia.put(movimientoActual.getFecha(), totalDebitosDelDia);
    }

    private BigDecimal normalizeValue(TipoMovimiento tipoMovimiento, BigDecimal valor) {
        return tipoMovimiento == TipoMovimiento.CREDITO ? valor.abs() : valueOrZero(valor).abs().negate();
    }

    private BigDecimal valueOrZero(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private BigDecimal getDailyLimit() {
        return appProperties.limiteRetiroDiario() == null
            ? new BigDecimal("1000")
            : appProperties.limiteRetiroDiario();
    }
}
