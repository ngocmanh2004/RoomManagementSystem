import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { TenantService } from '../../../services/tenant.service';
import { HttpClientModule } from '@angular/common/http';

@Component({
  selector: 'app-tenant-management',
  standalone: true,
  imports: [CommonModule, FormsModule, HttpClientModule],
  templateUrl: './tenant-management.component.html',
  styleUrls: ['./tenant-management.component.css']
})
export class TenantManagementComponent implements OnInit {
  tenants: any[] = [];
  filteredTenants: any[] = [];
  countDangThue = 0;
  countDaNghi = 0;
  countKhachMoi = 0;
  today = new Date();

  isFormVisible = false;
  mode: 'add' | 'edit' | 'view' = 'add';

   searchText = '';                  // input tìm kiếm
  selectedStatus = '';              // filter trạng thái

  // ===== PAGINATION =====
  currentPage = 1;
  pageSize = 5;           // số khách / trang
  totalPages = 0;

  // danh sách hiển thị theo trang
  pagedTenants: any[] = [];

  // ✅ Tenant gồm cả user
  currentTenant: any = {
    id: 0,
    userId: 0,
    cccd: '',
    dateOfBirth: '',
    address: '',
    user: { fullName: '', phone: '', email: '', status: 'ACTIVE', createdAt: new Date() }
  };

  constructor(private tenantService: TenantService) {}

  ngOnInit() {
    this.loadTenants();
  }
  validateTenant(): string | null {

    const user = this.currentTenant.user;

    // --- FULL NAME ---
    if (!user.fullName || user.fullName.trim() === '') {
      return 'Họ tên không được để trống!';
    }
    if (user.fullName.length < 3) {
      return 'Họ tên phải có ít nhất 3 ký tự!';
    }

    // --- PHONE ---
    const phoneRegex = /^(03|05|07|08|09)[0-9]{8}$/;
    if (!user.phone) {
      return 'Số điện thoại không được để trống!';
    }
    if (!phoneRegex.test(user.phone)) {
      return 'Số điện thoại không hợp lệ! (10 số, đầu 03-05-07-08-09)';
    }

    // --- EMAIL ---
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (user.email && !emailRegex.test(user.email)) {
      return 'Email không hợp lệ!';
    }

    // --- CCCD ---
    const cccdRegex = /^[0-9]{12}$/;
    if (!this.currentTenant.cccd) {
      return 'CCCD không được để trống!';
    }
    if (!cccdRegex.test(this.currentTenant.cccd)) {
      return 'CCCD phải gồm 12 chữ số!';
    }

    // --- DATE OF BIRTH ---
    if (!this.currentTenant.dateOfBirth) {
      return 'Ngày sinh không được để trống!';
    }
    const dob = new Date(this.currentTenant.dateOfBirth);
    const now = new Date();
    if (dob >= now) {
      return 'Ngày sinh không hợp lệ!';
    }

    // --- CHECK DUPLICATE PHONE ---
    const duplicate = this.tenants.find(t =>
      t.user?.phone === user.phone && t.id !== this.currentTenant.id
    );
    if (duplicate) {
      return 'Số điện thoại đã được sử dụng!';
    }

    return null; // hợp lệ
  }

  loadTenants() {
    this.tenantService.getMyTenants().subscribe({
      next: (data) => {
        this.tenants = data;
        this.filterTenants();
      },
      error: (err) => {
        console.error(err);
        alert('Không thể tải danh sách khách thuê');
      }
    });
  }

  // ----- FILTER & SEARCH -----
  filterTenants() {
    const keyword = this.searchText?.toLowerCase() || '';

    this.filteredTenants = this.tenants.filter(t => {
      const status = t.user?.status || '';

      const fullName = (t.user?.fullName || '').toLowerCase();
      const phone = (t.user?.phone || '');
      const cccd = (t.cccd || '');

      const matchStatus = this.selectedStatus
        ? status === this.selectedStatus
        : true;

      const matchSearch = keyword
        ? fullName.includes(keyword) ||
          phone.includes(keyword) ||
          cccd.includes(keyword)
        : true;

      return matchStatus && matchSearch;
    });

    this.updateCounts();

    this.currentPage = 1;
    this.applyPagination();
  }

