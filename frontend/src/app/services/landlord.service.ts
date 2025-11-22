import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ApiResponse } from '../models/api-response.model';
import { LandlordRequest, Landlord, RegistrationStatus } from '../models/landlord.model';

@Injectable({ providedIn: 'root' })
export class LandlordService {
  private tenantApiUrl = 'http://localhost:8081/api/landlord-registration';
  private adminApiUrl = 'http://localhost:8081/api/admin/landlords';

  constructor(private http: HttpClient) {}

  // ==================== TENANT APIs ====================
  
  getRegistrationStatus(userId: number): Observable<ApiResponse<RegistrationStatus>> {
    return this.http.get<ApiResponse<RegistrationStatus>>(
      `${this.tenantApiUrl}/status/${userId}`
    );
  }

  registerLandlord(formData: FormData): Observable<ApiResponse<LandlordRequest>> {
    return this.http.post<ApiResponse<LandlordRequest>>(
      `${this.tenantApiUrl}/register`,
      formData
    );
  }

  cancelRegistration(userId: number): Observable<ApiResponse<void>> {
    return this.http.delete<ApiResponse<void>>(
      `${this.tenantApiUrl}/cancel/${userId}`
    );
  }

  // ==================== ADMIN APIs ====================

  getAllRequests(): Observable<ApiResponse<LandlordRequest[]>> {
    return this.http.get<ApiResponse<LandlordRequest[]>>(
      `${this.adminApiUrl}/requests`
    );
  }

  getPendingRequests(): Observable<ApiResponse<LandlordRequest[]>> {
    return this.http.get<ApiResponse<LandlordRequest[]>>(
      `${this.adminApiUrl}/requests/pending`
    );
  }

  getRequestDetail(requestId: number): Observable<ApiResponse<LandlordRequest>> {
    return this.http.get<ApiResponse<LandlordRequest>>(
      `${this.adminApiUrl}/requests/${requestId}`
    );
  }

  approveRequest(requestId: number): Observable<ApiResponse<void>> {
    return this.http.post<ApiResponse<void>>(
      `${this.adminApiUrl}/requests/${requestId}/approve`,
      {}
    );
  }

  rejectRequest(requestId: number, reason: string): Observable<ApiResponse<void>> {
    return this.http.post<ApiResponse<void>>(
      `${this.adminApiUrl}/requests/${requestId}/reject`,
      { reason }
    );
  }

  getRequestStatistics(): Observable<ApiResponse<{ [key: string]: number }>> {
    return this.http.get<ApiResponse<{ [key: string]: number }>>(
      `${this.adminApiUrl}/requests/statistics`
    );
  }

  getAllLandlords(): Observable<ApiResponse<Landlord[]>> {
    return this.http.get<ApiResponse<Landlord[]>>(this.adminApiUrl);
  }

  getLandlordDetail(landlordId: number): Observable<ApiResponse<Landlord>> {
    return this.http.get<ApiResponse<Landlord>>(
      `${this.adminApiUrl}/${landlordId}`
    );
  }
}
