package com.fullstack.bp.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fullstack.bp.enums.Estado;
import com.fullstack.bp.enums.Genero;
import com.fullstack.bp.exception.GlobalExceptionHandler;
import com.fullstack.bp.model.Cliente;
import com.fullstack.bp.service.ClienteService;

@WebMvcTest(ClienteController.class)
@Import(GlobalExceptionHandler.class)
class ClienteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ClienteService clienteService;

    @Test
    void shouldCreateCliente() throws Exception {
        Cliente cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNombre("Juan Perez");
        cliente.setGenero(Genero.MASCULINO);
        cliente.setEdad(30);
        cliente.setIdentificacion("0102030405");
        cliente.setDireccion("Quito");
        cliente.setTelefono("0999999999");
        cliente.setClienteId("jperez");
        cliente.setContrasena("1234");
        cliente.setEstado(Estado.ACTIVO);

        when(clienteService.create(any())).thenReturn(cliente);

        mockMvc.perform(post("/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "nombre": "Juan Perez",
                      "genero": "MASCULINO",
                      "edad": 30,
                      "identificacion": "0102030405",
                      "direccion": "Quito",
                      "telefono": "0999999999",
                      "clienteId": "jperez",
                      "contrasena": "1234",
                      "estado": "ACTIVO"
                    }
                    """))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.nombre").value("Juan Perez"))
            .andExpect(jsonPath("$.clienteId").value("jperez"));
    }

    @Test
    void shouldReturnBadRequestWhenClienteIsInvalid() throws Exception {
        mockMvc.perform(post("/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "nombre": "",
                      "genero": "MASCULINO",
                      "edad": 30,
                      "identificacion": "",
                      "direccion": "",
                      "telefono": "",
                      "clienteId": "",
                      "contrasena": "",
                      "estado": "ACTIVO"
                    }
                    """))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").value("Validacion fallida"))
            .andExpect(jsonPath("$.details.nombre").exists());
    }
}
