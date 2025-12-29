import { Component, EventEmitter, Output, OnInit, OnDestroy, HostListener, ElementRef } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../../services/auth.service';
import { NotificationService } from '../../../services/notification.service';
import { Notification } from '../../../models/notification.model';
import { Subscription, interval } from 'rxjs';

interface User {
  id: number;
  username: string;
  fullName: string;
  email: string;
  role: number;
  roleName: string;
}

@Component({
  selector: 'app-admin-header',
  standalone: true,
  imports: [RouterModule, CommonModule],
  templateUrl: './admin-header.component.html',
  styleUrl: './admin-header.component.css'
})
export class AdminHeaderComponent implements OnInit, OnDestroy {
  @Output() toggleSidebar = new EventEmitter<void>();
  
  currentUser: User | null = null;
  showUserDropdown = false;

  // Notifications
  unreadCount = 0;
  showNotifications = false;
  notifications: Notification[] = [];
  notificationLoading = false;

  private unreadSub?: Subscription;
  private pollSub?: Subscription;

  constructor(
    private router: Router,
    private authService: AuthService,
    private notificationService: NotificationService,
    private elRef: ElementRef
  ) {}

  ngOnInit() {
    this.loadCurrentUser();
    
    if (this.currentUser) {
      // Initial load unread count
      this.notificationService.refreshUnreadCount(this.currentUser.id);
      
      // Subscribe to unread count changes
      this.unreadSub = this.notificationService.unreadCount$.subscribe(
        count => this.unreadCount = count
      );

      // Polling every 20 seconds
      this.pollSub = interval(20000).subscribe(() => {
        if (this.currentUser) {
          this.notificationService.refreshUnreadCount(this.currentUser.id);
        }
      });
    }
  }

  ngOnDestroy(): void {
    this.unreadSub?.unsubscribe();
    this.pollSub?.unsubscribe();
  }

  loadCurrentUser() {
    const userStr = localStorage.getItem('currentUser');
    if (userStr) {
      this.currentUser = JSON.parse(userStr);
    }
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

  onToggleSidebar() {
    this.toggleSidebar.emit();
  }

  toggleUserDropdown() {
    this.showUserDropdown = !this.showUserDropdown;
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

  // Load latest 6 notifications for dropdown
  loadLatestNotifications() {
    if (!this.currentUser) return;
    
    this.notificationLoading = true;
    this.notificationService.getNotificationsByUserId(this.currentUser.id).subscribe({
      next: (data) => {
        this.notifications = data.slice(0, 6);
        this.notificationLoading = false;

        // Auto-mark visible unread notifications as read
        const unread = this.notifications.filter(n => !n.isRead);
        if (unread.length > 0) {
          unread.forEach(n => {
            this.notificationService.markAsRead(n.id).subscribe({
              next: () => {
                n.isRead = true;
                if (this.currentUser) {
                  this.notificationService.refreshUnreadCount(this.currentUser.id);
                }
              },
              error: (err) => console.error('Error marking notif read:', err)
            });
          });
        }
      },
      error: (err) => {
        console.error('Lỗi tải thông báo (admin header):', err);
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
          if (this.currentUser) {
            this.notificationService.refreshUnreadCount(this.currentUser.id);
          }
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
        this.loadLatestNotifications();
        if (this.currentUser) {
          this.notificationService.refreshUnreadCount(this.currentUser.id);
        }
      },
      error: (err) => console.error(err)
    });
  }

  // Short preview message
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
    this.router.navigate(['/admin/notifications']);
    this.showNotifications = false;
  }

  getRoleName(role: number): string {
    switch(role) {
      case 0: return 'Admin';
      case 1: return 'Chủ trọ';
      case 2: return 'Khách thuê';
      default: return 'User';
    }
  }

  goToHome() {
    this.router.navigate(['/']);
    this.showUserDropdown = false;
  }

  goToProfile() {
    this.router.navigate(['/profile']);
    this.showUserDropdown = false;
  }

  logout() {
    this.authService.logout().subscribe({
      next: () => {
        this.router.navigate(['/login']);
      },
      error: err => {
        console.error(err);
        this.router.navigate(['/login']);
      }
    });
  }
}