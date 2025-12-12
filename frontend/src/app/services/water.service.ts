import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { WaterRecord } from '../models/water.model';
import { UtilityStatus } from '../models/electricity.model';

export interface WaterRequestDTO {
  roomId: number;
  oldIndex: number;
  newIndex: number;
  unitPrice: number;
  month: string;
}

@Injectable({ providedIn: 'root' })
export class WaterService {
  private apiUrl = 'http://localhost:8081/api/utilities/water';

  constructor(private http: HttpClient) {}

  getAll(
    month?: string,
    status?: UtilityStatus | 'ALL',
    roomId?: number
  ): Observable<WaterRecord[]> {
    let params = new HttpParams();
    if (month && month !== 'Tất cả tháng') {
      params = params.set('month', month);
    }

    if (status && status !== 'ALL') {
      params = params.set('status', status);
    }

    if (roomId && roomId !== 0) {
      params = params.set('roomId', roomId);
    }

    return this.http.get<WaterRecord[]>(this.apiUrl, { params });
  }

  getById(id: number): Observable<WaterRecord> {
    return this.http.get<WaterRecord>(`${this.apiUrl}/${id}`);
  }

  create(data: WaterRequestDTO): Observable<WaterRecord> {
    return this.http.post<WaterRecord>(this.apiUrl, data);
  }

  update(id: number, data: WaterRequestDTO): Observable<WaterRecord> {
    return this.http.put<WaterRecord>(`${this.apiUrl}/${id}`, data);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  markPaid(id: number): Observable<WaterRecord> {
    return this.http.put<WaterRecord>(`${this.apiUrl}/${id}/mark-paid`, {});
  }
}
