import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AdminLandlordApprovalComponent } from './admin-landlord-approval.component';

describe('AdminLandlordApprovalComponent', () => {
  let component: AdminLandlordApprovalComponent;
  let fixture: ComponentFixture<AdminLandlordApprovalComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AdminLandlordApprovalComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AdminLandlordApprovalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
