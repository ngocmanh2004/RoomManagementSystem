import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { UserService, User, PageResponse } from './user.service';

fdescribe('UserService', () => {
  let service: UserService;
  let httpMock: HttpTestingController;

  const API_URL = 'http://localhost:8081/api/admin/users';

  const mockUser: User = {
    id: 1,
    username: 'user1',
    fullName: 'Nguyễn Văn A',
    email: 'a@gmail.com',
    phone: '0909123456',
    role: 2,
    status: 'ACTIVE',
    createdAt: '2024-01-01'
  };

  const mockPageResponse: PageResponse<User> = {
    content: [mockUser],
    totalElements: 1,
    totalPages: 1,
    size: 15,
    number: 0
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [UserService]
    });

    service = TestBed.inject(UserService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify(); // đảm bảo không còn request nào chưa test
  });

  // ====================================================
  // getUsers
  // ====================================================
  it('TC01 - getUsers không truyền filter', () => {
    service.getUsers().subscribe(res => {
      expect(res).toEqual(mockPageResponse);
    });

    const req = httpMock.expectOne(
      r =>
        r.method === 'GET' &&
        r.url === API_URL &&
        r.params.get('page') === '0' &&
        r.params.get('size') === '15'
    );

    req.flush(mockPageResponse);
  });

  it('TC02 - getUsers có keyword, role, status', () => {
    service.getUsers('abc', 2, 'ACTIVE', 1, 10).subscribe();

    const req = httpMock.expectOne(
      r =>
        r.method === 'GET' &&
        r.url === API_URL &&
        r.params.get('keyword') === 'abc' &&
        r.params.get('role') === '2' &&
        r.params.get('status') === 'ACTIVE' &&
        r.params.get('page') === '1' &&
        r.params.get('size') === '10'
    );

    req.flush(mockPageResponse);
  });

  it('TC03 - getUsers không gửi status khi status = ALL', () => {
    service.getUsers(undefined, undefined, 'ALL').subscribe();

    const req = httpMock.expectOne(API_URL);
    expect(req.request.params.has('status')).toBeFalse();

    req.flush(mockPageResponse);
  });

  // ====================================================
  // createUser
  // ====================================================
  it('TC04 - createUser gọi POST đúng', () => {
    const newUser = {
      username: 'newuser',
      password: '123456',
      role: 2
    };

    service.createUser(newUser).subscribe(res => {
      expect(res).toEqual(mockUser);
    });

    const req = httpMock.expectOne(API_URL);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(newUser);

    req.flush(mockUser);
  });

  // ====================================================
  // updateUser
  // ====================================================
  it('TC05 - updateUser gọi PUT đúng', () => {
    const updateData = { fullName: 'Updated Name' };

    service.updateUser(1, updateData).subscribe(res => {
      expect(res).toEqual(mockUser);
    });

    const req = httpMock.expectOne(`${API_URL}/1`);
    expect(req.request.method).toBe('PUT');
    expect(req.request.body).toEqual(updateData);

    req.flush(mockUser);
  });

  // ====================================================
  // updateStatus
  // ====================================================
  it('TC06 - updateStatus gọi PUT đúng URL và body', () => {
    service.updateStatus(1, 'BANNED').subscribe();

    const req = httpMock.expectOne(`${API_URL}/1/status`);
    expect(req.request.method).toBe('PUT');
    expect(req.request.body).toEqual({ status: 'BANNED' });

    req.flush({});
  });

  // ====================================================
  // deleteUser
  // ====================================================
  it('TC07 - deleteUser gọi DELETE đúng', () => {
    service.deleteUser(1).subscribe();

    const req = httpMock.expectOne(`${API_URL}/1`);
    expect(req.request.method).toBe('DELETE');

    req.flush({});
  });

  // ====================================================
  // getTenants
  // ====================================================
  it('TC08 - getTenants trả về danh sách user role = 2', () => {
    const response: PageResponse<User> = {
      content: [mockUser],
      totalElements: 1,
      totalPages: 1,
      size: 10,
      number: 0
    };

    service.getTenants().subscribe(users => {
      expect(users.length).toBe(1);
      expect(users[0].role).toBe(2);
    });

    const req = httpMock.expectOne('/api/users?role=2');
    expect(req.request.method).toBe('GET');

    req.flush(response);
  });
});
