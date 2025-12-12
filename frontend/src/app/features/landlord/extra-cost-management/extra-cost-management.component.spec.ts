import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ExtraCostManagementComponent } from './extra-cost-management.component';

describe('ExtraCostManagementComponent', () => {
  let component: ExtraCostManagementComponent;
  let fixture: ComponentFixture<ExtraCostManagementComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ExtraCostManagementComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ExtraCostManagementComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
