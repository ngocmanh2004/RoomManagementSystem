import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TenantNotificationComponent } from './tenant-notification.component';

describe('TenantNotificationComponent', () => {
  let component: TenantNotificationComponent;
  let fixture: ComponentFixture<TenantNotificationComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TenantNotificationComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(TenantNotificationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
