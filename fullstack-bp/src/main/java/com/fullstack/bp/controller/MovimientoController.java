package com.fullstack.bp.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.fullstack.bp.dto.MovimientoRequest;
import com.fullstack.bp.model.Movimiento;
import com.fullstack.bp.service.MovimientoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/movimientos")
public class MovimientoController {

    private final MovimientoService movimientoService;

    public MovimientoController(MovimientoService movimientoService) {
        this.movimientoService = movimientoService;
    }

    @GetMapping
    public List<Movimiento> findAll(@RequestParam(required = false) Long cuentaId) {
        if (cuentaId != null) {
            return movimientoService.findByCuenta(cuentaId);
        }
        return movimientoService.findAll();
    }

    @GetMapping("/{id}")
    public Movimiento findById(@PathVariable Long id) {
        return movimientoService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Movimiento create(@Valid @RequestBody MovimientoRequest request) {
        return movimientoService.create(request);
    }

    @PutMapping("/{id}")
    public Movimiento update(@PathVariable Long id, @Valid @RequestBody MovimientoRequest request) {
        return movimientoService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        movimientoService.delete(id);
    }
}
