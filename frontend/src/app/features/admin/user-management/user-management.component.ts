import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { User, UserService } from '../../../services/user.service';
import { registerLocaleData } from '@angular/common';
import localeVi from '@angular/common/locales/vi';

@Component({
  selector: 'app-user-management',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, FormsModule],
  templateUrl: './user-management.component.html',
  styleUrls: ['./user-management.component.css']
})
export class UserManagementComponent implements OnInit {
  users: User[] = [];
  userForm: FormGroup;
  
  // Filter states
  keyword: string = '';
  selectedRole: string = 'ALL';
  selectedStatus: string = 'ALL';

  // Pagination states
  currentPage: number = 0;
  pageSize: number = 15;
  totalElements: number = 0;
  totalPages: number = 0;

  // Modal states
  isModalOpen = false;
  isEditMode = false;
  currentUserId: number | null = null;

  // Delete Modal states
  isDeleteModalOpen = false;
  userToDelete: User | null = null;

  // Thời gian thực
  currentDate: Date = new Date();

  constructor(
    private userService: UserService,
    private fb: FormBuilder
  ) {
    registerLocaleData(localeVi, 'vi-VN');
    
    this.userForm = this.fb.group({
      username: ['', Validators.required],
      password: [''], // Validator cho password sẽ được thêm/bớt tùy theo mode (Thêm/Sửa)
      fullName: ['', Validators.required],
      
      // Ràng buộc Email: Bắt buộc + Regex chuẩn
      email: ['', [
        Validators.required, 
        Validators.pattern(/^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,4}$/)
      ]],
      
      // Ràng buộc SĐT: Bắt buộc + Regex SĐT Việt Nam (10 số)
      phone: ['', [
        Validators.required,
        Validators.pattern(/^(0?)(3[2-9]|5[6|8|9]|7[0|6-9]|8[0-6|8|9]|9[0-4|6-9])[0-9]{7}$/)
      ]],
      
      role: [2, Validators.required],
      status: ['ACTIVE', Validators.required]
    });
  }

  ngOnInit(): void {
    this.loadUsers();
  }

  loadUsers() {
    const roleParam = this.selectedRole !== 'ALL' ? parseInt(this.selectedRole) : undefined;
    const statusParam = this.selectedStatus !== 'ALL' ? this.selectedStatus : undefined;

    this.userService.getUsers(this.keyword, roleParam, statusParam, this.currentPage, this.pageSize).subscribe({
      next: (response) => {
        this.users = response.content;
        this.totalElements = response.totalElements;
        this.totalPages = response.totalPages;
      },
      error: (err) => console.error('Failed to load users', err)
    });
  }

  onSearch(event: any) {
    this.keyword = event.target.value;
    this.currentPage = 0;
    this.loadUsers();
  }

  onFilterChange() {
    this.currentPage = 0;
    this.loadUsers();
  }

  onPageChange(page: number) {
    if (page >= 0 && page < this.totalPages) {
      this.currentPage = page;
      this.loadUsers();
    }
  }

  getInitials(name: string): string {
    if (!name) return '';
    const names = name.split(' ');
    return names[names.length - 1].charAt(0).toUpperCase();
  }

  // === MODAL THÊM / SỬA ===
  openCreateModal() {
    this.isEditMode = false;
    this.currentUserId = null;
    this.userForm.reset({ role: 2, status: 'ACTIVE' });
    this.userForm.get('password')?.setValidators([Validators.required]); // Bắt buộc pass khi tạo mới
    this.userForm.get('username')?.enable(); 
    this.isModalOpen = true;
  }

  openEditModal(user: User) {
    this.isEditMode = true;
    this.currentUserId = user.id;
    this.userForm.patchValue({
      username: user.username,
      fullName: user.fullName,
      email: user.email,
      phone: user.phone,
      role: user.role,
      status: user.status,
      password: '' 
    });
    this.userForm.get('password')?.clearValidators(); // Không bắt buộc pass khi sửa
    this.userForm.get('password')?.updateValueAndValidity();
    this.userForm.get('username')?.disable(); 
    this.isModalOpen = true;
  }

  closeModal() {
    this.isModalOpen = false;
  }

  onSubmit() {
    if (this.userForm.invalid) {
      this.userForm.markAllAsTouched(); // Hiển thị lỗi đỏ nếu người dùng chưa nhập gì mà bấm Lưu
      return;
    }

    const formValue = this.userForm.getRawValue(); 
    const userData = {
        ...formValue,
        role: Number(formValue.role)
    };

    if (this.isEditMode && this.currentUserId) {
      if (!userData.password) delete userData.password;

      this.userService.updateUser(this.currentUserId, userData).subscribe({
        next: () => {
          alert('Đã cập nhật thông tin người dùng thành công!');
          this.loadUsers();
          this.closeModal();
        },
        error: (err) => alert(err.error?.message || 'Lỗi cập nhật')
      });
    } else {
      this.userService.createUser(userData).subscribe({
        next: () => {
          alert('Đã thêm người dùng mới thành công!');
          this.loadUsers();
          this.closeModal();
        },
        error: (err) => alert(err.error?.message || 'Lỗi thêm mới')
      });
    }
  }

  // === KHÓA / MỞ KHÓA ===
  toggleStatus(user: User) {
    const newStatus = user.status === 'ACTIVE' ? 'BANNED' : 'ACTIVE';
    const confirmMsg = newStatus === 'BANNED' 
      ? 'Bạn có chắc muốn KHÓA tài khoản này? Người dùng sẽ bị đăng xuất ngay lập tức.' 
      : 'Mở khóa tài khoản này?';

    if (confirm(confirmMsg)) {
      this.userService.updateStatus(user.id, newStatus).subscribe({
        next: () => {
          const msg = newStatus === 'BANNED' ? 'Đã khóa tài khoản thành công!' : 'Đã mở khóa tài khoản thành công!';
          alert(msg);
          this.loadUsers();
        },
        error: (err) => alert(err.error?.message || 'Lỗi cập nhật trạng thái')
      });
    }
  }

  // === MODAL XÓA ===
  openDeleteModal(user: User) {
    this.userToDelete = user;
    this.isDeleteModalOpen = true;
  }

  closeDeleteModal() {
    this.isDeleteModalOpen = false;
    this.userToDelete = null;
  }

  confirmDelete() {
    if (!this.userToDelete) return;

    this.userService.deleteUser(this.userToDelete.id).subscribe({
      next: () => {
        alert('Đã xóa người dùng thành công!');
        this.loadUsers();
        this.closeDeleteModal();
      },
      error: (err) => {
        alert(err.error?.message || 'Không thể xóa tài khoản. Vui lòng kiểm tra dữ liệu liên quan.');
        this.closeDeleteModal();
      }
    });
  }

  getRoleName(role: number): string {
    switch(role) {
      case 0: return 'Admin';
      case 1: return 'Chủ trọ';
      case 2: return 'Người thuê';
      default: return 'Unknown';
    }
  }
}