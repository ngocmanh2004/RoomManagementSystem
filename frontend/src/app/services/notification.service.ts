import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { BehaviorSubject, Observable } from 'rxjs';
import { map, tap } from 'rxjs/operators';
import {
  Notification,
  SendNotificationRequest,
  SendNotificationResponse
} from '../models/notification.model';

@Injectable({
  providedIn: 'root'
})
export class NotificationService {

  private apiUrl = 'http://localhost:8081/api/notifications';

  // BehaviorSubject để share unread count across components
  private unreadCountSubject = new BehaviorSubject<number>(0);
  public unreadCount$ = this.unreadCountSubject.asObservable();

  constructor(private http: HttpClient) {}

  // =====================================================
  // LẤY TẤT CẢ NOTIFICATIONS (KHÔNG PHÂN TRANG)
  // Dùng cho dropdown header
  // =====================================================
  getNotificationsByUserId(userId: number): Observable<Notification[]> {
    return this.http.get<Notification[]>(`${this.apiUrl}/user/${userId}`);
  }

  // =====================================================
  // LẤY NOTIFICATIONS PHÂN TRANG
  // Dùng cho trang chi tiết
  // =====================================================
  getMyNotificationsPaged(
    page: number,
    size: number
  ): Observable<{
    content: Notification[];
    totalElements: number;
    totalPages: number;
    number: number;
  }> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    return this.http.get<any>(`${this.apiUrl}/my/paged`, { params }).pipe(
      map(res => ({
        content: res.content || [],
        totalElements: res.totalElements || 0,
        totalPages: res.totalPages || 0,
        number: res.number || 0
      }))
    );
  }

  // =====================================================
  // ĐẾM SỐ NOTIFICATIONS CHƯA ĐỌC
  // =====================================================
  getUnreadCount(userId: number): Observable<number> {
    return this.http.get<{ count: number }>(
      `${this.apiUrl}/user/${userId}/unread/count`
    ).pipe(
      map(res => res.count || 0)
    );
  }

  // =====================================================
  // REFRESH UNREAD COUNT VÀ CẬP NHẬT BEHAVIORSUBJECT
  // Gọi trong header component để update badge
  // =====================================================
  refreshUnreadCount(userId: number): void {
    this.getUnreadCount(userId).subscribe({
      next: (count) => {
        this.unreadCountSubject.next(count);
      },
      error: (err) => {
        console.error('Error refreshing unread count:', err);
      }
    });
  }

  // =====================================================
  // ĐÁNH DẤU 1 NOTIFICATION ĐÃ ĐỌC
  // =====================================================
  markAsRead(id: number): Observable<Notification> {
    return this.http.put<Notification>(`${this.apiUrl}/${id}/read`, {});
  }

  // =====================================================
  // ĐÁNH DẤU TẤT CẢ ĐÃ ĐỌC
  // =====================================================
  markAllAsRead(userId: number): Observable<any> {
    return this.http.put(`${this.apiUrl}/user/${userId}/read-all`, {});
  }

  // =====================================================
  // XÓA NOTIFICATION
  // =====================================================
  deleteNotification(id: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/${id}`);
  }

  // =====================================================
  // GỬI NOTIFICATION HÀNG LOẠT (CHO LANDLORD)
  // =====================================================
  send(req: SendNotificationRequest): Observable<SendNotificationResponse> {
    return this.http.post<SendNotificationResponse>(`${this.apiUrl}/send`, req);
  }

  // =====================================================
  // TIỆN ÍCH GỬI HÀNG LOẠT
  // =====================================================
  sendToRooms(
    title: string,
    message: string,
    roomIds: number[],
    sendEmail = false
  ): Observable<SendNotificationResponse> {
    return this.send({
      title,
      message,
      sendTo: 'ROOMS',
      roomIds,
      sendEmail
    });
  }

  sendToAllTenants(
    title: string,
    message: string,
    sendEmail = false
  ): Observable<SendNotificationResponse> {
    return this.send({
      title,
      message,
      sendTo: 'ALL_TENANTS',
      sendEmail
    });
  }

  sendToUsers(
    title: string,
    message: string,
    userIds: number[],
    sendEmail = false
  ): Observable<SendNotificationResponse> {
    return this.send({
      title,
      message,
      sendTo: 'USERS',
      userIds,
      sendEmail
    });
  }
}