import { ComponentFixture, TestBed } from '@angular/core/testing';
import { TenantManagementComponent } from './tenant-management.component';
import { TenantService } from '../../../services/tenant.service';
import { of, throwError } from 'rxjs';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

/* ================= MOCK DATA ================= */

const mockTenants = [
  {
    id: 1,
    cccd: '012345678901',
    dateOfBirth: '2000-01-01',
    address: 'Hà Nội',
    user: {
      fullName: 'Nguyễn Văn A',
      phone: '0912345678',
      email: 'a@gmail.com',
      status: 'ACTIVE',
      createdAt: new Date()
    }
  },
  {
    id: 2,
    cccd: '012345678902',
    dateOfBirth: '1998-01-01',
    address: 'HCM',
    user: {
      fullName: 'Trần Thị B',
      phone: '0987654321',
      email: 'b@gmail.com',
      status: 'PENDING',
      createdAt: new Date('2020-01-01')
    }
  }
];

/* ================= MOCK SERVICE ================= */

class MockTenantService {
  getAll() {
    return of(mockTenants);
  }

  delete() {
    return of({ message: 'Xóa thành công' });
  }

  update() {
    return of({});
  }

  add() {
    return of({ username: 'user1', password: '123456' });
  }
}

/* ================= TEST SUITE ================= */

describe('TenantManagementComponent', () => {
  let component: TenantManagementComponent;
  let fixture: ComponentFixture<TenantManagementComponent>;
  let service: TenantService;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        CommonModule,
        FormsModule,
        TenantManagementComponent
      ],
      providers: [
        { provide: TenantService, useClass: MockTenantService }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(TenantManagementComponent);
    component = fixture.componentInstance;
    service = TestBed.inject(TenantService);
    fixture.detectChanges();
  });

  /* ========== BASIC ========== */

  it('should create component', () => {
    expect(component).toBeTruthy();
  });

  /* ========== LOAD TENANTS ========== */

  it('should load tenants on init', () => {
    expect(component.tenants.length).toBe(2);
    expect(component.filteredTenants.length).toBe(2);
  });

  /* ========== FILTER & SEARCH ========== */

  it('should filter by ACTIVE status', () => {
    component.selectedStatus = 'ACTIVE';
    component.filterTenants();
    expect(component.filteredTenants.length).toBe(1);
  });

  it('should search by full name', () => {
    component.searchText = 'nguyễn';
    component.filterTenants();
    expect(component.filteredTenants.length).toBe(1);
  });

  it('should search by phone', () => {
    component.searchText = '0987';
    component.filterTenants();
    expect(component.filteredTenants.length).toBe(1);
  });

  it('should search by cccd', () => {
    component.searchText = '012345678901';
    component.filterTenants();
    expect(component.filteredTenants.length).toBe(1);
  });

  /* ========== COUNTS ========== */

  it('should update counts correctly', () => {
    component.filteredTenants = mockTenants;
    component.updateCounts();

    expect(component.countDangThue).toBe(1);
    expect(component.countDaNghi).toBe(1);
  });

  /* ========== FORM MODES ========== */

  it('should open add form', () => {
    component.openAddForm();
    expect(component.mode).toBe('add');
    expect(component.isFormVisible).toBeTrue();
  });

  it('should open view form', () => {
    component.viewTenant(mockTenants[0]);
    expect(component.mode).toBe('view');
    expect(component.isFormVisible).toBeTrue();
  });

  it('should open edit form', () => {
    component.editTenant(mockTenants[0]);
    expect(component.mode).toBe('edit');
    expect(component.isFormVisible).toBeTrue();
  });

  it('should close form', () => {
    component.closeForm();
    expect(component.isFormVisible).toBeFalse();
  });

  /* ========== VALIDATION ========== */

  it('should fail validation when fullName empty', () => {
    component.currentTenant.user.fullName = '';
    expect(component.validateTenant()).toContain('Họ tên');
  });

  /* ========== SAVE TENANT ========== */

  it('should not save when validation fails', () => {
    spyOn(window, 'alert');
    component.currentTenant.user.fullName = '';
    component.saveTenant();
    expect(window.alert).toHaveBeenCalled();
  });

  it('should add tenant successfully', () => {
    spyOn(window, 'alert');
    component.mode = 'add';

    component.currentTenant = {
      id: 0,
      cccd: '012345678903',
      dateOfBirth: '2001-01-01',
      address: 'HN',
      user: {
        fullName: 'Test User',
        phone: '0911111111',
        email: 'test@gmail.com',
        status: 'ACTIVE',
        createdAt: new Date()
      }
    };

    component.saveTenant();
    expect(window.alert).toHaveBeenCalled();
  });

  it('should update tenant successfully', () => {
    spyOn(window, 'alert');
    component.mode = 'edit';
    component.currentTenant = mockTenants[0];

    component.saveTenant();
    expect(window.alert).toHaveBeenCalled();
  });

  /* ========== DELETE ========== */

  it('should delete tenant when confirmed', () => {
    spyOn(window, 'confirm').and.returnValue(true);
    spyOn(window, 'alert');

    component.deleteTenant(mockTenants[0]);
    expect(window.alert).toHaveBeenCalled();
  });

  it('should not delete tenant when canceled', () => {
    spyOn(window, 'confirm').and.returnValue(false);
    spyOn(service, 'delete');

    component.deleteTenant(mockTenants[0]);
    expect(service.delete).not.toHaveBeenCalled();
  });

  /* ========== UPDATE STATUS ========== */

  it('should update status successfully', () => {
    spyOn(service, 'update').and.callThrough();
    component.updateStatus(mockTenants[0]);
    expect(service.update).toHaveBeenCalled();
  });

  it('should handle error when updating status', () => {
    spyOn(service, 'update').and.returnValue(throwError(() => new Error()));
    spyOn(window, 'alert');

    component.updateStatus(mockTenants[0]);
    expect(window.alert).toHaveBeenCalled();
  });
});
