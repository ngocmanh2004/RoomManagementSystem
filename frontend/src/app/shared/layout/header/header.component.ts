import { Component, OnInit, OnDestroy, HostListener, ElementRef } from '@angular/core';
import { Subscription, interval } from 'rxjs';
import { CommonModule } from '@angular/common';
import { Router, RouterLink, RouterLinkActive, RouterModule } from '@angular/router';
import { AuthService } from '../../../services/auth.service';
import { NotificationService } from '../../../services/notification.service';
import { Notification, getNotificationIcon, isImportantNotification } from '../../../models/notification.model';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [CommonModule, RouterModule, RouterLink, RouterLinkActive],
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css']
})
export class HeaderComponent implements OnInit, OnDestroy {
  isLoggedIn = false;
  currentUser: any = null;

  // Notifications
  unreadCount = 0;
  showNotifications = false;
  notifications: Notification[] = [];
  notificationLoading = false;

  private userSub?: Subscription;
  private unreadSub?: Subscription;
  private pollSub?: Subscription;

  constructor(
    public authService: AuthService,
    private router: Router,
    private notificationService: NotificationService,
    private elRef: ElementRef
  ) {}

  ngOnInit() {
    this.userSub = this.authService.currentUser$.subscribe(user => {
      this.currentUser = user;
      this.isLoggedIn = !!user;

      // Cleanup previous subscriptions
      this.unreadSub?.unsubscribe();
      this.pollSub?.unsubscribe();

      if (user) {
        // Initial load unread count
        this.notificationService.refreshUnreadCount(user.id);
        
        // Subscribe to unread count changes
        this.unreadSub = this.notificationService.unreadCount$.subscribe(
          count => this.unreadCount = count
        );

        // Polling every 20 seconds to check for new notifications
        this.pollSub = interval(20000).subscribe(() => {
          this.notificationService.refreshUnreadCount(user.id);
        });
      } else {
        this.unreadCount = 0;
      }
    });
  }

  ngOnDestroy(): void {
    this.userSub?.unsubscribe();
    this.unreadSub?.unsubscribe();
    this.pollSub?.unsubscribe();
  }

  // Close dropdown when clicking outside
  @HostListener('document:click', ['$event'])
  onDocumentClick(event: Event) {
    if (!this.showNotifications) return;
    const clickedInside = this.elRef.nativeElement.contains(event.target);
    if (!clickedInside) {
      this.showNotifications = false;
    }
  }

  // Toggle notification dropdown
  toggleNotifications(event: Event) {
    event.stopPropagation();
    this.showNotifications = !this.showNotifications;
    
    if (this.showNotifications) {
      this.loadLatestNotifications();
    }
  }

  // Track expanded notifications by id
  private expandedIds = new Set<number>();

  isExpanded(n: Notification): boolean {
    return this.expandedIds.has(n.id);
  }

  toggleExpanded(n: Notification) {
    if (this.isExpanded(n)) {
      this.expandedIds.delete(n.id);
    } else {
      this.expandedIds.add(n.id);
    }
  }

  // Get icon for notification type
  getNotificationIcon(type: string): string {
    return getNotificationIcon(type as any);
  }

  // Check if notification is important
  isImportant(n: Notification): boolean {
    return isImportantNotification(n.type as any);
  }

  // Load latest 6 notifications for dropdown
  loadLatestNotifications() {
    if (!this.currentUser) return;
    
    this.notificationLoading = true;
    this.notificationService.getNotificationsByUserId(this.currentUser.id).subscribe({
      next: (data) => {
        this.notifications = data.slice(0, 6); // Chỉ lấy 6 notifications mới nhất
        this.notificationLoading = false;

        // Auto-mark visible unread notifications as read
        const unread = this.notifications.filter(n => !n.isRead);
        if (unread.length > 0) {
          unread.forEach(n => {
            this.notificationService.markAsRead(n.id).subscribe({
              next: () => {
                n.isRead = true;
                this.notificationService.refreshUnreadCount(this.currentUser.id);
              },
              error: (err) => console.error('Error marking notif read:', err)
            });
          });
        }
      },
      error: (err) => {
        console.error('Lỗi tải thông báo (header):', err);
        this.notificationLoading = false;
      }
    });
  }

  // Click on notification
  onNotificationClick(n: Notification, event?: Event) {
    if (event) event.stopPropagation();
    
    if (!n.isRead) {
      this.notificationService.markAsRead(n.id).subscribe({
        next: () => {
          n.isRead = true;
          this.notificationService.refreshUnreadCount(this.currentUser.id);
        },
        error: (err) => console.error(err)
      });
    }
    
    this.toggleExpanded(n);
  }

  // Delete notification
  onDeleteNotification(id: number, event: Event) {
    event.stopPropagation();
    
    if (!confirm('Bạn có chắc muốn xóa thông báo này?')) return;
    
    this.notificationService.deleteNotification(id).subscribe({
      next: () => {
        this.notifications = this.notifications.filter(x => x.id !== id);
        if (this.currentUser) {
          this.notificationService.refreshUnreadCount(this.currentUser.id);
        }
      },
      error: (err) => console.error(err)
    });
  }

  // Mark all as read
  markAll() {
    if (!this.currentUser) return;
    
    this.notificationService.markAllAsRead(this.currentUser.id).subscribe({
      next: () => {
        // Reload from server and update UI
        this.loadLatestNotifications();
        this.notificationService.refreshUnreadCount(this.currentUser.id);
      },
      error: (err) => console.error(err)
    });
  }

  // Short preview message (max 60 chars)
  shortMessage(n: Notification): string {
    if (!n.message) return '';
    return n.message.length > 60 ? n.message.substring(0, 60) + '...' : n.message;
  }

  // Check if has unread
  get hasUnread(): boolean {
    return this.unreadCount > 0 || this.notifications.some(n => !n.isRead);
  }

  // Navigate to full notifications page
  openAllNotifications() {
    if (!this.currentUser) return;
    
    // Navigate based on role
    if (this.currentUser.role === 0) {
      this.router.navigate(['/admin/notifications']);
    } else if (this.currentUser.role === 1) {
      this.router.navigate(['/landlord/landlord-feedback']);
    } else {
      this.router.navigate(['/notification']);
    }
    
    this.showNotifications = false;
  }

  logout() {
    if (confirm('Bạn có chắc chắn muốn đăng xuất?')) {
      this.authService.logout().subscribe({
        next: () => {
          alert('Đăng xuất thành công!');
          this.router.navigate(['/']);
        },
        error: (err) => {
          console.error('Logout error:', err);
          this.router.navigate(['/']);
        }
      });
    }
  }

  getRoleName(role: number): string {
    switch (role) {
      case 0: return 'Admin';
      case 1: return 'Chủ trọ';
      case 2: return 'Khách thuê';
      default:
        console.error('Invalid role:', role);
        return 'Không xác định';
    }
  }

  getDashboardLink(): string {
    if (!this.currentUser) return '/';

    switch (this.currentUser.role) {
      case 0: return '/admin/dashboard';
      case 1: return '/landlord/dashboard';
      case 2: return '/tenant/dashboard';
      default:
        console.error('Invalid role for dashboard:', this.currentUser.role);
        return '/';
    }
  }
}