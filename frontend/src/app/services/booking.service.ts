import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError, tap } from 'rxjs/operators';
import { BookingRequest, Contract, ContractResponse } from '../models/booking.model';

@Injectable({
  providedIn: 'root'
})
export class BookingService {
  private apiUrl = '/api/bookings';

  constructor(private http: HttpClient) {}

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
    console.log('📝 Token exists:', localStorage.getItem('accessToken') ? 'YES' : 'NO');

    return this.http.post<Contract>(this.apiUrl, payload).pipe(
      tap(response => {
        console.log('✅ Booking created:', response);
      }),
      catchError(error => {
        console.error('❌ Booking error:', error);
        console.error('❌ Error status:', error.status);
        console.error('❌ Error response:', error.error);
        
        let message = 'Lỗi đặt thuê phòng';
        
        if (error.status === 401) {
          message = 'Phiên đăng nhập hết hạn. Vui lòng đăng nhập lại.';
        } else if (error.status === 403) {
          message = 'Bạn không có quyền thực hiện thao tác này.';
        } else if (error.error?.message) {
          message = error.error.message;
        } else if (error.statusText) {
          message = error.statusText;
        }
        
        return throwError(() => new Error(message));
      })
    );
  }

  getMyContracts(page: number = 0, size: number = 10): Observable<ContractResponse> {
    return this.http.get<ContractResponse>(
      `${this.apiUrl}/my-contracts?page=${page}&size=${size}`
    ).pipe(
      catchError(error => {
        console.error('Error loading contracts:', error);
        return throwError(() => new Error(error.error?.message || 'Lỗi tải hợp đồng'));
      })
    );
  }

  getContractById(id: number): Observable<Contract> {
    return this.http.get<Contract>(`${this.apiUrl}/${id}`).pipe(
      catchError(error => {
        console.error('Error loading contract:', error);
        return throwError(() => new Error(error.error?.message || 'Lỗi tải chi tiết hợp đồng'));
      })
    );
  }
}