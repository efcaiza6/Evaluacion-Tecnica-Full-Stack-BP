package com.fullstack.bp.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.fullstack.bp.constants.AppMessages;
import com.fullstack.bp.dto.ClienteRequest;
import com.fullstack.bp.exception.BusinessException;
import com.fullstack.bp.exception.ResourceNotFoundException;
import com.fullstack.bp.model.Cliente;
import com.fullstack.bp.repository.ClienteRepository;
import com.fullstack.bp.repository.CuentaRepository;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final CuentaRepository cuentaRepository;

    public ClienteService(ClienteRepository clienteRepository, CuentaRepository cuentaRepository) {
        this.clienteRepository = clienteRepository;
        this.cuentaRepository = cuentaRepository;
    }

    public List<Cliente> findAll() {
        return clienteRepository.findAll();
    }

    public Cliente findById(Long id) {
        return clienteRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(AppMessages.CLIENTE_NO_ENCONTRADO));
    }

    public Cliente create(ClienteRequest request) {
        Cliente cliente = new Cliente();
        mapRequest(cliente, request);
        return clienteRepository.save(cliente);
    }

    public Cliente update(Long id, ClienteRequest request) {
        Cliente cliente = findById(id);
        mapRequest(cliente, request);
        return clienteRepository.save(cliente);
    }

    public void delete(Long id) {
        Cliente cliente = findById(id);
        if (cuentaRepository.existsByClienteId(id)) {
            throw new BusinessException(AppMessages.CLIENTE_CON_CUENTAS);
        }
        clienteRepository.delete(cliente);
    }

    private void mapRequest(Cliente cliente, ClienteRequest request) {
        cliente.setNombre(request.getNombre());
        cliente.setGenero(request.getGenero());
        cliente.setEdad(request.getEdad());
        cliente.setIdentificacion(request.getIdentificacion());
        cliente.setDireccion(request.getDireccion());
        cliente.setTelefono(request.getTelefono());
        cliente.setClienteId(request.getClienteId());
        cliente.setContrasena(request.getContrasena());
        cliente.setEstado(request.getEstado());
    }
}
