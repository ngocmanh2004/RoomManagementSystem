import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
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

  constructor(private http: HttpClient) {}

  // =====================================================
  // SEND NOTIFICATION
  // =====================================================
  send(req: SendNotificationRequest): Observable<SendNotificationResponse> {
    return this.http.post<SendNotificationResponse>(
      `${this.apiUrl}/send`,
      req
    );
  }

  // =====================================================
  // GET MY NOTIFICATIONS (PAGED)
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

    return this.http.get<any>(
      `${this.apiUrl}/my/paged`,
      { params }
    ).pipe(
      map(res => ({
        content: res.content || [],
        totalElements: res.totalElements || 0,
        totalPages: res.totalPages || 0,
        number: res.number || 0
      }))
    );
  }

  // =====================================================
  // MARK AS READ
  // =====================================================
  markAsRead(id: number): Observable<Notification> {
    return this.http.put<Notification>(
      `${this.apiUrl}/${id}/read`,
      {}
    );
  }

  // =====================================================
  // OPTIONAL – COUNT UNREAD (nếu backend có)
  // =====================================================
  getUnreadCount(): Observable<number> {
    return this.http.get<number>(`${this.apiUrl}/my/unread-count`);
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
