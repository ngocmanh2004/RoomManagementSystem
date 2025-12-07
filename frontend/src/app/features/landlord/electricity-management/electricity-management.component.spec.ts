import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ElectricityManagementComponent } from './electricity-management.component';

describe('ElectricityManagementComponent', () => {
  let component: ElectricityManagementComponent;
  let fixture: ComponentFixture<ElectricityManagementComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ElectricityManagementComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ElectricityManagementComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
