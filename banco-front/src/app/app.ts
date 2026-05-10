import { CommonModule } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, computed, inject, signal } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';

import { ESTADOS, GENEROS, TIPOS_CUENTA, TIPOS_MOVIMIENTO } from './core/constants/catalogos';
import { MENU_ITEMS } from './core/constants/menu';
import type { ApiErrorResponse, Cliente, Cuenta, Movimiento, ReporteResponse } from './core/models/bank.models';
import { ApiService } from './core/services/api.service';

@Component({
  selector: 'app-root',
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  private readonly apiService = inject(ApiService);
  private readonly formBuilder = inject(FormBuilder);

  protected readonly menuItems = MENU_ITEMS;
  protected readonly generos = GENEROS;
  protected readonly estados = ESTADOS;
  protected readonly tiposCuenta = TIPOS_CUENTA;
  protected readonly tiposMovimiento = TIPOS_MOVIMIENTO;

  protected readonly seccionActiva = signal<'clientes' | 'cuentas' | 'movimientos' | 'reportes'>('clientes');
  protected readonly terminoBusqueda = signal('');
  protected readonly mensaje = signal('');
  protected readonly tipoMensaje = signal<'ok' | 'error'>('ok');

  protected readonly clientes = signal<Cliente[]>([]);
  protected readonly cuentas = signal<Cuenta[]>([]);
  protected readonly movimientos = signal<Movimiento[]>([]);
  protected readonly reporte = signal<ReporteResponse | null>(null);

  protected readonly clienteForm = this.formBuilder.nonNullable.group({
    nombre: ['', Validators.required],
    genero: ['MASCULINO' as Cliente['genero'], Validators.required],
    edad: [18, [Validators.required, Validators.min(0)]],
    identificacion: ['', Validators.required],
    direccion: ['', Validators.required],
    telefono: ['', Validators.required],
    clienteId: ['', Validators.required],
    contrasena: ['', Validators.required],
    estado: ['ACTIVO' as Cliente['estado'], Validators.required]
  });

  protected readonly cuentaForm = this.formBuilder.nonNullable.group({
    numeroCuenta: ['', Validators.required],
    tipoCuenta: ['AHORROS' as Cuenta['tipoCuenta'], Validators.required],
    saldoInicial: [0, [Validators.required, Validators.min(0)]],
    estado: ['ACTIVO' as Cuenta['estado'], Validators.required],
    clienteId: [0, [Validators.required, Validators.min(1)]]
  });

  protected readonly movimientoForm = this.formBuilder.nonNullable.group({
    fecha: [this.today(), Validators.required],
    tipoMovimiento: ['CREDITO' as Movimiento['tipoMovimiento'], Validators.required],
    valor: [0, [Validators.required, Validators.min(0.01)]],
    cuentaId: [0, [Validators.required, Validators.min(1)]]
  });

  protected readonly reporteForm = this.formBuilder.nonNullable.group({
    clienteId: [0, [Validators.required, Validators.min(1)]],
    fechaDesde: ['', Validators.required],
    fechaHasta: ['', Validators.required]
  });

  protected editingClienteId: number | null = null;
  protected editingCuentaId: number | null = null;
  protected editingMovimientoId: number | null = null;

  protected readonly clientesFiltrados = computed(() => {
    const criterio = this.terminoBusqueda().trim().toLowerCase();
    if (!criterio) {
      return this.clientes();
    }

    return this.clientes().filter((cliente) =>
      [cliente.nombre, cliente.identificacion, cliente.clienteId, cliente.telefono]
        .some((valor) => valor.toLowerCase().includes(criterio))
    );
  });

  protected readonly cuentasFiltradas = computed(() => {
    const criterio = this.terminoBusqueda().trim().toLowerCase();
    if (!criterio) {
      return this.cuentas();
    }

    return this.cuentas().filter((cuenta) =>
      [cuenta.numeroCuenta, cuenta.tipoCuenta, cuenta.cliente?.nombre ?? '']
        .some((valor) => valor.toLowerCase().includes(criterio))
    );
  });

  protected readonly movimientosFiltrados = computed(() => {
    const criterio = this.terminoBusqueda().trim().toLowerCase();
    if (!criterio) {
      return this.movimientos();
    }

    return this.movimientos().filter((movimiento) =>
      [movimiento.fecha, movimiento.tipoMovimiento, movimiento.cuenta?.numeroCuenta ?? '']
        .some((valor) => valor.toLowerCase().includes(criterio))
    );
  });

  constructor() {
    this.cargarTodo();
  }

  protected cambiarSeccion(seccion: 'clientes' | 'cuentas' | 'movimientos' | 'reportes') {
    this.seccionActiva.set(seccion);
    this.terminoBusqueda.set('');
    this.mensaje.set('');
  }

  protected actualizarBusqueda(event: Event) {
    const target = event.target as HTMLInputElement;
    this.terminoBusqueda.set(target.value);
  }

  protected guardarCliente() {
    if (this.clienteForm.invalid) {
      this.clienteForm.markAllAsTouched();
      this.showError('Revise los datos del cliente');
      return;
    }

    const request = this.cleanClientePayload(this.clienteForm.getRawValue());
    const operation = this.editingClienteId
      ? this.apiService.updateCliente(this.editingClienteId, request)
      : this.apiService.createCliente(request);

    operation.subscribe({
      next: () => {
        this.showMessage('Cliente guardado correctamente');
        this.cancelarCliente();
        this.cargarClientes();
      },
      error: (error) => this.handleError(error)
    });
  }

  protected editarCliente(cliente: Cliente) {
    this.clienteForm.patchValue({
      nombre: cliente.nombre,
      genero: cliente.genero,
      edad: cliente.edad,
      identificacion: cliente.identificacion,
      direccion: cliente.direccion,
      telefono: cliente.telefono,
      clienteId: cliente.clienteId,
      contrasena: cliente.contrasena,
      estado: cliente.estado
    });
    this.editingClienteId = cliente.id ?? null;
    this.cambiarSeccion('clientes');
  }

  protected eliminarCliente(id?: number) {
    if (!id || !confirm('Desea eliminar este cliente?')) {
      return;
    }

    this.apiService.deleteCliente(id).subscribe({
      next: () => {
        this.showMessage('Cliente eliminado correctamente');
        this.cargarClientes();
        this.cargarCuentas();
      },
      error: (error) => this.handleError(error)
    });
  }

  protected cancelarCliente() {
    this.clienteForm.reset({
      nombre: '',
      genero: 'MASCULINO',
      edad: 18,
      identificacion: '',
      direccion: '',
      telefono: '',
      clienteId: '',
      contrasena: '',
      estado: 'ACTIVO'
    });
    this.editingClienteId = null;
  }

  protected guardarCuenta() {
    if (this.cuentaForm.invalid) {
      this.cuentaForm.markAllAsTouched();
      this.showError('Revise los datos de la cuenta');
      return;
    }

    const request = this.cleanCuentaPayload(this.cuentaForm.getRawValue());
    const operation = this.editingCuentaId
      ? this.apiService.updateCuenta(this.editingCuentaId, request)
      : this.apiService.createCuenta(request);

    operation.subscribe({
      next: () => {
        this.showMessage('Cuenta guardada correctamente');
        this.cancelarCuenta();
        this.cargarCuentas();
      },
      error: (error) => this.handleError(error)
    });
  }

  protected editarCuenta(cuenta: Cuenta) {
    this.cuentaForm.patchValue({
      numeroCuenta: cuenta.numeroCuenta,
      tipoCuenta: cuenta.tipoCuenta,
      saldoInicial: Number(cuenta.saldoInicial),
      estado: cuenta.estado,
      clienteId: cuenta.cliente?.id ?? cuenta.clienteId
    });
    this.editingCuentaId = cuenta.id ?? null;
    this.cambiarSeccion('cuentas');
  }

  protected eliminarCuenta(id?: number) {
    if (!id || !confirm('Desea eliminar esta cuenta?')) {
      return;
    }

    this.apiService.deleteCuenta(id).subscribe({
      next: () => {
        this.showMessage('Cuenta eliminada correctamente');
        this.cargarCuentas();
        this.cargarMovimientos();
      },
      error: (error) => this.handleError(error)
    });
  }

  protected cancelarCuenta() {
    this.cuentaForm.reset({
      numeroCuenta: '',
      tipoCuenta: 'AHORROS',
      saldoInicial: 0,
      estado: 'ACTIVO',
      clienteId: 0
    });
    this.editingCuentaId = null;
  }

  protected guardarMovimiento() {
    if (this.movimientoForm.invalid) {
      this.movimientoForm.markAllAsTouched();
      this.showError('Revise los datos del movimiento');
      return;
    }

    const request = this.cleanMovimientoPayload(this.movimientoForm.getRawValue());
    const operation = this.editingMovimientoId
      ? this.apiService.updateMovimiento(this.editingMovimientoId, request)
      : this.apiService.createMovimiento(request);

    operation.subscribe({
      next: () => {
        this.showMessage('Movimiento guardado correctamente');
        this.cancelarMovimiento();
        this.cargarMovimientos();
        this.cargarCuentas();
      },
      error: (error) => this.handleError(error)
    });
  }

  protected editarMovimiento(movimiento: Movimiento) {
    this.movimientoForm.patchValue({
      fecha: movimiento.fecha,
      tipoMovimiento: movimiento.tipoMovimiento,
      valor: Math.abs(Number(movimiento.valor)),
      cuentaId: movimiento.cuenta?.id ?? movimiento.cuentaId
    });
    this.editingMovimientoId = movimiento.id ?? null;
    this.cambiarSeccion('movimientos');
  }

  protected eliminarMovimiento(id?: number) {
    if (!id || !confirm('Desea eliminar este movimiento?')) {
      return;
    }

    this.apiService.deleteMovimiento(id).subscribe({
      next: () => {
        this.showMessage('Movimiento eliminado correctamente');
        this.cargarMovimientos();
        this.cargarCuentas();
      },
      error: (error) => this.handleError(error)
    });
  }

  protected cancelarMovimiento() {
    this.movimientoForm.reset({
      fecha: this.today(),
      tipoMovimiento: 'CREDITO',
      valor: 0,
      cuentaId: 0
    });
    this.editingMovimientoId = null;
  }

  protected generarReporte() {
    if (this.reporteForm.invalid) {
      this.reporteForm.markAllAsTouched();
      this.showError('Complete los datos del reporte');
      return;
    }

    const { clienteId, fechaDesde, fechaHasta } = this.reporteForm.getRawValue();

    this.apiService.getReporte(clienteId, fechaDesde, fechaHasta).subscribe({
      next: (response) => {
        this.reporte.set(response);
        this.showMessage('Reporte generado correctamente');
      },
      error: (error) => this.handleError(error)
    });
  }

  protected descargarPdf() {
    if (this.reporteForm.invalid) {
      this.reporteForm.markAllAsTouched();
      this.showError('Primero genere el reporte');
      return;
    }

    const { clienteId, fechaDesde, fechaHasta } = this.reporteForm.getRawValue();

    this.apiService.getReportePdf(clienteId, fechaDesde, fechaHasta).subscribe({
      next: (response) => {
        const link = document.createElement('a');
        link.href = `data:application/pdf;base64,${response.archivoBase64}`;
        link.download = 'reporte.pdf';
        link.click();
      },
      error: (error) => this.handleError(error)
    });
  }

  protected isInvalid(formName: 'cliente' | 'cuenta' | 'movimiento' | 'reporte', controlName: string) {
    const form = this.resolveForm(formName);
    const control = form.get(controlName);
    return !!control && control.invalid && (control.touched || control.dirty);
  }

  protected formatCurrency(value?: number | string | null) {
    return new Intl.NumberFormat('es-EC', {
      style: 'currency',
      currency: 'USD'
    }).format(Number(value ?? 0));
  }

  protected isNegative(value?: number | string | null) {
    return Number(value ?? 0) < 0;
  }

  private cargarTodo() {
    this.cargarClientes();
    this.cargarCuentas();
    this.cargarMovimientos();
  }

  private cargarClientes() {
    this.apiService.getClientes().subscribe({
      next: (data) => this.clientes.set(data),
      error: (error) => this.handleError(error)
    });
  }

  private cargarCuentas() {
    this.apiService.getCuentas().subscribe({
      next: (data) => this.cuentas.set(data.map((cuenta) => ({
        ...cuenta,
        clienteId: cuenta.cliente?.id ?? cuenta.clienteId
      }))),
      error: (error) => this.handleError(error)
    });
  }

  private cargarMovimientos() {
    this.apiService.getMovimientos().subscribe({
      next: (data) => this.movimientos.set(data.map((movimiento) => ({
        ...movimiento,
        cuentaId: movimiento.cuenta?.id ?? movimiento.cuentaId
      }))),
      error: (error) => this.handleError(error)
    });
  }

  private resolveForm(formName: 'cliente' | 'cuenta' | 'movimiento' | 'reporte'): FormGroup {
    switch (formName) {
      case 'cliente':
        return this.clienteForm;
      case 'cuenta':
        return this.cuentaForm;
      case 'movimiento':
        return this.movimientoForm;
      default:
        return this.reporteForm;
    }
  }

  private showMessage(text: string) {
    this.tipoMensaje.set('ok');
    this.mensaje.set(text);
  }

  private showError(text: string) {
    this.tipoMensaje.set('error');
    this.mensaje.set(text);
  }

  private handleError(error: HttpErrorResponse) {
    const payload = error.error as ApiErrorResponse | string | null;

    if (payload && typeof payload === 'object' && payload.details) {
      const details = Object.values(payload.details).join(', ');
      this.showError(details || 'No se pudo procesar la solicitud');
      return;
    }

    if (payload && typeof payload === 'object' && (payload.error || payload.message)) {
      this.showError(payload.error || payload.message || 'No se pudo procesar la solicitud');
      return;
    }

    if (typeof payload === 'string' && payload.trim()) {
      this.showError(payload);
      return;
    }

    this.showError('Ocurrio un error al conectar con el servidor');
  }

  private cleanClientePayload(cliente: {
    nombre: string;
    genero: Cliente['genero'];
    edad: number;
    identificacion: string;
    direccion: string;
    telefono: string;
    clienteId: string;
    contrasena: string;
    estado: Cliente['estado'];
  }): Cliente {
    return {
      nombre: cliente.nombre.trim(),
      genero: cliente.genero,
      edad: Number(cliente.edad),
      identificacion: cliente.identificacion.trim(),
      direccion: cliente.direccion.trim(),
      telefono: cliente.telefono.trim(),
      clienteId: cliente.clienteId.trim(),
      contrasena: cliente.contrasena.trim(),
      estado: cliente.estado
    };
  }

  private cleanCuentaPayload(cuenta: {
    numeroCuenta: string;
    tipoCuenta: Cuenta['tipoCuenta'];
    saldoInicial: number;
    estado: Cuenta['estado'];
    clienteId: number;
  }): Cuenta {
    return {
      numeroCuenta: cuenta.numeroCuenta.trim(),
      tipoCuenta: cuenta.tipoCuenta,
      saldoInicial: Number(cuenta.saldoInicial),
      estado: cuenta.estado,
      clienteId: Number(cuenta.clienteId)
    };
  }

  private cleanMovimientoPayload(movimiento: {
    fecha: string;
    tipoMovimiento: Movimiento['tipoMovimiento'];
    valor: number;
    cuentaId: number;
  }): Movimiento {
    return {
      fecha: movimiento.fecha,
      tipoMovimiento: movimiento.tipoMovimiento,
      valor: Number(movimiento.valor),
      cuentaId: Number(movimiento.cuentaId)
    };
  }

  private today() {
    return new Date().toISOString().slice(0, 10);
  }
}
