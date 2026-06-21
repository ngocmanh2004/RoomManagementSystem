import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { ApiResponse } from '../models/api-response.model';
import { Contract } from '../models/contract.model';
import { DirectContractRequest } from '../models/direct-contract.model';

export interface ContractListResponse {
  content: Contract[];
  totalPages: number;
  totalElements: number;
  number: number;
  size: number;
}

export interface RejectRequest {
  reason: string;
}

export interface TerminateRequest {
  terminationType: 'EXPIRED' | 'CANCELLED';
  reason?: string;
}

@Injectable({
  providedIn: 'root'
})
export class LandlordBookingService {
  private apiUrl = '/api/landlord/contracts';

  constructor(private http: HttpClient) {}

  getMyContracts(page: number = 0, size: number = 10, status?: string): Observable<ContractListResponse> {
    let url = `${this.apiUrl}?page=${page}&size=${size}`;
    if (status) {
      url += `&status=${status}`;
    }

    return this.http.get<ApiResponse<ContractListResponse>>(url, { withCredentials: true }).pipe(
      map(resp => resp.data as ContractListResponse),
      catchError(this.handleError)
    );
  }
  
  getContractDetail(id: number): Observable<Contract> {
    return this.http.get<ApiResponse<Contract>>(`${this.apiUrl}/${id}`, { withCredentials: true }).pipe(
      map(resp => resp.data as Contract),
      catchError(this.handleError)
    );
  }

  approveContract(id: number): Observable<Contract> {
    return this.http.post<ApiResponse<Contract>>(
      `${this.apiUrl}/${id}/approve`, 
      {}, 
      { withCredentials: true }
    ).pipe(
      map(resp => resp.data as Contract),
      catchError(this.handleError)
    );
  }

  rejectContract(id: number, reason: string): Observable<Contract> {
    const request: RejectRequest = { reason };
    return this.http.post<ApiResponse<Contract>>(
      `${this.apiUrl}/${id}/reject`,
      request,
      { withCredentials: true }
    ).pipe(
      map(resp => resp.data as Contract),
      catchError(this.handleError)
    );
  }

  terminateContract(
    id: number, 
    terminationType: 'EXPIRED' | 'CANCELLED', 
    reason?: string
  ): Observable<Contract> {
    const request: TerminateRequest = {
      terminationType: terminationType,
      reason: reason || ''
    };
    
    return this.http.post<ApiResponse<Contract>>(
      `${this.apiUrl}/${id}/terminate`,
      request,
      { withCredentials: true }
    ).pipe(
      map(resp => resp.data as Contract),
      catchError(this.handleError)
    );
  }

  /**
   * Tạo hợp đồng trực tiếp cho khách vãng lai
   * Hợp đồng được tạo ở trạng thái ACTIVE, bỏ qua PENDING
   */
  createDirectContract(data: DirectContractRequest): Observable<Contract> {
    return this.http.post<ApiResponse<Contract>>(
      `${this.apiUrl}/direct`,
      data,
      { withCredentials: true }
    ).pipe(
      map(resp => resp.data as Contract),
      catchError(this.handleError)
    );
  }

  /**
   * Lấy danh sách phòng AVAILABLE của landlord
   */
  getMyAvailableRooms(): Observable<any[]> {
    return this.http.get<ApiResponse<any[]>>(
      `${this.apiUrl}/rooms/available`,
      { withCredentials: true }
    ).pipe(
      map(resp => resp.data as any[]),
      catchError(this.handleError)
    );
  }

  /**
   * Lấy danh sách tenant không có hợp đồng ACTIVE
   */
  getAvailableTenants(): Observable<any[]> {
    return this.http.get<ApiResponse<any[]>>(
      '/api/tenants/available',
      { withCredentials: true }
    ).pipe(
      map(resp => resp.data as any[]),
      catchError(this.handleError)
    );
  }

  private handleError(error: any): Observable<never> {
    console.error('API Error:', error);
    let message = 'Đã xảy ra lỗi';
    
    if (error.status === 401) {
      message = 'Phiên đăng nhập hết hạn. Vui lòng đăng nhập lại.';
    } else if (error.status === 403) {
      message = 'Bạn không có quyền thực hiện thao tác này.';
    } else if (error.error?.message) {
      message = error.error.message;
    }
    
    return throwError(() => new Error(message));
  }

  // Thêm vào landlord-booking.service.ts

    extendContract(contractId: number, newEndDate: string, notes?: string): Observable<Contract> {
    const url = `${this.apiUrl}/${contractId}/extend`;
    const body = {
        newEndDate: newEndDate,
        notes: notes || ''
    };
    
    return this.http.post<ApiResponse<Contract>>(url, body, { withCredentials: true }).pipe(
        map(response => {
        if (response.success && response.data) {
            return response.data;
        }
        throw new Error(response.message || 'Gia hạn hợp đồng thất bại');
        }),
        catchError(error => {
        const message = error.error?.message || 'Có lỗi xảy ra khi gia hạn hợp đồng';
        return throwError(() => new Error(message));
        })
    );
    }
}