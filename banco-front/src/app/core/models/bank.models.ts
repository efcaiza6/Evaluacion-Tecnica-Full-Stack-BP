export interface Cliente {
  id?: number;
  nombre: string;
  genero: 'MASCULINO' | 'FEMENINO' | 'OTRO';
  edad: number;
  identificacion: string;
  direccion: string;
  telefono: string;
  clienteId: string;
  contrasena: string;
  estado: 'ACTIVO' | 'INACTIVO';
}

export interface Cuenta {
  id?: number;
  numeroCuenta: string;
  tipoCuenta: 'AHORROS' | 'CORRIENTE';
  saldoInicial: number;
  saldoDisponible?: number;
  estado: 'ACTIVO' | 'INACTIVO';
  clienteId: number;
  cliente?: Cliente;
}

export interface Movimiento {
  id?: number;
  fecha: string;
  tipoMovimiento: 'CREDITO' | 'DEBITO';
  valor: number;
  saldo?: number;
  cuentaId: number;
  cuenta?: Cuenta;
}

export interface ReporteMovimiento {
  fecha: string;
  tipoMovimiento: string;
  valor: number;
  saldo: number;
}

export interface ReporteCuenta {
  numeroCuenta: string;
  tipoCuenta: string;
  saldoDisponible: number;
  totalCreditos: number;
  totalDebitos: number;
  movimientos: ReporteMovimiento[];
}

export interface ReporteResponse {
  clienteId: number;
  cliente: string;
  fechaDesde: string;
  fechaHasta: string;
  cuentas: ReporteCuenta[];
}

export interface ApiErrorResponse {
  error?: string;
  message?: string;
  details?: Record<string, string>;
}
