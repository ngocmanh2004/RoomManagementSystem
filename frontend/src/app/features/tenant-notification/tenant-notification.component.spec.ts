import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TenantNotificationComponent } from './tenant-notification.component';
import { HttpClientTestingModule } from '@angular/common/http/testing';

describe('TenantNotificationComponent', () => {
  let component: TenantNotificationComponent;
  let fixture: ComponentFixture<TenantNotificationComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TenantNotificationComponent, HttpClientTestingModule]
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
