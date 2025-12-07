import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import {
  ExtraCost,
  CostType,
  ExtraCostStatus,
} from '../models/extra-cost.model';

@Injectable({
  providedIn: 'root',
})
export class ExtraCostService {
  private http = inject(HttpClient);

  private apiUrl = 'http://localhost:8081/api/utilities/extra-costs';

  constructor() {}

  getAll(
    month?: string,
    roomId?: number,
    type?: CostType | 'ALL',
    status?: ExtraCostStatus | 'ALL'
  ): Observable<ExtraCost[]> {
    let params = new HttpParams();

    if (month) {
      params = params.set('month', month);
    }

    if (roomId && roomId !== 0) {
      params = params.set('roomId', roomId);
    }

    if (type && type !== 'ALL') {
      params = params.set('type', type);
    }

    if (status && status !== 'ALL') {
      params = params.set('status', status);
    }

    return this.http.get<ExtraCost[]>(this.apiUrl, { params });
  }

  getById(id: number): Observable<ExtraCost> {
    return this.http.get<ExtraCost>(`${this.apiUrl}/${id}`);
  }

  create(data: ExtraCost): Observable<ExtraCost> {
    return this.http.post<ExtraCost>(this.apiUrl, data);
  }

  update(id: number, data: ExtraCost): Observable<ExtraCost> {
    return this.http.put<ExtraCost>(`${this.apiUrl}/${id}`, data);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  markPaid(id: number): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/${id}/mark-paid`, {});
  }
}
