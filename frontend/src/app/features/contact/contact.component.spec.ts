import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ContactComponent } from './contact.component';

describe('ContactComponent', () => {
  let component: ContactComponent;
  let fixture: ComponentFixture<ContactComponent>;
  let compiled: HTMLElement;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ContactComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(ContactComponent);
    component = fixture.componentInstance;
    compiled = fixture.nativeElement;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render the main title', () => {
    const titleElement = compiled.querySelector('.hero-section h1');
    expect(titleElement?.textContent).toContain('Liên hệ với chúng tôi');
  });

  it('should render the email address', () => {
    const emailElement = compiled.querySelector('.info-box a[href*="mailto:contact@techroom.vn"]');
    const emailText = compiled.querySelector('.info-box p:last-child');
    
    expect(emailElement).toBeTruthy();
    expect(emailText?.textContent).toContain('contact@techroom.vn');
  });

  it('should render the office address', () => {
    const addressElement = compiled.querySelector('.info-box p');
    expect(addressElement?.textContent).toContain('19 Nguyễn Hữu Thọ');
  });
});