import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ElectricRecord, UtilityStatus } from '../models/electricity.model';

export interface ElectricityRequestDTO {
  roomId: number;
  oldIndex: number;
  newIndex: number;
  unitPrice: number;
  month: string;
  source: 'LANDLORD' | 'TENANT' | 'SYSTEM';
}

@Injectable({ providedIn: 'root' })
export class UtilityService {
  private apiUrl = 'http://localhost:8081/api/utilities/electric';

  constructor(private http: HttpClient) {}

  getAll(
    month?: string,
    status?: UtilityStatus | 'ALL'
  ): Observable<ElectricRecord[]> {
    let params = new HttpParams();

    if (month && month !== 'Tất cả tháng') {
      params = params.set('month', month);
    }

    if (status && status !== 'ALL') {
      params = params.set('status', status);
    }

    return this.http.get<ElectricRecord[]>(this.apiUrl, { params });
  }

  getById(id: number): Observable<ElectricRecord> {
    return this.http.get<ElectricRecord>(`${this.apiUrl}/${id}`);
  }

  create(data: ElectricityRequestDTO): Observable<ElectricRecord> {
    return this.http.post<ElectricRecord>(this.apiUrl, data);
  }

  update(id: number, data: ElectricityRequestDTO): Observable<ElectricRecord> {
    return this.http.put<ElectricRecord>(`${this.apiUrl}/${id}`, data);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  markPaid(id: number): Observable<ElectricRecord> {
    return this.http.put<ElectricRecord>(`${this.apiUrl}/${id}/mark-paid`, {});
  }
}
