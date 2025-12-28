import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Feedback } from '../models/feedback.model';

/*@Injectable({ providedIn: 'root' })
export class FeedbackService {
  private api = 'http://localhost:8081/api/feedbacks';

  constructor(private http: HttpClient) {}

  create(feedback: { title: string; content: string; attachmentUrl?: string }) {
    return this.http.post<Feedback>(this.api, feedback);
  }

  getMyFeedback(): Observable<Feedback[]> {
    return this.http.get<Feedback[]>(`${this.api}/my`);
  }
  getMyFeedbackPaged(page: number, size: number): Observable<{ content: Feedback[], totalElements: number }> {
    return this.http.get<{ content: Feedback[], totalElements: number }>(
      `/api/feedback/my?page=${page}&size=${size}`
    );
  }

  getForLandlord(page = 0, size = 10): Observable<any> {
    return this.http.get<Feedback>(`${this.api}/landlord?page=${page}&size=${size}&sort=createdAt,desc`);
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
}*/
@Injectable({ providedIn: 'root' })
export class FeedbackService {

  private api = 'http://localhost:8081/api/feedbacks';

  constructor(private http: HttpClient) {}

  /* ================= TENANT ================= */

  create(data: {
    title: string;
    content: string;
    attachmentUrl?: string;
  }): Observable<Feedback> {
    return this.http.post<Feedback>(this.api, data);
  }
  
  getMyFeedback(page = 0, size = 10): Observable<any> {
    return this.http.get<any>(
      `${this.api}/my?page=${page}&size=${size}`
    );
  }

  tenantConfirm(
    id: number,
    satisfied: boolean,
    tenantFeedback?: string
  ): Observable<Feedback> {
    return this.http.put<Feedback>(
      `${this.api}/${id}/confirm`,
      { satisfied, tenantFeedback }
    );
  }

  /* ================= LANDLORD ================= */

  getForLandlord(page = 0, size = 10): Observable<any> {
    return this.http.get<any>(
      `${this.api}/landlord?page=${page}&size=${size}`
    );
  }

  process(
    id: number,
    status: 'PROCESSING' | 'RESOLVED' | 'CANCELED',
    landlordNote?: string
  ): Observable<Feedback> {
    return this.http.put<Feedback>(
      `${this.api}/${id}/process`,
      { status, landlordNote }
    );
  }

  /* ================= DELETE ================= */

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.api}/${id}`);
  }
  
  cancel(id: number): Observable<Feedback> {
    console.log('Cancel ID:', id);
    return this.http.put<Feedback>(`${this.api}/${id}/cancel`, {});
    }

  tenantReject(id: number, feedback: string): Observable<Feedback> {
    return this.http.put<Feedback>(`${this.api}/${id}/tenant-reject`, { feedback });
  }
}
