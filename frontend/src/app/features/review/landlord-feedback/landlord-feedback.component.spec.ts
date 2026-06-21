import { environment } from '../../../../environments/environment';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { LandlordFeedbackComponent } from './landlord-feedback.component';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

describe('LandlordFeedbackComponent (FULL)', () => {
  let component: LandlordFeedbackComponent;
  let fixture: ComponentFixture<LandlordFeedbackComponent>;
  let httpMock: HttpTestingController;

  const apiUrl = `${environment.apiUrl}/feedbacks`;

  const mockFeedbacks: any[] = [
    {
      id: 1,
      title: 'Máy lạnh hỏng',
      content: 'Máy lạnh không chạy',
      status: 'PENDING',
      createdAt: '2024-01-01',
      tenant: {
        user: {
          fullName: 'Nguyễn Văn A',
          email: 'a@gmail.com'
        }
      },
      room: {
        id: 1,
        name: '101',
        building: { name: 'A' }
      }
    },
    {
      id: 2,
      title: 'Nước yếu',
      content: 'Nước chảy yếu',
      status: 'RESOLVED',
      createdAt: '2024-01-02',
      tenant: {
        user: {
          fullName: 'Trần Thị B',
          email: 'b@gmail.com'
        }
      },
      room: {
        id: 2,
        name: '102',
        building: { name: 'B' }
      }
    }
  ];

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        CommonModule,
        FormsModule,
        HttpClientTestingModule,
        LandlordFeedbackComponent
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(LandlordFeedbackComponent);
    component = fixture.componentInstance;
    httpMock = TestBed.inject(HttpTestingController);

    fixture.detectChanges();

    // ✅ FLUSH REQUEST ngOnInit()
    const req = httpMock.expectOne(
      r => r.method === 'GET' && r.url.includes('/api/feedbacks/landlord')
    );
    req.flush({ content: mockFeedbacks });
  });

  afterEach(() => {
    httpMock.verify();
  });

  // ---------------- BASIC ----------------
  it('should create component', () => {
    expect(component).toBeTruthy();
    expect(component.feedbacks.length).toBe(2);
  });

  // ---------------- FILTER ----------------
  it('should filter all feedbacks', () => {
    component.setFilter('all');
    expect(component.filteredFeedbacks.length).toBe(2);
  });

  it('should filter PENDING feedbacks', () => {
    component.setFilter('PENDING');
    expect(component.filteredFeedbacks.length).toBe(1);
    expect(component.filteredFeedbacks[0].status).toBe('PENDING');
  });

  it('should filter RESOLVED feedbacks', () => {
    component.setFilter('RESOLVED');
    expect(component.filteredFeedbacks.length).toBe(1);
    expect(component.filteredFeedbacks[0].status).toBe('RESOLVED');
  });

  // ---------------- COUNTERS ----------------
  it('should count pending feedbacks', () => {
    expect(component.pendingCount).toBe(1);
  });

  it('should count resolved feedbacks', () => {
    expect(component.resolvedCount).toBe(1);
  });

  // ---------------- MODAL ----------------
  it('should open detail modal', () => {
    component.openDetail(mockFeedbacks[0]);
    expect(component.showDetailModal).toBeTrue();
    expect(component.selectedFeedback?.id).toBe(1);
  });

  it('should close detail modal', () => {
    component.openDetail(mockFeedbacks[0]);
    component.closeDetail();
    expect(component.showDetailModal).toBeFalse();
    expect(component.selectedFeedback).toBeNull();
  });

  // ---------------- PROCESS ----------------
  it('should start processing feedback', () => {
    spyOn(window, 'alert');

    component.startProcessing(mockFeedbacks[0]);

    // ✅ PUT process
    const processReq = httpMock.expectOne(`${apiUrl}/1/process`);
    expect(processReq.request.method).toBe('PUT');
    processReq.flush({});

    // ✅ GET reload list (BẮT BUỘC)
    const reloadReq = httpMock.expectOne(
      r => r.method === 'GET' && r.url.includes('/api/feedbacks/landlord')
    );
    reloadReq.flush({ content: mockFeedbacks });

    expect(window.alert).toHaveBeenCalled();
  });

  it('should resolve feedback when note exists', () => {
    spyOn(window, 'alert');

    component.processForm.landlordNote = 'Đã xử lý xong';
    component.resolveFeedback(1);

    // ✅ PUT
    const processReq = httpMock.expectOne(`${apiUrl}/1/process`);
    expect(processReq.request.method).toBe('PUT');
    processReq.flush({});

    // ✅ GET reload
    const reloadReq = httpMock.expectOne(
      r => r.method === 'GET' && r.url.includes('/api/feedbacks/landlord')
    );
    reloadReq.flush({ content: mockFeedbacks });

    expect(window.alert).toHaveBeenCalled();
  });

  it('should NOT resolve feedback when note empty', () => {
    spyOn(window, 'alert');

    component.processForm.landlordNote = '';
    component.resolveFeedback(1);

    httpMock.expectNone(`${apiUrl}/1/process`);
    expect(window.alert).toHaveBeenCalled();
  });

  // ---------------- DELETE ----------------
  it('should delete feedback when confirmed', () => {
    spyOn(window, 'confirm').and.returnValue(true);
    spyOn(window, 'alert');

    component.deleteFeedback(1);

    const req = httpMock.expectOne(
      `${apiUrl}/1`
    );
    expect(req.request.method).toBe('DELETE');
    req.flush({});

    // 🔥 reload list
    const reloadReq = httpMock.expectOne(
      r => r.method === 'GET' && r.url.includes('/api/feedbacks/landlord')
    );
    reloadReq.flush({ content: [] });

    expect(window.alert).toHaveBeenCalled();
  });

  it('should NOT delete feedback when cancel confirm', () => {
    spyOn(window, 'confirm').and.returnValue(false);

    component.deleteFeedback(1);

    httpMock.expectNone(`${apiUrl}/1`);
  });

  // ---------------- UTILS ----------------
  it('should return correct status text', () => {
    expect(component.getStatusText('PENDING')).toBe('Chưa xử lý');
    expect(component.getStatusText('RESOLVED')).toBe('Đã xử lý');
  });

  it('should return correct status color', () => {
    expect(component.getStatusColor('PENDING')).toBe('#ffc107');
    expect(component.getStatusColor('RESOLVED')).toBe('#28a745');
  });

  it('should return correct status icon', () => {
    expect(component.getStatusIcon('PENDING')).toContain('clock');
    expect(component.getStatusIcon('RESOLVED')).toContain('check-circle');
  });
});
