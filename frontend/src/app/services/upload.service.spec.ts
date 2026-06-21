import { environment } from '../../environments/environment';
import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { UploadService } from './upload.service';

describe('UploadService', () => {
  let service: UploadService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [UploadService]
    });

    service = TestBed.inject(UploadService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    // ✅ đảm bảo không còn request nào chưa được xử lý
    httpMock.verify();
  });

  // ============================
  // CREATE
  // ============================
  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  // ============================
  // UPLOAD IMAGE
  // ============================
  it('should upload image and return url', () => {
    // 🔹 fake file
    const file = new File(['dummy content'], 'test.png', {
      type: 'image/png'
    });

    const mockResponse = {
      url: 'http://localhost:8081/uploads/test.png'
    };

    service.uploadImage(file).subscribe(response => {
      expect(response).toEqual(mockResponse);
    });

    // 🔹 bắt request
    const req = httpMock.expectOne(`${environment.apiUrl}/upload`);
    expect(req.request.method).toBe('POST');

    // 🔹 kiểm tra FormData
    expect(req.request.body instanceof FormData).toBeTrue();

    const formData = req.request.body as FormData;
    expect(formData.has('file')).toBeTrue();

    // 🔹 trả response giả
    req.flush(mockResponse);
  });
});
