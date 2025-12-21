import { ComponentFixture, TestBed } from '@angular/core/testing';

import { WaterManagementComponent } from './water-management.component';

describe('WaterManagementComponent', () => {
  let component: WaterManagementComponent;
  let fixture: ComponentFixture<WaterManagementComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [WaterManagementComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(WaterManagementComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
