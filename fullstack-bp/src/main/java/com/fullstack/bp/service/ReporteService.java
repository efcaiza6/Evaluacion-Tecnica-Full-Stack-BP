package com.fullstack.bp.service;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.fullstack.bp.constants.AppMessages;
import com.fullstack.bp.dto.ReporteCuentaDto;
import com.fullstack.bp.dto.ReporteMovimientoDto;
import com.fullstack.bp.dto.ReporteResponse;
import com.fullstack.bp.enums.TipoMovimiento;
import com.fullstack.bp.model.Cliente;
import com.fullstack.bp.model.Cuenta;
import com.fullstack.bp.model.Movimiento;
import com.fullstack.bp.repository.MovimientoRepository;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;

@Service
public class ReporteService {

    private final ClienteService clienteService;
    private final CuentaService cuentaService;
    private final MovimientoRepository movimientoRepository;

    public ReporteService(
        ClienteService clienteService,
        CuentaService cuentaService,
        MovimientoRepository movimientoRepository
    ) {
        this.clienteService = clienteService;
        this.cuentaService = cuentaService;
        this.movimientoRepository = movimientoRepository;
    }

    public ReporteResponse generarReporte(Long clienteId, LocalDate fechaDesde, LocalDate fechaHasta) {
        Cliente cliente = clienteService.findById(clienteId);
        List<Cuenta> cuentas = cuentaService.findByCliente(clienteId);

        List<ReporteCuentaDto> cuentasReporte = cuentas.stream()
            .map(cuenta -> buildCuentaDto(cuenta, fechaDesde, fechaHasta))
            .toList();

        return new ReporteResponse(
            cliente.getId(),
            cliente.getNombre(),
            fechaDesde,
            fechaHasta,
            cuentasReporte
        );
    }

    public String generarReportePdfBase64(Long clienteId, LocalDate fechaDesde, LocalDate fechaHasta) {
        ReporteResponse reporte = generarReporte(clienteId, fechaDesde, fechaHasta);
        Document document = new Document();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, outputStream);
            document.open();
            document.add(new Paragraph("Estado de cuenta"));
            document.add(new Paragraph("Cliente: " + reporte.cliente()));
            document.add(new Paragraph("Rango: " + fechaDesde + " - " + fechaHasta));
            document.add(new Paragraph(" "));

            for (ReporteCuentaDto cuenta : reporte.cuentas()) {
                document.add(new Paragraph("Cuenta: " + cuenta.numeroCuenta()));
                document.add(new Paragraph("Tipo: " + cuenta.tipoCuenta()));
                document.add(new Paragraph("Saldo disponible: " + cuenta.saldoDisponible()));
                document.add(new Paragraph("Total creditos: " + cuenta.totalCreditos()));
                document.add(new Paragraph("Total debitos: " + cuenta.totalDebitos()));
                for (ReporteMovimientoDto movimiento : cuenta.movimientos()) {
                    document.add(new Paragraph(
                        movimiento.fecha() + " | " + movimiento.tipoMovimiento() + " | " +
                            movimiento.valor() + " | saldo: " + movimiento.saldo()
                    ));
                }
                document.add(new Paragraph(" "));
            }
        } catch (DocumentException ex) {
            throw new IllegalStateException(AppMessages.PDF_NO_GENERADO, ex);
        } finally {
            document.close();
        }

        return Base64.getEncoder().encodeToString(outputStream.toByteArray());
    }

    private ReporteCuentaDto buildCuentaDto(Cuenta cuenta, LocalDate fechaDesde, LocalDate fechaHasta) {
        List<Movimiento> movimientos = movimientoRepository.findByCuentaIdOrderByFechaAscIdAsc(cuenta.getId())
            .stream()
            .filter(item -> !item.getFecha().isBefore(fechaDesde) && !item.getFecha().isAfter(fechaHasta))
            .toList();

        List<ReporteMovimientoDto> movimientosReporte = movimientos.stream()
            .map(item -> new ReporteMovimientoDto(
                item.getFecha(),
                item.getTipoMovimiento().name(),
                item.getValor(),
                item.getSaldo()
            ))
            .collect(Collectors.toList());

        BigDecimal totalCreditos = movimientos.stream()
            .filter(item -> item.getTipoMovimiento() == TipoMovimiento.CREDITO)
            .map(Movimiento::getValor)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalDebitos = movimientos.stream()
            .filter(item -> item.getTipoMovimiento() == TipoMovimiento.DEBITO)
            .map(item -> item.getValor().abs())
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new ReporteCuentaDto(
            cuenta.getNumeroCuenta(),
            cuenta.getTipoCuenta().name(),
            cuenta.getSaldoDisponible(),
            totalCreditos,
            totalDebitos,
            movimientosReporte
        );
    }
}
