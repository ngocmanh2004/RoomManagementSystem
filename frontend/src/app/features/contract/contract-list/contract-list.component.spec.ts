import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ContractListComponent } from './contract-list.component';
import { ContractService } from '../../../services/contract.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { By } from '@angular/platform-browser';
import { of } from 'rxjs';

/* ================= MOCK DATA ================= */

const mockContracts = [
  {
    id: 1,
    fullName: 'Nguyễn Văn A',
    roomName: 'Phòng 101',
    startDate: new Date('2024-01-01'),
    endDate: new Date('2024-12-31'),
    status: 'ACTIVE',
    createdAt: new Date('2023-12-01T10:00:00')
  },
  {
    id: 2,
    fullName: 'Trần Thị B',
    roomName: 'Phòng 102',
    startDate: new Date('2023-01-01'),
    endDate: new Date('2023-12-31'),
    status: 'EXPIRED',
    createdAt: new Date('2022-12-01T09:00:00')
  }
] as any;

/* ================= MOCK SERVICE ================= */

class MockContractService {
  getContracts(status: string) {
    return of(mockContracts);
  }
}

/* ================= TEST SUITE ================= */

describe('ContractListComponent', () => {
  let component: ContractListComponent;
  let fixture: ComponentFixture<ContractListComponent>;
  let contractService: ContractService;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        CommonModule,
        FormsModule,
        ContractListComponent
      ],
      providers: [
        { provide: ContractService, useClass: MockContractService }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(ContractListComponent);
    component = fixture.componentInstance;
    contractService = TestBed.inject(ContractService);
    fixture.detectChanges();
  });

  /* ========== BASIC TESTS ========== */

  it('should create component', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize filterStatus as empty string', () => {
    expect(component.filterStatus).toBe('');
  });

  /* ========== LIFECYCLE ========== */

  it('should call loadContracts on ngOnInit', () => {
    spyOn(component, 'loadContracts');
    component.ngOnInit();
    expect(component.loadContracts).toHaveBeenCalled();
  });

  /* ========== SERVICE INTERACTION ========== */

  it('should call ContractService.getContracts with filterStatus', () => {
    spyOn(contractService, 'getContracts').and.callThrough();
    component.filterStatus = 'ACTIVE';
    component.loadContracts();
    expect(contractService.getContracts).toHaveBeenCalledWith('ACTIVE');
  });

  it('should load contracts from service', () => {
    component.loadContracts();
    expect(component.contracts.length).toBe(2);
    expect(component.contracts[0].fullName).toBe('Nguyễn Văn A');
  });

  /* ========== FILTER ========== */

  it('should reload contracts when filter changes', () => {
    spyOn(component, 'loadContracts');
    component.onFilterChange();
    expect(component.loadContracts).toHaveBeenCalled();
  });

  /* ========== convertStatus() ========== */

  it('should convert PENDING correctly', () => {
    expect(component.convertStatus('PENDING')).toBe('Đang chờ duyệt');
  });

  it('should convert ACTIVE correctly', () => {
    expect(component.convertStatus('ACTIVE')).toBe('Đang thuê');
  });

  it('should convert EXPIRED correctly', () => {
    expect(component.convertStatus('EXPIRED')).toBe('Hết hạn');
  });

  it('should convert CANCELLED correctly', () => {
    expect(component.convertStatus('CANCELLED')).toBe('Hủy');
  });

  it('should return original status if unknown', () => {
    expect(component.convertStatus('UNKNOWN')).toBe('UNKNOWN');
  });

  /* ========== TEMPLATE / DOM ========== */

  it('should render correct number of rows', () => {
    component.contracts = mockContracts;
    fixture.detectChanges();

    const rows = fixture.debugElement.queryAll(By.css('tbody tr'));
    expect(rows.length).toBe(2);
  });

  it('should display fullName and roomName in table', () => {
    component.contracts = mockContracts;
    fixture.detectChanges();

    const firstRow = fixture.debugElement.query(By.css('tbody tr'));
    const cells = firstRow.queryAll(By.css('td'));

    expect(cells[1].nativeElement.textContent).toContain('Nguyễn Văn A');
    expect(cells[2].nativeElement.textContent).toContain('Phòng 101');
  });

  /* ========== SELECT / ngModel ========== */

  it('should update filterStatus when select changes', () => {
    const select = fixture.debugElement.query(By.css('select')).nativeElement;
    select.value = 'ACTIVE';
    select.dispatchEvent(new Event('change'));
    fixture.detectChanges();

    expect(component.filterStatus).toBe('ACTIVE');
  });

  it('should call onFilterChange when select changes', () => {
    spyOn(component, 'onFilterChange');

    const select = fixture.debugElement.query(By.css('select'));
    select.triggerEventHandler('change', { target: { value: 'ACTIVE' } });

    expect(component.onFilterChange).toHaveBeenCalled();
  });
});
