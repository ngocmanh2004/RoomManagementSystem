import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DasboardAdminComponent } from './dasboard-admin.component';

describe('DasboardAdminComponent', () => {
  let component: DasboardAdminComponent;
  let fixture: ComponentFixture<DasboardAdminComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DasboardAdminComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DasboardAdminComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
