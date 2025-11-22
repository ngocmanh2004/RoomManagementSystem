import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
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
    let headers = new HttpHeaders({
      'Content-Type': 'application/json'
    });

    const token = localStorage.getItem('accessToken');
    if (token) {
      headers = headers.set('Authorization', `Bearer ${token}`);
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
    );
  }

  createReview(request: ReviewRequest): Observable<Review> {
    if (!request.roomId || request.roomId <= 0) {
      throw new Error('roomId là bắt buộc');
    }
    if (!request.rating || request.rating < 1 || request.rating > 5) {
      throw new Error('rating phải từ 1 đến 5');
    }

    return this.http.post<Review>(
      this.apiUrl,
      request,
      { headers: this.getHeaders() }
    );
  }

  updateReview(id: number, request: ReviewRequest): Observable<Review> {
    if (!request.rating || request.rating < 1 || request.rating > 5) {
      throw new Error('rating phải từ 1 đến 5');
    }

    return this.http.put<Review>(
      `${this.apiUrl}/${id}`,
      request,
      { headers: this.getHeaders() }
    );
  }

  deleteReview(id: number): Observable<ApiResponse<void>> {
    return this.http.delete<ApiResponse<void>>(
      `${this.apiUrl}/${id}`,
      { headers: this.getHeaders() }
    );
  }
}