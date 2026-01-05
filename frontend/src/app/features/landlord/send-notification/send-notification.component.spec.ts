import { ComponentFixture, TestBed } from '@angular/core/testing';
import { SendNotificationComponent } from './send-notification.component';
import { NotificationService } from '../../../services/notification.service';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { of, throwError } from 'rxjs';

describe('SendNotificationComponent', () => {
  let component: SendNotificationComponent;
  let fixture: ComponentFixture<SendNotificationComponent>;
  let notificationService: jasmine.SpyObj<NotificationService>;
  let httpMock: HttpTestingController;

  beforeEach(async () => {
    const notificationSpy = jasmine.createSpyObj('NotificationService', [
      'send',
      'saveDraft',
      'getById',
      'resend'
    ]);

    await TestBed.configureTestingModule({
      imports: [
        SendNotificationComponent,
        HttpClientTestingModule
      ],
      providers: [
        { provide: NotificationService, useValue: notificationSpy }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(SendNotificationComponent);
    component = fixture.componentInstance;
    notificationService = TestBed.inject(NotificationService) as jasmine.SpyObj<NotificationService>;
    httpMock = TestBed.inject(HttpTestingController);

    fixture.detectChanges();

    // ✅ flush API gọi trong ngOnInit (CHỈ 1 LẦN)
    const req = httpMock.expectOne('http://localhost:8081/api/rooms/my');
    req.flush([]);
  });

  afterEach(() => {
    httpMock.verify();
  });

  afterEach(() => {
    httpMock.verify();
  });

  // =============================
  // INIT
  // =============================
  it('should create component', () => {
    expect(component).toBeTruthy();
  });

  it('should load rooms on init', () => {
    const mockRooms = [
    { id: 1, name: 'P101' },
    { id: 2, name: 'P102' }
  ];

  component.loadRooms(); // 🔥 trigger API lần 2

  const req = httpMock.expectOne('http://localhost:8081/api/rooms/my');
  req.flush(mockRooms);

  expect(component.rooms.length).toBe(2);
});

  // =============================
  // ROOM SELECTION
  // =============================
  it('should select and unselect a room', () => {
    component.toggleRoomSelection(1);
    expect(component.selectedRooms).toContain(1);

    component.toggleRoomSelection(1);
    expect(component.selectedRooms).not.toContain(1);
  });

  it('should select all rooms', () => {
    component.rooms = [
      { id: 1, name: 'P101' },
      { id: 2, name: 'P102' }
    ];

    component.selectAllRooms();
    expect(component.selectedRooms.length).toBe(2);

    component.selectAllRooms();
    expect(component.selectedRooms.length).toBe(0);
  });

  // =============================
  // SEND NOTIFICATION
  // =============================
  it('should NOT send notification if title or message empty', () => {
    spyOn(window, 'alert');

    component.notification.title = '';
    component.notification.message = '';

    component.sendNotification();

    expect(window.alert).toHaveBeenCalledWith('Vui lòng điền đầy đủ tiêu đề và nội dung!');
    expect(notificationService.send).not.toHaveBeenCalled();
  });

  it('should send notification successfully', () => {
    spyOn(window, 'alert');

    component.notification.title = 'Test';
    component.notification.message = 'Message';
    component.selectedRooms = [1, 2];

    notificationService.send.and.returnValue(
      of({
        success: true,
        message: 'OK',
        sentToCount: 2
      })
    );

    component.sendNotification();

    expect(notificationService.send).toHaveBeenCalled();
    expect(window.alert).toHaveBeenCalledWith('Gửi thông báo thành công đến 2 khách thuê!');
    expect(component.sending).toBeFalse();
  });

  it('should handle send notification error', () => {
    spyOn(window, 'alert');

    component.notification.title = 'Test';
    component.notification.message = 'Message';
    component.selectedRooms = [1];

    notificationService.send.and.returnValue(
      throwError(() => new Error('Send error'))
    );

    component.sendNotification();

    expect(window.alert).toHaveBeenCalledWith('Có lỗi xảy ra khi gửi thông báo!');
    expect(component.sending).toBeFalse();
  });

  // =============================
  // SAVE DRAFT
  // =============================
  it('should save draft successfully', () => {
    spyOn(window, 'alert');

    component.notification.title = 'Draft';
    component.notification.message = 'Draft content';

    notificationService.saveDraft.and.returnValue(of({}));

    component.saveDraft();

    expect(notificationService.saveDraft).toHaveBeenCalled();
    expect(window.alert).toHaveBeenCalledWith('Đã lưu nháp thành công');
  });

  // =============================
  // VIEW HISTORY
  // =============================
  it('should load notification history detail', () => {
    const mockData = {
      title: 'Old title',
      message: 'Old message',
      sendTo: 'ROOMS',
      roomIds: '[1,2]'
    };

    notificationService.getById.and.returnValue(of(mockData));

    component.viewHistory(1);

    expect(component.notification.title).toBe('Old title');
    expect(component.selectedRooms.length).toBe(2);
  });

  // =============================
  // RESEND
  // =============================
  it('should resend notification', () => {
    spyOn(window, 'confirm').and.returnValue(true);
    spyOn(window, 'alert');

    notificationService.resend.and.returnValue(of({}));

    component.resendNotification(1);

    expect(notificationService.resend).toHaveBeenCalledWith(1);
    expect(window.alert).toHaveBeenCalledWith('Đã gửi lại thông báo');
  });
});
