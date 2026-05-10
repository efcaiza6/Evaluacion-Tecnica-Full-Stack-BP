package com.fullstack.bp.model;

import com.fullstack.bp.enums.Estado;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "clientes")
@Getter
@Setter
public class Cliente extends Persona {

    @Column(nullable = false, unique = true)
    @NotBlank(message = "El clienteId es obligatorio")
    private String clienteId;

    @Column(nullable = false)
    @NotBlank(message = "La contrasena es obligatoria")
    private String contrasena;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @NotNull(message = "El estado es obligatorio")
    private Estado estado;
}
