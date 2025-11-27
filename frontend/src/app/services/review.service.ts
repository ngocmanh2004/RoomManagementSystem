import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError, tap } from 'rxjs/operators';
import { 
  Review, 
  ReviewRequest, 
  ReviewResponse, 
  ApiResponse,
  ErrorResponse 
} from '../models/review.model';

@Injectable({
  providedIn: 'root'
})
export class ReviewService {
  private apiUrl = '/api/reviews';

  constructor(private http: HttpClient) {}

  private getHeaders(): HttpHeaders {
    const token = localStorage.getItem('accessToken');
    console.log('Token in storage:', token ? 'EXISTS' : 'MISSING');
    
    let headers = new HttpHeaders({
      'Content-Type': 'application/json'
    });

    if (token) {
      headers = headers.set('Authorization', `Bearer ${token}`);
      console.log('Authorization header added');
    }
    
    return headers;
  }

  getReviewsByRoom(roomId: number, page: number = 0, size: number = 10): Observable<ReviewResponse> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    return this.http.get<ReviewResponse>(
      `${this.apiUrl}/room/${roomId}`,
      { params, headers: this.getHeaders() }
    ).pipe(
      catchError(error => {
        console.error('Error loading reviews:', error);
        return throwError(() => new Error(error.error?.message || 'Lỗi tải đánh giá'));
      })
    );
  }

  createReview(request: ReviewRequest): Observable<Review> {
    const roomId = parseInt(String(request.roomId), 10);
    const rating = parseInt(String(request.rating), 10);
    const comment = (request.comment || '').trim();

    const payload = {
      roomId: roomId,
      rating: rating,
      comment: comment
    };

    console.log('Creating review - Payload:', JSON.stringify(payload));
    console.log('Headers:', this.getHeaders());

    return this.http.post<Review>(
      this.apiUrl,
      payload,
      { headers: this.getHeaders() }
    ).pipe(
      tap(response => {
        console.log('Review created successfully:', response);
      }),
      catchError(error => {
        console.error('Create error:', error);
        const message = error.error?.message || error.statusText || 'Lỗi tạo đánh giá';
        return throwError(() => new Error(message));
      })
    );
  }

  updateReview(id: number, request: ReviewRequest): Observable<Review> {
    const rating = parseInt(String(request.rating), 10);
    const comment = (request.comment || '').trim();

    const payload = {
      roomId: parseInt(String(request.roomId), 10) || 0,
      rating: rating,
      comment: comment
    };

    console.log('Updating review - ID:', id, 'Payload:', JSON.stringify(payload));
    console.log('Headers:', this.getHeaders());

    return this.http.put<Review>(
      `${this.apiUrl}/${id}`,
      payload,
      { headers: this.getHeaders() }
    ).pipe(
      tap(response => {
        console.log('Review updated successfully:', response);
      }),
      catchError(error => {
        console.error('Update error:', error);
        const message = error.error?.message || error.statusText || 'Lỗi cập nhật đánh giá';
        return throwError(() => new Error(message));
      })
    );
  }

  deleteReview(id: number): Observable<ApiResponse<void>> {
    console.log('Deleting review - ID:', id);
    console.log('Headers:', this.getHeaders());

    return this.http.delete<ApiResponse<void>>(
      `${this.apiUrl}/${id}`,
      { headers: this.getHeaders() }
    ).pipe(
      tap(() => {
        console.log('Review deleted successfully');
      }),
      catchError(error => {
        console.error('Delete error:', error);
        const message = error.error?.message || error.statusText || 'Lỗi xóa đánh giá';
        return throwError(() => new Error(message));
      })
    );
  }
}