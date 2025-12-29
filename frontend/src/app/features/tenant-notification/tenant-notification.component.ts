import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { Subscription } from 'rxjs';
import { NotificationService } from '../../services/notification.service';
import { Notification } from '../../models/notification.model';

@Component({
  selector: 'app-tenant-notification',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './tenant-notification.component.html',
  styleUrls: ['./tenant-notification.component.css']
})
export class TenantNotificationComponent implements OnInit, OnDestroy {
  notifications: Notification[] = [];
  filteredNotifications: Notification[] = [];
  loading = false;
  currentPage = 0;
  pageSize = 5;
  totalPages = 0;
  totalElements = 0;
  
  filterTab: 'all' | 'unread' | 'read' = 'all';
  
  private subscription?: Subscription;

  constructor(private notificationService: NotificationService) {}
  
  ngOnInit() {
    this.loadNotifications();
  }

  ngOnDestroy() {
    this.subscription?.unsubscribe();
  }

  loadNotifications() {
    this.loading = true;
    this.notificationService.getMyNotificationsPaged(this.currentPage, this.pageSize)
      .subscribe({
        next: (response) => {
          this.notifications = response.content;
          this.totalPages = response.totalPages;
          this.totalElements = response.totalElements;
          this.currentPage = response.number;
          this.applyFilter();
          this.loading = false;
        },
        error: (err) => {
          console.error('Error loading notifications:', err);
          this.loading = false;
        }
      });
  }

  applyFilter() {
    switch (this.filterTab) {
      case 'unread':
        this.filteredNotifications = this.notifications.filter(n => !n.isRead);
        break;
      case 'read':
        this.filteredNotifications = this.notifications.filter(n => n.isRead);
        break;
      default:
        this.filteredNotifications = [...this.notifications];
    }
  }

  setFilter(tab: 'all' | 'unread' | 'read') {
    this.filterTab = tab;
    this.applyFilter();
  }

  markAsRead(notification: Notification) {
    if (notification.isRead) return;
    
    this.notificationService.markAsRead(notification.id).subscribe({
      next: () => {
        notification.isRead = true;
        this.applyFilter();
      },
      error: (err) => console.error('Error marking as read:', err)
    });
  }

  deleteNotification(id: number, event: Event) {
    event.stopPropagation();
    
    if (!confirm('Bạn có chắc muốn xóa thông báo này?')) return;
    
    this.notificationService.deleteNotification(id).subscribe({
      next: () => {
        this.loadNotifications();
      },
      error: (err) => console.error('Error deleting notification:', err)
    });
  }

  markAllAsRead() {
    const userId = JSON.parse(localStorage.getItem('currentUser') || '{}').id;
    if (!userId) return;

    this.notificationService.markAllAsRead(userId).subscribe({
      next: () => {
        this.loadNotifications();
      },
      error: (err) => console.error('Error marking all as read:', err)
    });
  }

  nextPage() {
    if (this.currentPage < this.totalPages - 1) {
      this.currentPage++;
      this.loadNotifications();
    }
  }

  previousPage() {
    if (this.currentPage > 0) {
      this.currentPage--;
      this.loadNotifications();
    }
  }

  getNotificationIcon(type: string): string {
    const icons: { [key: string]: string } = {
      'INVOICE_CREATED': '💰',
      'INVOICE_REMINDER': '⏰',
      'PAYMENT_OVERDUE': '⚠️',
      'UTILITY_REQUEST': '⚡',
      'FEEDBACK_PROCESSING': '⏳',
      'FEEDBACK_RESOLVED': '✅',
      'FEEDBACK_CANCELLED': '🚫',
      'SYSTEM': '🔔',
      'CONTRACT_EXPIRED': '⏰',
    };
    return icons[type] || '🔔';
  }

  getNotificationColor(type: string): string {
    if (['PAYMENT_OVERDUE', 'CONTRACT_EXPIRED'].includes(type)) return 'red';
    if (['INVOICE_REMINDER', 'UTILITY_REQUEST'].includes(type)) return 'orange';
    if (['FEEDBACK_RESOLVED'].includes(type)) return 'green';
    return 'blue';
  }

  getNotificationTypeLabel(type: string): string {
    const labels: { [key: string]: string } = {
      'INVOICE_CREATED': 'Hóa đơn',
      'INVOICE_REMINDER': 'Nhắc nhở',
      'PAYMENT_OVERDUE': 'Quá hạn',
      'UTILITY_REQUEST': 'Tiện ích',
      'FEEDBACK_PROCESSING': 'Phản hồi',
      'FEEDBACK_RESOLVED': 'Đã xử lý',
      'FEEDBACK_CANCELLED': 'Đã hủy',
      'SYSTEM': 'Hệ thống',
      'CONTRACT_EXPIRED': 'Hợp đồng',
    };
    return labels[type] || 'Thông báo';
  }

  get unreadCount(): number {
    return this.notifications.filter(n => !n.isRead).length;
  }

  get readCount(): number {
    return this.notifications.filter(n => n.isRead).length;
  }
}