  openAddForm() {
  this.mode = 'add';
  this.currentTenant = {
    id: 0,
    userId: 0,
    cccd: '',
    dateOfBirth: '',
    address: '',
    user: { fullName: '', phone: '', email: '', status: 'ACTIVE', createdAt: new Date() }
   };
  this.isFormVisible = true;
}

  viewTenant(t: any) {
    this.mode = 'view';
    this.currentTenant = JSON.parse(JSON.stringify(t));
    this.isFormVisible = true;
  }

  editTenant(t: any) {
    this.mode = 'edit';
    this.currentTenant = JSON.parse(JSON.stringify(t));
    this.isFormVisible = true;
  }

   closeForm() {
    this.isFormVisible = false;
  }

  saveTenant() {

    const validationError = this.validateTenant();
    if (validationError) {
      alert(validationError);
      return;
    }

    if (this.mode === 'edit') {
      // update
      this.tenantService.update(this.currentTenant.id, this.currentTenant).subscribe({
        next: () => {
          alert('Cập nhật thành công!');
          this.loadTenants();
          this.closeForm();
        },
        error: () => alert('Không thể cập nhật!')
      });
    }
    else {
      // add
      const registerRequest = {
        username: this.currentTenant.user.phone,
        fullName: this.currentTenant.user.fullName,
        email: this.currentTenant.user.email,
        phone: this.currentTenant.user.phone,
        address: this.currentTenant.address,
        cccd: this.currentTenant.cccd,
        dateOfBirth: this.currentTenant.dateOfBirth
      };

      this.tenantService.add(registerRequest).subscribe({
        next: (res: any) => {

          const newTenant = res.tenant;

          // ✅ Gán user cho chắc
          newTenant.user.createdAt = new Date();
          newTenant.user.status = 'ACTIVE';

          // ✅ Thêm ngay vào danh sách
          this.tenants.unshift(newTenant);
          this.filterTenants();

          alert(`
      Thêm khách thuê thành công!
      Tài khoản: ${res.username}
      Mật khẩu: ${res.password}
          `);

          this.closeForm();
        }
      });
    }
  }


 // ----- CẬP NHẬT TRẠNG THÁI TRỰC TIẾP -----
  updateStatus(t: any) {
    this.tenantService.update(t.id, t).subscribe({
      next: () => {
        this.filterTenants(); // cập nhật bảng + counts
      },
      error: (err) => {
        console.error('Lỗi khi cập nhật trạng thái:', err);
        alert('Không thể cập nhật trạng thái!');
      }
    });
  }
  updateCounts() {
    const now = new Date();
    const currentMonth = now.getMonth();
    const currentYear = now.getFullYear();

    this.countDangThue = this.filteredTenants.filter(t => t.user?.status === 'ACTIVE').length;
    this.countDaNghi = this.filteredTenants.filter(t => t.user?.status === 'PENDING').length;

    // Tính khách mới trong tháng
    this.countKhachMoi = this.filteredTenants.filter(t => {
      const created = new Date(t.user?.createdAt);
      return created.getMonth() === currentMonth && created.getFullYear() === currentYear;
    }).length;
  }

  applyPagination() {
    this.totalPages = Math.ceil(this.filteredTenants.length / this.pageSize);

    const startIndex = (this.currentPage - 1) * this.pageSize;
    const endIndex = startIndex + this.pageSize;

    this.pagedTenants = this.filteredTenants.slice(startIndex, endIndex);
  }

  goToPage(page: number) {
    if (page < 1 || page > this.totalPages) return;
    this.currentPage = page;
    this.applyPagination();
  }

  nextPage() {
    if (this.currentPage < this.totalPages) {
      this.currentPage++;
      this.applyPagination();
    }
  }

  prevPage() {
    if (this.currentPage > 1) {
      this.currentPage--;
      this.applyPagination();
    }
  }

}
