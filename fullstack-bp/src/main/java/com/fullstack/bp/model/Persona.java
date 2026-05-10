package com.fullstack.bp.model;

import com.fullstack.bp.enums.Genero;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Entity
@Table(name = "personas")
@Inheritance(strategy = InheritanceType.JOINED)
@Data
public class Persona {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @NotNull(message = "El genero es obligatorio")
    private Genero genero;

    @Column(nullable = false)
    @NotNull(message = "La edad es obligatoria")
    @Min(value = 0, message = "La edad no puede ser negativa")
    private Integer edad;

    @Column(nullable = false, unique = true)
    @NotBlank(message = "La identificacion es obligatoria")
    private String identificacion;

    @Column(nullable = false)
    @NotBlank(message = "La direccion es obligatoria")
    private String direccion;

    @Column(nullable = false)
    @NotBlank(message = "El telefono es obligatorio")
    @Size(min = 7, message = "El telefono debe tener al menos 7 caracteres")
    private String telefono;
}
