import { ComponentFixture, TestBed } from '@angular/core/testing';
import { UserManagementComponent } from './user-management.component';
import { UserService, PageResponse, User } from '../../../services/user.service';
import { of, throwError } from 'rxjs';

fdescribe('UserManagementComponent - Kiểm thử tổng hợp', () => {
  let component: UserManagementComponent;
  let fixture: ComponentFixture<UserManagementComponent>;
  let userServiceSpy: jasmine.SpyObj<UserService>;

  const emptyPageResponse: PageResponse<User> = {
    content: [],
    totalElements: 0,
    totalPages: 0,
    size: 15,
    number: 0
  };

  // === Status constants ===
  const STATUS_ACTIVE: User['status'] = 'ACTIVE';
  const STATUS_BANNED: User['status'] = 'BANNED';
  const STATUS_PENDING: User['status'] = 'PENDING';

  beforeEach(async () => {
    userServiceSpy = jasmine.createSpyObj<UserService>('UserService', [
      'getUsers',
      'createUser',
      'updateUser',
      'deleteUser',
      'updateStatus'
    ]);

    userServiceSpy.getUsers.and.returnValue(of(emptyPageResponse));

    spyOn(window, 'confirm').and.returnValue(true);
    spyOn(window, 'alert').and.stub();

    await TestBed.configureTestingModule({
      imports: [UserManagementComponent],
      providers: [{ provide: UserService, useValue: userServiceSpy }]
    }).compileComponents();

    fixture = TestBed.createComponent(UserManagementComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  // ====================================================
  // US 1.1 - Thêm người dùng
  // ====================================================
  describe('US 1.1 - Thêm người dùng', () => {
    it('TC01 - Mở modal thêm người dùng mới', () => {
      component.openCreateModal();
      expect(component.isModalOpen).toBeTrue();
      expect(component.isEditMode).toBeFalse();
    });

    it('TC02 - Form invalid khi chưa nhập dữ liệu', () => {
      component.openCreateModal();
      component.userForm.reset();
      component.onSubmit();
      expect(userServiceSpy.createUser).not.toHaveBeenCalled();
    });

    it('TC03 - Thêm người dùng thành công', () => {
      userServiceSpy.createUser.and.returnValue(of({} as User));
      component.openCreateModal();
      component.userForm.setValue({
        username: 'newuser',
        password: '123456',
        fullName: 'Nguyễn Văn A',
        email: 'test@gmail.com',
        phone: '0909123456',
        role: 2,
        status: STATUS_ACTIVE
      });
      component.onSubmit();
      expect(userServiceSpy.createUser).toHaveBeenCalled();
    });

    it('TC04 - Thêm người dùng thất bại do server', () => {
      userServiceSpy.createUser.and.returnValue(
        throwError(() => ({ error: { message: 'Server error' } }))
      );
      component.openCreateModal();
      component.userForm.setValue({
        username: 'newuser',
        password: '123456',
        fullName: 'Nguyễn Văn A',
        email: 'test@gmail.com',
        phone: '0909123456',
        role: 2,
        status: STATUS_ACTIVE
      });
      component.onSubmit();
      expect(userServiceSpy.createUser).toHaveBeenCalled();
    });

    it('TC05 - Reset form khi đóng modal', () => {
      component.openCreateModal();
      component.closeModal();
      expect(component.isModalOpen).toBeFalse();
    });
  });

  // ====================================================
  // US 1.2 - Chỉnh sửa người dùng
  // ====================================================
  describe('US 1.2 - Chỉnh sửa người dùng', () => {
    const mockUser: User = {
      id: 1,
      username: 'testuser',
      fullName: 'Nguyễn Văn B',
      email: 'b@gmail.com',
      phone: '0909123457',
      role: 2,
      status: STATUS_ACTIVE,
      createdAt: '2024-01-01'
    };

    it('TC06 - Mở modal chỉnh sửa', () => {
      component.openEditModal(mockUser);
      expect(component.isModalOpen).toBeTrue();
      expect(component.isEditMode).toBeTrue();
    });

    it('TC07 - Form invalid khi sửa dữ liệu chưa hợp lệ', () => {
      component.openEditModal(mockUser);
      component.userForm.get('fullName')?.setValue('');
      component.onSubmit();
      expect(userServiceSpy.updateUser).not.toHaveBeenCalled();
    });

    it('TC08 - Cập nhật thông tin người dùng thành công', () => {
      userServiceSpy.updateUser.and.returnValue(of(mockUser));
      component.openEditModal(mockUser);
      component.userForm.patchValue({ fullName: 'Updated Name', password: '123456' });
      component.onSubmit();
      expect(userServiceSpy.updateUser).toHaveBeenCalled();
    });

    it('TC09 - Cập nhật thất bại do server', () => {
      userServiceSpy.updateUser.and.returnValue(
        throwError(() => ({ error: { message: 'Server error' } }))
      );
      component.openEditModal(mockUser);
      component.userForm.patchValue({ fullName: 'Updated Name', password: '123456' });
      component.onSubmit();
      expect(userServiceSpy.updateUser).toHaveBeenCalled();
    });

    it('TC10 - Đóng modal khi chỉnh sửa xong', () => {
      component.openEditModal(mockUser);
      component.closeModal();
      expect(component.isModalOpen).toBeFalse();
    });
  });

  // ====================================================
  // US 1.3 - Xóa người dùng
  // ====================================================
  describe('US 1.3 - Xóa người dùng', () => {
    const mockUser: User = {
      id: 1,
      username: 'user1',
      fullName: 'Nguyễn C',
      email: 'c@gmail.com',
      phone: '0909123458',
      role: 2,
      status: STATUS_ACTIVE,
      createdAt: '2024-01-01'
    };

    it('TC11 - Mở modal xác nhận xóa', () => {
      component.openDeleteModal(mockUser);
      expect(component.isDeleteModalOpen).toBeTrue();
      expect(component.userToDelete).toBe(mockUser);
    });

    it('TC12 - Xóa người dùng thành công', () => {
      userServiceSpy.deleteUser.and.returnValue(of({}));
      component.userToDelete = mockUser;
      component.confirmDelete();
      expect(userServiceSpy.deleteUser).toHaveBeenCalledWith(mockUser.id);
    });

    it('TC13 - Xóa thất bại do server', () => {
      userServiceSpy.deleteUser.and.returnValue(
        throwError(() => ({ error: { message: 'Server error' } }))
      );
      component.userToDelete = mockUser;
      component.confirmDelete();
      expect(userServiceSpy.deleteUser).toHaveBeenCalled();
    });

    it('TC14 - Đóng modal xóa', () => {
      component.userToDelete = mockUser;
      component.closeDeleteModal();
      expect(component.isDeleteModalOpen).toBeFalse();
      expect(component.userToDelete).toBeNull();
    });

    it('TC15 - Không xóa khi userToDelete null', () => {
      component.userToDelete = null;
      component.confirmDelete();
      expect(userServiceSpy.deleteUser).not.toHaveBeenCalled();
    });
  });

  // ====================================================
  // US 1.4 - Cập nhật trạng thái người dùng
  // ====================================================
  describe('US 1.4 - Cập nhật trạng thái người dùng', () => {
    const mockUser: User = {
      id: 1,
      username: 'user2',
      fullName: 'Nguyễn D',
      email: 'd@gmail.com',
      phone: '0909123459',
      role: 2,
      status: STATUS_ACTIVE,
      createdAt: '2024-01-01'
    };

    it('TC16 - Khóa người dùng thành công', () => {
      userServiceSpy.updateStatus.and.returnValue(of({}));
      component.toggleStatus(mockUser);
      expect(userServiceSpy.updateStatus).toHaveBeenCalledWith(mockUser.id, STATUS_BANNED);
    });

    it('TC17 - Mở khóa người dùng thành công', () => {
      userServiceSpy.updateStatus.and.returnValue(of({}));
      const bannedUser = { ...mockUser, status: STATUS_BANNED };
      component.toggleStatus(bannedUser);
      expect(userServiceSpy.updateStatus).toHaveBeenCalledWith(bannedUser.id, STATUS_ACTIVE);
    });

    it('TC18 - Cập nhật trạng thái thất bại do server', () => {
      userServiceSpy.updateStatus.and.returnValue(
        throwError(() => ({ error: { message: 'Server error' } }))
      );
      component.toggleStatus(mockUser);
      expect(userServiceSpy.updateStatus).toHaveBeenCalled();
    });

    it('TC19 - Không khóa Admin', () => {
      const adminUser = { ...mockUser, role: 0 };
      component.toggleStatus(adminUser);
      expect(userServiceSpy.updateStatus).not.toHaveBeenCalled();
    });

    it('TC20 - Xác nhận popup trước khi khóa', () => {
      spyOn(window, 'confirm').and.returnValue(false);
      component.toggleStatus(mockUser);
      expect(userServiceSpy.updateStatus).not.toHaveBeenCalled();
    });
  });

  // ====================================================
  // US mở rộng: Phân trang & tìm kiếm
  // ====================================================
  describe('US 1.x - Phân trang & tìm kiếm', () => {
    it('TC21 - Tìm kiếm reset trang', () => {
      component.keyword = 'abc';
      component.currentPage = 2;
      component.onSearch({ target: { value: 'xyz' } });
      expect(component.currentPage).toBe(0);
      expect(component.keyword).toBe('xyz');
    });

    it('TC22 - Thay đổi filter reset trang', () => {
      component.currentPage = 3;
      component.onFilterChange();
      expect(component.currentPage).toBe(0);
    });

    it('TC23 - Chuyển trang hợp lệ', () => {
      component.totalPages = 5;
      component.onPageChange(2);
      expect(component.currentPage).toBe(2);
    });

    it('TC24 - Chuyển trang không hợp lệ', () => {
      component.totalPages = 2;
      component.onPageChange(5);
      expect(component.currentPage).toBe(0);
    });

    it('TC25 - Tạo initials từ tên', () => {
      const initials = component.getInitials('Nguyễn Văn A');
      expect(initials).toBe('A');
    });

    it('TC26 - Lấy role name đúng', () => {
      expect(component.getRoleName(0)).toBe('Admin');
      expect(component.getRoleName(1)).toBe('Chủ trọ');
      expect(component.getRoleName(2)).toBe('Người thuê');
      expect(component.getRoleName(99)).toBe('Unknown');
    });

    it('TC27 - Modal thêm reset password validator', () => {
      component.openCreateModal();
      const validators = component.userForm.get('password')?.validator;
      expect(validators).toBeTruthy();
    });

    it('TC28 - Modal edit clear password validator', () => {
      const mockUser: User = {
        id: 1,
        username: 'x',
        fullName: 'x',
        email: 'x',
        phone: 'x',
        role: 2,
        status: STATUS_ACTIVE,
        createdAt: '2024-01-01'
      };
      component.openEditModal(mockUser);
      const validators = component.userForm.get('password')?.validator;
      expect(validators).toBeNull();
    });

    it('TC29 - Load users empty', () => {
      userServiceSpy.getUsers.and.returnValue(of(emptyPageResponse));
      component.loadUsers();
      expect(component.users.length).toBe(0);
    });

    it('TC30 - Load users non-empty', () => {
      const mockPage: PageResponse<User> = {
        content: [
          {
            id: 1,
            username: 'u',
            fullName: 'U',
            email: 'u@u.com',
            phone: '0909',
            role: 2,
            status: STATUS_ACTIVE,
            createdAt: '2024-01-01'
          }
        ],
        totalElements: 1,
        totalPages: 1,
        size: 15,
        number: 0
      };
      userServiceSpy.getUsers.and.returnValue(of(mockPage));
      component.loadUsers();
      expect(component.users.length).toBe(1);
    });
  });
});
