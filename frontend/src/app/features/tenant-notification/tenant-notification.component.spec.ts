import { ComponentFixture, TestBed } from '@angular/core/testing';
import { TenantNotificationComponent } from './tenant-notification.component';
import { NotificationService } from '../../services/notification.service';
import { of, throwError } from 'rxjs';
import { CommonModule } from '@angular/common';

/* ================= MOCK DATA ================= */

const mockNotifications = [
  {
    id: 1,
    title: 'Hóa đơn tháng 6',
    message: 'Hóa đơn tiền điện',
    type: 'INVOICE_CREATED',
    isRead: false,
    createdAt: '2024-06-01'
  },
  {
    id: 2,
    title: 'Phản hồi đã xử lý',
    message: 'Chủ trọ đã phản hồi',
    type: 'FEEDBACK_RESOLVED',
    isRead: true,
    createdAt: '2024-06-02'
  }
] as any;

const mockPagedResponse = {
  content: mockNotifications,
  totalPages: 1,
  totalElements: 2,
  number: 0
};

/* ================= MOCK SERVICE ================= */

class MockNotificationService {
  getMyNotificationsPaged() {
    return of(mockPagedResponse);
  }

  markAsRead() {
    return of({});
  }

  markAllAsRead() {
    return of({});
  }

  deleteNotification() {
    return of({});
  }
}

/* ================= TEST SUITE ================= */

describe('TenantNotificationComponent', () => {
  let component: TenantNotificationComponent;
  let fixture: ComponentFixture<TenantNotificationComponent>;
  let service: NotificationService;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        CommonModule,
        TenantNotificationComponent
      ],
      providers: [
        { provide: NotificationService, useClass: MockNotificationService }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(TenantNotificationComponent);
    component = fixture.componentInstance;
    service = TestBed.inject(NotificationService);
    fixture.detectChanges();
  });

  /* ========== BASIC ========== */

  it('should create component', () => {
    expect(component).toBeTruthy();
  });

  /* ========== LOAD NOTIFICATIONS ========== */

  it('should load notifications on init', () => {
    expect(component.notifications.length).toBe(2);
    expect(component.filteredNotifications.length).toBe(2);
    expect(component.loading).toBeFalse();
  });

  it('should handle error when loading notifications', () => {
    spyOn(service, 'getMyNotificationsPaged')
      .and.returnValue(throwError(() => new Error('Load error')));

    component.loadNotifications();
    expect(component.loading).toBeFalse();
  });

  /* ========== FILTER ========== */

  it('should filter unread notifications', () => {
    component.notifications = mockNotifications;
    component.setFilter('unread');

    expect(component.filteredNotifications.length).toBe(1);
  });

  it('should filter read notifications', () => {
    component.notifications = mockNotifications;
    component.setFilter('read');

    expect(component.filteredNotifications.length).toBe(1);
  });

  it('should show all notifications', () => {
    component.notifications = mockNotifications;
    component.setFilter('all');

    expect(component.filteredNotifications.length).toBe(2);
  });

  /* ========== MARK AS READ ========== */

  it('should mark notification as read', () => {
    const notification = { ...mockNotifications[0] };
    spyOn(service, 'markAsRead').and.callThrough();

    component.markAsRead(notification);

    expect(service.markAsRead).toHaveBeenCalled();
    expect(notification.isRead).toBeTrue();
  });

  it('should not mark already read notification', () => {
    spyOn(service, 'markAsRead');
    component.markAsRead(mockNotifications[1]);
    expect(service.markAsRead).not.toHaveBeenCalled();
  });

  /* ========== MARK ALL AS READ ========== */

  it('should mark all notifications as read when user exists', () => {
    localStorage.setItem('currentUser', JSON.stringify({ id: 1 }));
    spyOn(service, 'markAllAsRead').and.callThrough();
    spyOn(component, 'loadNotifications');

    component.markAllAsRead();

    expect(service.markAllAsRead).toHaveBeenCalled();
    expect(component.loadNotifications).toHaveBeenCalled();
  });

  it('should not mark all as read if userId missing', () => {
    localStorage.removeItem('currentUser');
    spyOn(service, 'markAllAsRead');

    component.markAllAsRead();
    expect(service.markAllAsRead).not.toHaveBeenCalled();
  });

  /* ========== PAGINATION ========== */

  it('should go to next page', () => {
    spyOn(component, 'loadNotifications');
    component.totalPages = 3;
    component.currentPage = 0;

    component.nextPage();
    expect(component.currentPage).toBe(1);
    expect(component.loadNotifications).toHaveBeenCalled();
  });

  it('should go to previous page', () => {
    spyOn(component, 'loadNotifications');
    component.currentPage = 1;

    component.previousPage();
    expect(component.currentPage).toBe(0);
    expect(component.loadNotifications).toHaveBeenCalled();
  });

  /* ========== UTIL METHODS ========== */

  it('should return correct icon', () => {
    expect(component.getNotificationIcon('INVOICE_CREATED')).toBe('💰');
  });

  it('should return correct color', () => {
    expect(component.getNotificationColor('PAYMENT_OVERDUE')).toBe('red');
  });

  it('should return correct label', () => {
    expect(component.getNotificationTypeLabel('SYSTEM')).toBe('Hệ thống');
  });

  /* ========== GETTERS ========== */

  it('should calculate unreadCount correctly', () => {
    component.notifications = mockNotifications;
    expect(component.unreadCount).toBe(1);
  });

  it('should calculate readCount correctly', () => {
    component.notifications = mockNotifications;
    expect(component.readCount).toBe(1);
  });

  /* ========== DESTROY ========== */

  it('should unsubscribe on destroy', () => {
    const sub = jasmine.createSpyObj('Subscription', ['unsubscribe']);
    component['subscription'] = sub;

    component.ngOnDestroy();
    expect(sub.unsubscribe).toHaveBeenCalled();
  });
});
