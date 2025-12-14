import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Notification } from '../../models/notification.model'; 
import { NotificationService } from '../../services/notification.service'; 

@Component({
  selector: 'app-tenant-notification',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './tenant-notification.component.html',
  styleUrl: './tenant-notification.component.css'
})
export class TenantNotificationComponent implements OnInit {
  notifications: Notification[] = [];
  loading = true;
  errorMessage: string = '';

  constructor(private notificationService: NotificationService) {}

  ngOnInit(): void {
    this.loadNotifications();
  }

  loadNotifications(): void {
    this.loading = true;
    this.errorMessage = '';
    this.notificationService.getMyNotifications().subscribe({
      next: (data: Notification[]) => {
        this.notifications = data;
        this.loading = false;
      },
      error: (err: HttpErrorResponse) => {
        this.errorMessage = err.error?.message || 'Không tải được thông báo. Vui lòng thử lại.';
        this.loading = false;
        console.error(err);
      }
    });
  }

  markAsRead(notification: Notification): void {
    // Thêm kiểm tra nếu notification đang trong quá trình đánh dấu
    // Sử dụng [] notation để truy cập loading nếu nó không có trong interface Notification chính thức.
    if (notification.isRead || notification.loading) return; 

    const originalIsRead = notification.isRead;
    // Tạm thời cập nhật UI để phản hồi nhanh
    notification.isRead = true; 
    notification.loading = true; 

    this.notificationService.markAsRead(notification.id).subscribe({
      next: (updatedNotif: Notification) => {
        // API thành công
        notification.loading = false;
        console.log('Đã đánh dấu là Đã đọc:', notification.id);
      },
      error: (err: HttpErrorResponse) => {
        // API thất bại, đảo ngược trạng thái UI về ban đầu
        notification.isRead = originalIsRead;
        notification.loading = false;

        const errorMsg = err.error?.message || 'Không thể đánh dấu đã đọc. Vui lòng thử lại.';
        alert(errorMsg);
        
        console.error('Lỗi khi đánh dấu đã đọc:', err);
      }
    });
  }

  // Hàm tiện ích để hiển thị thời gian đã trôi qua giữ nguyên
  timeAgo(dateString: string): string {
    const now = new Date();
    const past = new Date(dateString);
    const diff = Math.abs(now.getTime() - past.getTime());
    const seconds = Math.floor(diff / 1000);
    const minutes = Math.floor(seconds / 60);
    const hours = Math.floor(minutes / 60);
    const days = Math.floor(hours / 24);

    if (days > 0) return `${days} ngày trước`;
    if (hours > 0) return `${hours} giờ trước`;
    if (minutes > 0) return `${minutes} phút trước`;
    return 'Vừa xong';
  }
}