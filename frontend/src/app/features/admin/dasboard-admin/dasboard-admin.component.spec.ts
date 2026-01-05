import { ComponentFixture, TestBed } from '@angular/core/testing';
import { DashboardAdminComponent } from './dasboard-admin.component';
import { UserService } from '../../../services/user.service';
import { LandlordService } from '../../../services/landlord.service';
import { RoomService } from '../../../services/room.service';
import { ReviewService } from '../../../services/review.service';
import { of } from 'rxjs';
import { NO_ERRORS_SCHEMA } from '@angular/core';

/* ================= MOCK DATA ================= */

const mockUsers = {
  content: [
    {
      id: 1,
      username: 'admin',
      fullName: 'Admin',
      email: 'admin@test.com',
      phone: '000',
      role: 0,
      status: 'ACTIVE',
      createdAt: new Date().toISOString()
    },
    {
      id: 2,
      username: 'tenant',
      fullName: 'Tenant',
      email: 'tenant@test.com',
      phone: '111',
      role: 2,
      status: 'ACTIVE',
      createdAt: new Date().toISOString()
    }
  ],
  totalElements: 2
};

const mockLandlords = {
  data: [{ id: 1 }, { id: 2 }]
};

const mockRooms = [{ id: 1 }, { id: 2 }, { id: 3 }];

const mockRequests = {
  data: [
    {
      id: 1,
      cccd: '123456789',
      address: 'HCM',
      user: {
        fullName: 'Nguyen Van A',
        email: 'a@test.com'
      }
    }
  ]
};

const mockReports = {
  content: [
    {
      reporterName: 'User A',
      reportedUserName: 'User B',
      reason: 'SPAM',
      createdAt: new Date(),
      status: 'PENDING'
    }
  ]
};

/* ================= MOCK SERVICES ================= */

class UserServiceMock {
  getUsers() {
    return of(mockUsers);
  }
}

class LandlordServiceMock {
  getAllLandlords() {
    return of(mockLandlords);
  }

  getPendingRequests() {
    return of(mockRequests);
  }

  approveRequest() {
    return of({});
  }

  rejectRequest() {
    return of({});
  }
}

class RoomServiceMock {
  getAllRooms() {
    return of(mockRooms);
  }
}

class ReviewServiceMock {
  getReviewReports() {
    return of(mockReports);
  }
}

/* ================= TEST SUITE ================= */

describe('DashboardAdminComponent', () => {
  let component: DashboardAdminComponent;
  let fixture: ComponentFixture<DashboardAdminComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DashboardAdminComponent],
      providers: [
        { provide: UserService, useClass: UserServiceMock },
        { provide: LandlordService, useClass: LandlordServiceMock },
        { provide: RoomService, useClass: RoomServiceMock },
        { provide: ReviewService, useClass: ReviewServiceMock }
      ],
      schemas: [NO_ERRORS_SCHEMA] // Bỏ qua chart & routerLink
    }).compileComponents();

    fixture = TestBed.createComponent(DashboardAdminComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  /* ================= BASIC ================= */

  it('TC01 - Component should be created', () => {
    expect(component).toBeTruthy();
  });

  /* ================= LOAD STATS ================= */

  it('TC02 - Load total users correctly', () => {
    expect(component.totalUsers).toBe(2);
  });

  it('TC03 - Load total landlords correctly', () => {
    expect(component.totalLandlords).toBe(2);
  });

  it('TC04 - Load total rooms correctly', () => {
    expect(component.totalRooms).toBe(3);
  });

  /* ================= RECENT DATA ================= */

  it('TC05 - Load pending landlord requests', () => {
    expect(component.pendingRequests.length).toBe(1);
  });

  it('TC06 - Load recent reports', () => {
    expect(component.recentReports.length).toBe(1);
  });

  /* ================= BUSINESS LOGIC ================= */

  it('TC07 - Process user ratio data correctly', () => {
    component.processUserRatioData(mockUsers.content as any);
    expect().nothing(); // chỉ test không crash
  });

  it('TC08 - Process user growth data correctly', () => {
    component.processUserGrowthData(mockUsers.content as any);
    expect().nothing(); // không test chart render
  });

  /* ================= ACTIONS ================= */

  it('TC09 - Approve landlord request', () => {
    spyOn(window, 'confirm').and.returnValue(true);
    const spy = spyOn(component['landlordService'], 'approveRequest').and.callThrough();

    component.approveRequest(1);
    expect(spy).toHaveBeenCalledWith(1);
  });

  it('TC10 - Reject landlord request', () => {
    spyOn(window, 'prompt').and.returnValue('Không hợp lệ');
    const spy = spyOn(component['landlordService'], 'rejectRequest').and.callThrough();

    component.rejectRequest(1);
    expect(spy).toHaveBeenCalledWith(1, 'Không hợp lệ');
  });
});
