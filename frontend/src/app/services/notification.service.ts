import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { Notification, SendNotificationRequest, SendNotificationResponse } from '../models/notification.model';

@Injectable({
  providedIn: 'root'
})
export class NotificationService {
  private apiUrl = 'http://localhost:8081/api/notifications';

  constructor(private http: HttpClient) {}

  /**
   * Gửi thông báo
   * @param req dữ liệu gửi
   * @returns Observable<SendNotificationResponse>
   */
  send(req: SendNotificationRequest): Observable<SendNotificationResponse> {
    return this.http.post<SendNotificationResponse>(`${this.apiUrl}/send`, req);
  }

  /**
   * Lấy danh sách thông báo của user hiện tại (nếu có API riêng thì dùng)
   */
  getMyNotifications(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/my`); // bạn có thể tạo endpoint này sau
  }

  // =====================================================
  // CÁC HÀM TIỆN ÍCH (rất hay dùng trong component)
  // =====================================================

  /** Gửi thông báo cho nhiều phòng – trả về Promise dễ dùng với async/await */
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

  /** Gửi cho tất cả khách đang thuê */
  sendToAllTenants(title: string, message: string, sendEmail = false): Observable<SendNotificationResponse> {
    return this.send({
      title,
      message,
      sendTo: 'ALL', // hoặc 'ALL_TENANTS' tùy bạn set backend
      sendEmail
    });
  }

  /** Gửi cho danh sách user cụ thể */
  sendToUsers(title: string, message: string, userIds: number[], sendEmail = false): Observable<SendNotificationResponse> {
    return this.send({
      title,
      message,
      sendTo: 'USERS',
      userIds,
      sendEmail
    });
  }
  markAsRead(id: number) {
    return this.http.post<Notification>(`${this.apiUrl}/${id}/read`,{});
    }
}