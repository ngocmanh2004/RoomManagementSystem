import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError, tap } from 'rxjs/operators';
import { BookingRequest, Contract, ContractResponse } from '../models/booking.model';

@Injectable({
  providedIn: 'root'
})
export class BookingService {
  private apiUrl = '/api/bookings';

  constructor(private http: HttpClient) {}

  private getHeaders(): HttpHeaders {
    const token = localStorage.getItem('accessToken');
    let headers = new HttpHeaders({
      'Content-Type': 'application/json'
    });

    if (token) {
      headers = headers.set('Authorization', `Bearer ${token}`);
    }
    
    return headers;
  }

  createBooking(request: BookingRequest): Observable<Contract> {
    const payload = {
      roomId: request.roomId,
      startDate: request.startDate,
      endDate: request.endDate || null,
      deposit: request.deposit || 0,
      notes: request.notes || '',
      fullName: request.fullName,
      cccd: request.cccd,
      phone: request.phone,
      address: request.address
    };

    console.log('📝 Creating booking:', JSON.stringify(payload));

    return this.http.post<Contract>(
      this.apiUrl,
      payload,
      { headers: this.getHeaders() }
    ).pipe(
      tap(response => {
        console.log('✅ Booking created:', response);
      }),
      catchError(error => {
        console.error('❌ Booking error:', error);
        const message = error.error?.message || error.statusText || 'Lỗi đặt thuê phòng';
        return throwError(() => new Error(message));
      })
    );
  }

  getMyContracts(page: number = 0, size: number = 10): Observable<ContractResponse> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    return this.http.get<ContractResponse>(
      `${this.apiUrl}/my-contracts`,
      { params, headers: this.getHeaders() }
    ).pipe(
      catchError(error => {
        console.error('Error loading contracts:', error);
        return throwError(() => new Error(error.error?.message || 'Lỗi tải hợp đồng'));
      })
    );
  }

  getContractById(id: number): Observable<Contract> {
    return this.http.get<Contract>(
      `${this.apiUrl}/${id}`,
      { headers: this.getHeaders() }
    ).pipe(
      catchError(error => {
        console.error('Error loading contract:', error);
        return throwError(() => new Error(error.error?.message || 'Lỗi tải chi tiết hợp đồng'));
      })
    );
  }
}