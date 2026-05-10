import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';

import {
  Cliente,
  Cuenta,
  Movimiento,
  ReporteResponse
} from '../models/bank.models';

@Injectable({ providedIn: 'root' })
export class ApiService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = 'http://localhost:8080';

  getClientes() {
    return this.http.get<Cliente[]>(`${this.baseUrl}/clientes`);
  }

  createCliente(payload: Cliente) {
    return this.http.post<Cliente>(`${this.baseUrl}/clientes`, payload);
  }

  updateCliente(id: number, payload: Cliente) {
    return this.http.put<Cliente>(`${this.baseUrl}/clientes/${id}`, payload);
  }

  deleteCliente(id: number) {
    return this.http.delete(`${this.baseUrl}/clientes/${id}`);
  }

  getCuentas() {
    return this.http.get<Cuenta[]>(`${this.baseUrl}/cuentas`);
  }

  createCuenta(payload: Cuenta) {
    return this.http.post<Cuenta>(`${this.baseUrl}/cuentas`, payload);
  }

  updateCuenta(id: number, payload: Cuenta) {
    return this.http.put<Cuenta>(`${this.baseUrl}/cuentas/${id}`, payload);
  }

  deleteCuenta(id: number) {
    return this.http.delete(`${this.baseUrl}/cuentas/${id}`);
  }

  getMovimientos(cuentaId?: number) {
    let params = new HttpParams();
    if (cuentaId) {
      params = params.set('cuentaId', cuentaId);
    }
    return this.http.get<Movimiento[]>(`${this.baseUrl}/movimientos`, { params });
  }

  createMovimiento(payload: Movimiento) {
    return this.http.post<Movimiento>(`${this.baseUrl}/movimientos`, payload);
  }

  updateMovimiento(id: number, payload: Movimiento) {
    return this.http.put<Movimiento>(`${this.baseUrl}/movimientos/${id}`, payload);
  }

  deleteMovimiento(id: number) {
    return this.http.delete(`${this.baseUrl}/movimientos/${id}`);
  }

  getReporte(clienteId: number, fechaDesde: string, fechaHasta: string) {
    const params = new HttpParams()
      .set('clienteId', clienteId)
      .set('fechaDesde', fechaDesde)
      .set('fechaHasta', fechaHasta)
      .set('formato', 'json');

    return this.http.get<ReporteResponse>(`${this.baseUrl}/reportes`, { params });
  }

  getReportePdf(clienteId: number, fechaDesde: string, fechaHasta: string) {
    const params = new HttpParams()
      .set('clienteId', clienteId)
      .set('fechaDesde', fechaDesde)
      .set('fechaHasta', fechaHasta)
      .set('formato', 'pdf');

    return this.http.get<{ archivoBase64: string }>(`${this.baseUrl}/reportes`, { params });
  }
}
