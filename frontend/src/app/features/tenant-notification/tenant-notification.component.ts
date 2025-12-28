import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Subscription, interval } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { HttpErrorResponse } from '@angular/common/http';
import { Notification } from '../../models/notification.model';
import { NotificationService } from '../../services/notification.service';

@Component({
  selector: 'app-tenant-notification',
  imports: [CommonModule, FormsModule],
  templateUrl: './tenant-notification.component.html',
  styleUrls: ['./tenant-notification.component.css']
})
export class TenantNotificationComponent implements OnInit, OnDestroy {
  notifications: Notification[] = [];
  loading = true;
  errorMessage: string = '';

  currentPage = 1;
  pageSize = 5;
  totalItems = 0;
  totalPages = 0;
  
  private autoReloadSub?: Subscription;

  constructor(private notificationService: NotificationService) {}

  ngOnInit(): void {
    this.loadState();
    this.loadNotifications(this.currentPage);

    // Polling mỗi 5 giây, chỉ reload page hiện tại
    this.autoReloadSub = interval(5000).pipe(
      switchMap(() => this.notificationService.getMyNotificationsPaged(this.currentPage - 1, this.pageSize))
    ).subscribe({
      next: res => {
        if (JSON.stringify(this.notifications) !== JSON.stringify(res.content)) {
          this.notifications = res.content;
          this.totalItems = res.totalElements;
          this.totalPages = Math.ceil(this.totalItems / this.pageSize);
          this.saveState();
        }
      },
      error: err => {
        console.error('Polling error:', err);
      }
    });
  }

  ngOnDestroy(): void {
    this.autoReloadSub?.unsubscribe();
  }

  loadNotifications(page: number = 1): void {
    this.loading = true;
    this.errorMessage = '';
    this.currentPage = page;

    this.notificationService.getMyNotificationsPaged(this.currentPage - 1, this.pageSize)
      .subscribe({
        next: res => {
          this.notifications = res.content || [];
          this.totalItems = res.totalElements;
          this.totalPages = Math.ceil(this.totalItems / this.pageSize);
          this.loading = false;
          this.saveState();
        },
        error: (err: HttpErrorResponse) => {
          this.errorMessage = err.error?.message || 'Không tải được thông báo.';
          this.loading = false;
        }
      });
  }

  markAsRead(notification: Notification): void {
    if (notification.isRead || (notification as any).loading) return;

    const originalIsRead = notification.isRead;
    notification.isRead = true;
    (notification as any).loading = true;

    this.notificationService.markAsRead(notification.id).subscribe({
      next: updatedNotif => {
        (notification as any).loading = false;
        Object.assign(notification, updatedNotif);
      },
      error: (err: HttpErrorResponse) => {
        notification.isRead = originalIsRead;
        (notification as any).loading = false;
        this.showErrorAlert(err.error?.message || 'Không thể đánh dấu đã đọc.');
      }
    });
  }

  goToPage(page: number) {
    if (page < 1 || page > this.totalPages) return;
    this.loadNotifications(page);
  }

  get pages(): number[] {
    return Array.from({ length: this.totalPages }, (_, i) => i + 1);
  }

  private saveState(): void {
    const state = {
      notifications: this.notifications,
      currentPage: this.currentPage,
      pageSize: this.pageSize,
      totalItems: this.totalItems,
      totalPages: this.totalPages
    };
    localStorage.setItem('tenantNotifications', JSON.stringify(state));
  }

  private loadState(): void {
    const saved = localStorage.getItem('tenantNotifications');
    if (saved) {
      try {
        const state = JSON.parse(saved);
        this.notifications = state.notifications || [];
        this.currentPage = state.currentPage || 1;
        this.pageSize = state.pageSize || 5;
        this.totalItems = state.totalItems || 0;
        this.totalPages = state.totalPages || 0;
        this.loading = false;
      } catch {
        this.currentPage = 1;
      }
    }
  }

  private showErrorAlert(message: string) {
    this.errorMessage = message;
    setTimeout(() => this.errorMessage = '', 3000);
  }

  timeAgo(dateString: string): string {
    const now = new Date();
    const past = new Date(dateString);
    if (isNaN(past.getTime())) return 'Thời gian không hợp lệ';

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
