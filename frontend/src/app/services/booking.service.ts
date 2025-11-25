import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError, tap, map } from 'rxjs/operators';
import { BookingRequest, BookingContract, ContractResponse } from '../models/booking.model';
import { Contract } from '../models/contract.model';
import { ApiResponse } from '../models/api-response.model';

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

    return this.http.post<ApiResponse<Contract>>(this.apiUrl, payload, { withCredentials: true }).pipe(
      map(resp => resp.data as Contract),
      tap(response => {
        console.log('✅ Booking created (unwrapped):', response);
      }),
      catchError(error => {
        console.error('❌ Booking error:', error);
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
    return this.http.get<ApiResponse<ContractResponse>>(
      `${this.apiUrl}/my-contracts?page=${page}&size=${size}`, { withCredentials: true }
    ).pipe(
      map(resp => resp.data as ContractResponse),
      catchError(error => {
        console.error('Error loading contracts:', error);
        return throwError(() => new Error(error.error?.message || 'Lỗi tải hợp đồng'));
      })
    );
  }

  getContractById(id: number): Observable<Contract> {
    return this.http.get<ApiResponse<Contract>>(`${this.apiUrl}/${id}`, { withCredentials: true }).pipe(
      map(resp => resp.data as Contract),
      catchError(error => {
        console.error('Error loading contract:', error);
        return throwError(() => new Error(error.error?.message || 'Lỗi tải chi tiết hợp đồng'));
      })
    );
  }

  getMyActiveContract(): Observable<Contract> {
    return this.http.get<ApiResponse<Contract>>(`${this.apiUrl}/my-contract`, { withCredentials: true }).pipe(
      map(resp => resp.data as Contract),
      catchError(error => {
        console.error('Error loading my active contract:', error);
        return throwError(() => new Error(error.error?.message || 'Lỗi tải hợp đồng'));
      })
    );
  }
}