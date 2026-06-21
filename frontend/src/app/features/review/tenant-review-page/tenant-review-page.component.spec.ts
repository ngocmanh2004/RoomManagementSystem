import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TenantReviewPageComponent } from './tenant-review-page.component';
import { HttpClientTestingModule } from '@angular/common/http/testing';

describe('TenantReviewPageComponent', () => {
  let component: TenantReviewPageComponent;
  let fixture: ComponentFixture<TenantReviewPageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TenantReviewPageComponent, HttpClientTestingModule]
    })
    .compileComponents();

    fixture = TestBed.createComponent(TenantReviewPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
