import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ApiResponse } from '../models/api-response.model';
import { LandlordRequest, Landlord, RegistrationStatus } from '../models/landlord.model';

@Injectable({ providedIn: 'root' })
export class LandlordService {
  private tenantApiUrl = '/api/landlord-registration';
  private adminApiUrl = '/api/admin/landlords';

  constructor(private http: HttpClient) {}

  // ==================== TENANT APIs ====================
  
  getRegistrationStatus(userId: number): Observable<ApiResponse<RegistrationStatus>> {
    return this.http.get<ApiResponse<RegistrationStatus>>(
      `${this.tenantApiUrl}/status/${userId}`, { withCredentials: true }
    );
  }

  registerLandlord(formData: FormData): Observable<ApiResponse<LandlordRequest>> {
    return this.http.post<ApiResponse<LandlordRequest>>(
      `${this.tenantApiUrl}/register`,
      formData,
      { withCredentials: true }
    );
  }

  cancelRegistration(userId: number): Observable<ApiResponse<void>> {
    return this.http.delete<ApiResponse<void>>(
      `${this.tenantApiUrl}/cancel/${userId}`, { withCredentials: true }
    );
  }

  // ==================== ADMIN APIs ====================

  getAllRequests(): Observable<ApiResponse<LandlordRequest[]>> {
    return this.http.get<ApiResponse<LandlordRequest[]>>(
      `${this.adminApiUrl}/requests`, { withCredentials: true }
    );
  }

  getPendingRequests(): Observable<ApiResponse<LandlordRequest[]>> {
    return this.http.get<ApiResponse<LandlordRequest[]>>(
      `${this.adminApiUrl}/requests/pending`, { withCredentials: true }
    );
  }

  getRequestDetail(requestId: number): Observable<ApiResponse<LandlordRequest>> {
    return this.http.get<ApiResponse<LandlordRequest>>(
      `${this.adminApiUrl}/requests/${requestId}`, { withCredentials: true }
    );
  }

  approveRequest(requestId: number): Observable<ApiResponse<void>> {
    return this.http.post<ApiResponse<void>>(
      `${this.adminApiUrl}/requests/${requestId}/approve`,
      {}, { withCredentials: true }
    );
  }

  rejectRequest(requestId: number, reason: string): Observable<ApiResponse<void>> {
    return this.http.post<ApiResponse<void>>(
      `${this.adminApiUrl}/requests/${requestId}/reject`,
      { reason }, { withCredentials: true }
    );
  }

  getRequestStatistics(): Observable<ApiResponse<{ [key: string]: number }>> {
    return this.http.get<ApiResponse<{ [key: string]: number }>>(
      `${this.adminApiUrl}/requests/statistics`, { withCredentials: true }
    );
  }

  getAllLandlords(): Observable<ApiResponse<Landlord[]>> {
    return this.http.get<ApiResponse<Landlord[]>>(this.adminApiUrl, { withCredentials: true });
  }

  getLandlordDetail(landlordId: number): Observable<ApiResponse<Landlord>> {
    return this.http.get<ApiResponse<Landlord>>(
      `${this.adminApiUrl}/${landlordId}`, { withCredentials: true }
    );
  }
}
