import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CreateDirectContractComponent } from './create-direct-contract.component';
import { HttpClientTestingModule } from '@angular/common/http/testing';

describe('CreateDirectContractComponent', () => {
  let component: CreateDirectContractComponent;
  let fixture: ComponentFixture<CreateDirectContractComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CreateDirectContractComponent, HttpClientTestingModule]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CreateDirectContractComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
