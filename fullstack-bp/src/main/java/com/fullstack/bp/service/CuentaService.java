package com.fullstack.bp.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;

import com.fullstack.bp.constants.AppMessages;
import com.fullstack.bp.dto.CuentaRequest;
import com.fullstack.bp.exception.ResourceNotFoundException;
import com.fullstack.bp.model.Cliente;
import com.fullstack.bp.model.Cuenta;
import com.fullstack.bp.repository.CuentaRepository;

@Service
public class CuentaService {

    private final CuentaRepository cuentaRepository;
    private final ClienteService clienteService;

    public CuentaService(CuentaRepository cuentaRepository, ClienteService clienteService) {
        this.cuentaRepository = cuentaRepository;
        this.clienteService = clienteService;
    }

    public List<Cuenta> findAll() {
        return cuentaRepository.findAll();
    }

    public Cuenta findById(Long id) {
        return cuentaRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(AppMessages.CUENTA_NO_ENCONTRADA));
    }

    public Cuenta create(CuentaRequest request) {
        Cuenta cuenta = new Cuenta();
        mapRequest(cuenta, request);
        return cuentaRepository.save(cuenta);
    }

    public Cuenta update(Long id, CuentaRequest request) {
        Cuenta cuenta = findById(id);
        BigDecimal saldoActual = cuenta.getSaldoDisponible();
        BigDecimal diferenciaBase = saldoActual.subtract(cuenta.getSaldoInicial());

        mapRequest(cuenta, request);
        cuenta.setSaldoDisponible(cuenta.getSaldoInicial().add(diferenciaBase));
        return cuentaRepository.save(cuenta);
    }

    public void delete(Long id) {
        Cuenta cuenta = findById(id);
        cuentaRepository.delete(cuenta);
    }

    public List<Cuenta> findByCliente(Long clienteId) {
        return cuentaRepository.findByClienteId(clienteId);
    }

    private void mapRequest(Cuenta cuenta, CuentaRequest request) {
        Cliente cliente = clienteService.findById(request.getClienteId());
        cuenta.setNumeroCuenta(request.getNumeroCuenta());
        cuenta.setTipoCuenta(request.getTipoCuenta());
        cuenta.setSaldoInicial(request.getSaldoInicial());
        if (cuenta.getSaldoDisponible() == null) {
            cuenta.setSaldoDisponible(request.getSaldoInicial());
        }
        cuenta.setEstado(request.getEstado());
        cuenta.setCliente(cliente);
    }
}
