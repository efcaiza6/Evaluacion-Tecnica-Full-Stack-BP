package com.fullstack.bp.dto;

import com.fullstack.bp.enums.Estado;
import com.fullstack.bp.enums.Genero;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ClienteRequest {

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotNull(message = "El genero es obligatorio")
    private Genero genero;

    @NotNull(message = "La edad es obligatoria")
    @Min(value = 0, message = "La edad no puede ser negativa")
    private Integer edad;

    @NotBlank(message = "La identificacion es obligatoria")
    private String identificacion;

    @NotBlank(message = "La direccion es obligatoria")
    private String direccion;

    @NotBlank(message = "El telefono es obligatorio")
    private String telefono;

    @NotBlank(message = "El clienteId es obligatorio")
    private String clienteId;

    @NotBlank(message = "La contrasena es obligatoria")
    private String contrasena;

    @NotNull(message = "El estado es obligatorio")
    private Estado estado;
}
