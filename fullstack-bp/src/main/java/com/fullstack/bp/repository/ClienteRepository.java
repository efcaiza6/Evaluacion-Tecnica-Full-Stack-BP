package com.fullstack.bp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fullstack.bp.model.Cliente;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
}
