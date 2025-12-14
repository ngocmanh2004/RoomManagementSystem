import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
export interface Feedback {
  id: number;
  title: string;
  content: string;
  attachmentUrl?: string;
  status: 'PENDING'|'PROCESSING'|'RESOLVED'|'CANCELED'|'TENANT_CONFIRMED'|'TENANT_REJECTED';
  room: { id: number; name: string };
  createdAt: string;
  resolvedAt?: string;
  landlordNote?: string;
  tenantFeedback?: string;
  tenantSatisfied?: boolean;
}

@Injectable({ providedIn: 'root' })
export class FeedbackService {
  private api = 'http://localhost:8081/api/feedback';

  constructor(private http: HttpClient) {}

  create(data: any): Observable<Feedback> {
    return this.http.post<Feedback>(this.api, data);
  }

  getMyFeedback(): Observable<Feedback[]> {
    return this.http.get<Feedback[]>(`${this.api}/my`);
  }

  getForLandlord(page = 0, size = 10): Observable<any> {
    return this.http.get<any>(`${this.api}/landlord?page=${page}&size=${size}&sort=createdAt,desc`);
  }

  startProcessing(id: number): Observable<Feedback> {
    return this.http.put<Feedback>(`${this.api}/${id}/process`, {});
  }

  resolve(id: number, note: string) {
    return this.http.put<Feedback>(`${this.api}/${id}/resolve`, { 
        landlordNote: note,
        status: 'RESOLVED' // Đảm bảo backend cập nhật status này
    });
    }
  cancel(id: number): Observable<Feedback> {
    console.log('Cancel ID:', id);
    return this.http.put<Feedback>(`${this.api}/${id}/cancel`, {});
    }

  tenantConfirm(id: number): Observable<Feedback> {
    return this.http.put<Feedback>(`${this.api}/${id}/tenant-confirm`, {});
  }

  tenantReject(id: number, feedback: string): Observable<Feedback> {
    return this.http.put<Feedback>(`${this.api}/${id}/tenant-reject`, { feedback });
  }
  // feedback.service.ts
    delete(id: number) {
    return this.http.delete(`${this.api}/${id}`);
    }
    // API REOPEN (khi tenant reject)
  reopen(feedbackId: number): Observable<any> {
    return this.http.put(`${this.api}/${feedbackId}/reopen`, {});
  }
  
  // API để lấy feedback với filter (cho landlord)
  getForLandlordWithFilter(page: number, filters: any): Observable<any> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', '10');
    
    // Thêm filter
    if (filters.status) params = params.set('status', filters.status);
    if (filters.roomId) params = params.set('roomId', filters.roomId);
    if (filters.startDate) params = params.set('startDate', filters.startDate);
    if (filters.endDate) params = params.set('endDate', filters.endDate);
    
    return this.http.get(`${this.api}/landlord`, { params });
  }
}