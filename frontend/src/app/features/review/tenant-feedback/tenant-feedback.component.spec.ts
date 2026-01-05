import { ComponentFixture, TestBed } from '@angular/core/testing';
import { TenantFeedbackComponent } from './tenant-feedback.component';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

describe('TenantFeedbackComponent (FIXED)', () => {
  let component: TenantFeedbackComponent;
  let fixture: ComponentFixture<TenantFeedbackComponent>;
  let httpMock: HttpTestingController;

  const apiUrl = 'http://localhost:8081/api/feedbacks';

  const mockFeedbacks = [
    {
      id: 1,
      title: 'Máy lạnh hỏng',
      content: 'Không hoạt động',
      status: 'PENDING',
      createdAt: '2024-01-01',
      room: { id: 1, name: '101' }
    },
    {
      id: 2,
      title: 'Nước yếu',
      content: 'Buổi tối nước yếu',
      status: 'RESOLVED',
      createdAt: '2024-01-02',
      landlordNote: 'Đã sửa',
      room: { id: 1, name: '101' }
    }
  ];

  /** 🔑 HELPER: flush request loadFeedbacks (ngOnInit) */
  function flushInitRequest(data: any = { content: [] }) {
    const req = httpMock.expectOne(
      `${apiUrl}/my?page=0&size=10`
    );
    expect(req.request.method).toBe('GET');
    req.flush(data);
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        TenantFeedbackComponent,
        CommonModule,
        FormsModule,
        HttpClientTestingModule
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(TenantFeedbackComponent);
    component = fixture.componentInstance;
    httpMock = TestBed.inject(HttpTestingController);

    fixture.detectChanges(); // 🚨 trigger ngOnInit → gọi API
  });

  afterEach(() => {
    httpMock.verify(); // 🚨 đảm bảo không còn request treo
  });

  // ================= BASIC =================

  it('should create component', () => {
    flushInitRequest();
    expect(component).toBeTruthy();
  });

  // ================= LOAD FEEDBACK =================

  it('should load feedbacks on init', () => {
    flushInitRequest({ content: mockFeedbacks });

    expect(component.feedbacks.length).toBe(2);
    expect(component.loading).toBeFalse();
  });

  it('should handle load feedback error', () => {
    const req = httpMock.expectOne(
      `${apiUrl}/my?page=0&size=10`
    );
    req.error(new ProgressEvent('Network error'));

    expect(component.loading).toBeFalse();
  });

  // ================= TOGGLE FORM =================

  it('should toggle create form', () => {
    flushInitRequest();

    component.toggleCreateForm();
    expect(component.showCreateForm).toBeTrue();

    component.toggleCreateForm();
    expect(component.showCreateForm).toBeFalse();
  });

  // ================= CREATE FEEDBACK =================

  it('should not create feedback if title or content is empty', () => {
    flushInitRequest();
    spyOn(window, 'alert');

    component.createFeedback();

    expect(window.alert).toHaveBeenCalled();
  });

  it('should create feedback successfully', () => {
    flushInitRequest();
    spyOn(window, 'alert');

    component.newFeedback = {
      title: 'Test',
      content: 'Test content',
      attachmentUrl: ''
    };

    component.createFeedback();

    // POST
    const postReq = httpMock.expectOne(apiUrl);
    expect(postReq.request.method).toBe('POST');
    postReq.flush({});

    // 🔥 loadFeedbacks() được gọi trong afterSave()
    const getReqs = httpMock.match(
      `${apiUrl}/my?page=0&size=10`
    );
    expect(getReqs.length).toBe(1);
    getReqs[0].flush({ content: [] });

    expect(window.alert).toHaveBeenCalledWith('Gửi phản hồi thành công!');
  });

  // ================= UPDATE FEEDBACK =================

  it('should update feedback when editingFeedbackId exists', () => {
    flushInitRequest();
    spyOn(window, 'alert');

    component.editingFeedbackId = 1;
    component.newFeedback.title = 'Updated';
    component.newFeedback.content = 'Updated content';

    component.createFeedback();

    // PUT
    const putReq = httpMock.expectOne(`${apiUrl}/1`);
    expect(putReq.request.method).toBe('PUT');
    putReq.flush({});

    // 🔥 loadFeedbacks()
    const getReqs = httpMock.match(
      `${apiUrl}/my?page=0&size=10`
    );
    expect(getReqs.length).toBe(1);
    getReqs[0].flush({ content: [] });

    expect(window.alert).toHaveBeenCalledWith('Cập nhật phản hồi thành công!');
  });

  // ================= DELETE FEEDBACK =================

  it('should delete feedback when confirmed', () => {
    flushInitRequest();
    spyOn(window, 'confirm').and.returnValue(true);
    spyOn(window, 'alert');

    component.deleteFeedback(1);

    // DELETE
    const deleteReq = httpMock.expectOne(`${apiUrl}/1`);
    expect(deleteReq.request.method).toBe('DELETE');
    deleteReq.flush({});

    // 🔥 GET LẠI danh sách (loadFeedbacks)
    const getReqs = httpMock.match(
      `${apiUrl}/my?page=0&size=10`
    );
    expect(getReqs.length).toBe(1);
    getReqs[0].flush({ content: [] });

    expect(window.alert).toHaveBeenCalledWith('Đã xóa phản hồi!');
  });

  it('should not delete feedback when canceled', () => {
    flushInitRequest();
    spyOn(window, 'confirm').and.returnValue(false);

    component.deleteFeedback(1);

    httpMock.expectNone(`${apiUrl}/1`);
  });

  // ================= EDIT FEEDBACK =================

  it('should open edit form with selected feedback', () => {
    flushInitRequest();

    component.editFeedback(mockFeedbacks[0] as any);

    expect(component.editingFeedbackId).toBe(1);
    expect(component.showCreateForm).toBeTrue();
    expect(component.newFeedback.title).toBe('Máy lạnh hỏng');
  });

  // ================= FILTER =================

  it('should filter feedbacks by status', () => {
    flushInitRequest();
    component.feedbacks = mockFeedbacks as any;

    component.filterStatus = 'PENDING';
    const result = component.filteredFeedbacks;

    expect(result.length).toBe(1);
    expect(result[0].status).toBe('PENDING');
  });

  // ================= STATUS HELPERS =================

  it('should return correct status text', () => {
    flushInitRequest();

    expect(component.getStatusText('PENDING')).toBe('Chưa xử lý');
    expect(component.getStatusText('RESOLVED')).toBe('Đã xử lý');
  });

  it('should return correct status icon', () => {
    flushInitRequest();

    expect(component.getStatusIcon('PENDING')).toBe('clock');
    expect(component.getStatusIcon('CANCELED')).toBe('ban');
  });

  it('should return correct status icon card', () => {
    flushInitRequest();

    expect(component.getStatusIconCard('PROCESSING')).toBe('hourglass-half');
  });

  // ================= FILE UPLOAD =================

  it('should handle file selection and call uploadImage', () => {
    flushInitRequest();

    const file = new File(['test'], 'test.png', { type: 'image/png' });
    const event = {
      target: { files: [file] }
    } as any;

    spyOn(component, 'uploadImage');

    component.onFileSelected(event);

    expect(component.selectedFile).toBe(file);
    expect(component.uploadImage).toHaveBeenCalled();
  });
});
