import { ComponentFixture, TestBed } from '@angular/core/testing';
import { DashboardLandlordComponent } from './dashboard-landlord.component';
import { AuthService } from '../../../services/auth.service';
import { RoomService } from '../../../services/room.service';
import { InvoiceService } from '../../../services/invoice.service';
import { TenantService } from '../../../services/tenant.service';
import { of } from 'rxjs';
import { NO_ERRORS_SCHEMA } from '@angular/core';

/* ================= MOCK DATA ================= */

const mockRooms = [
  { id: 1, status: 'AVAILABLE' },
  { id: 2, status: 'OCCUPIED' },
  { id: 3, status: 'OCCUPIED' }
];

const mockInvoices = {
  data: [
    {
      contractCode: 'HD01',
      tenantName: 'Nguyen Van A',
      roomName: 'P101',
      totalAmount: 2000000,
      status: 'PAID',
      month: new Date().toISOString().slice(0, 7),
      createdAt: new Date().toISOString()
    },
    {
      contractCode: 'HD02',
      tenantName: 'Tran Van B',
      roomName: 'P102',
      totalAmount: 1500000,
      status: 'UNPAID',
      month: new Date().toISOString().slice(0, 7),
      createdAt: new Date().toISOString()
    }
  ]
};

/* ================= MOCK SERVICES ================= */

class AuthServiceMock {
  getCurrentLandlordId() {
    return 1;
  }
}

class RoomServiceMock {
  getRoomsByLandlord() {
    return of(mockRooms);
  }
}

class InvoiceServiceMock {
  getByLandlordId() {
    return of(mockInvoices);
  }
}

class TenantServiceMock {}

/* ================= TEST SUITE ================= */

describe('DashboardLandlordComponent', () => {
  let component: DashboardLandlordComponent;
  let fixture: ComponentFixture<DashboardLandlordComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DashboardLandlordComponent],
      providers: [
        { provide: AuthService, useClass: AuthServiceMock },
        { provide: RoomService, useClass: RoomServiceMock },
        { provide: InvoiceService, useClass: InvoiceServiceMock },
        { provide: TenantService, useClass: TenantServiceMock }
      ],
      schemas: [NO_ERRORS_SCHEMA] // Bỏ qua canvas, chart.js
    }).compileComponents();

    fixture = TestBed.createComponent(DashboardLandlordComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  /* ================= BASIC ================= */

  it('TC01 - Component should be created', () => {
    expect(component).toBeTruthy();
  });

  /* ================= AUTH ================= */

  it('TC02 - Should get landlordId on init', () => {
    expect(component.landlordId).toBe(1);
  });

  /* ================= ROOM STATS ================= */

  it('TC03 - Should load room statistics correctly', () => {
    expect(component.totalRooms).toBe(3);
    expect(component.emptyRooms).toBe(1);
    expect(component.occupiedRooms).toBe(2);
    expect(component.totalTenants).toBe(2);
  });

  /* ================= FINANCIAL STATS ================= */

  it('TC04 - Should calculate monthly revenue correctly', () => {
    expect(component.monthlyRevenue).toBe(2000000);
  });

  it('TC05 - Should load recent invoices', () => {
    expect(component.recentInvoices.length).toBe(2);
  });

  /* ================= BUSINESS LOGIC ================= */

  it('TC06 - Should prepare revenue chart data without error', () => {
    component.prepareRevenueChartData(mockInvoices.data as any);
    expect(component.revenueData.length).toBe(6);
  });

  it('TC07 - Should update room chart safely', () => {
    component.updateRoomChart();
    expect().nothing(); // không test chart render
  });

  /* ================= LIFE CYCLE ================= */

  it('TC08 - ngAfterViewInit should not throw error', () => {
    component.ngAfterViewInit();
    expect().nothing();
  });
});
