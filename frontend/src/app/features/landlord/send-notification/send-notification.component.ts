import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, FormsModule, Validators } from '@angular/forms';
import { NotificationService } from '../../../services/notification.service';
import { SendNotificationResponse } from '../../../models/notification.model';
import { RoomService, Room } from '../../../services/room.service';
import { UserService, User } from '../../../services/user.service';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';

@Component({
  selector: 'app-send-notification',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    FormsModule,
  ],
  templateUrl: './send-notification.component.html',
  styleUrls: ['./send-notification.component.css'] 
})
export class SendNotificationComponent implements OnInit {
  form: FormGroup;
  rooms: Room[] = [];
  tenants: User[] = [];
  sending = false;
  alertMessage = '';
  alertType: 'success' | 'error' | '' = '';
  openRoomSelector = false;
  openUserSelector = false; 
  loadingTenants = false;
  loadingRooms = false;

  constructor(
    private fb: FormBuilder,
    private notificationService: NotificationService,
    private roomService: RoomService,
    private userService: UserService
  ) {
    this.form = this.fb.group({
      title: ['', Validators.required],
      message: ['', Validators.required],
      sendTo: ['ALL', Validators.required],
      selectedRoomIds: [[]],
      selectedUserIds: [[]],
      sendEmail: [false]
    });
  }

  ngOnInit(): void {
    this.loadRooms();
    this.loadTenants();
  }

  private loadRooms(): void {
    this.loadingRooms = true;
    this.roomService.getAllRooms().subscribe({
      next: (rooms: Room[]) => {
        this.rooms = rooms;
        this.loadingRooms = false;
        console.log('Danh sách phòng:', this.rooms);
      },
      error: (err) => {
        this.showError('Không tải được danh sách phòng');
        this.loadingRooms = false;
      }
    });
  }

  // Helper: lấy danh sách roomId đã chọn
  get selectedRoomIds(): any[] {
    return this.form.get('selectedRoomIds')?.value || [];
  }

  // Lấy tên phòng theo ID
  getRoomName(id: any): string {
    const room = this.rooms.find(r => r.id === id);
    return room ? room.name : 'Phòng không xác định';
  }

  // Toggle chọn phòng
  toggleRoom(roomId: any, checked: boolean): void {
    const current = [...this.selectedRoomIds];
    if (checked) {
      if (!current.includes(roomId)) current.push(roomId);
    } else {
      const index = current.indexOf(roomId);
      if (index > -1) current.splice(index, 1);
    }
    this.form.get('selectedRoomIds')?.setValue(current);
  }

  // Xóa 1 phòng khỏi danh sách đã chọn
  removeRoom(roomId: any): void {
    const current = this.selectedRoomIds.filter(id => id !== roomId);
    this.form.get('selectedRoomIds')?.setValue(current);
  }

  // Helper: lấy danh sách userId đã chọn
  get selectedUserIds(): any[] {
    return this.form.get('selectedUserIds')?.value || [];
  }

  // Lấy tên user theo ID - ĐƠN GIẢN HÓA
  getUserName(id: any): string {
    const user = this.tenants.find(u => u.id === id);
    return user ? (user.fullName || user.username || 'Không tên') : 'Không tìm thấy';
  }

  // Lấy thông tin user đầy đủ để hiển thị trong popup
  getUserInfo(user: User): string {
    let info = user.fullName || user.username || 'Không tên';
    if (user.email) info += ` (${user.email})`;
    if (user.phone) info += ` • ${user.phone}`;
    return info;
  }

  // Toggle chọn user
  toggleUser(userId: any, checked: boolean): void {
    const current = [...this.selectedUserIds];
    if (checked) {
      if (!current.includes(userId)) current.push(userId);
    } else {
      const index = current.indexOf(userId);
      if (index > -1) current.splice(index, 1);
    }
    this.form.get('selectedUserIds')?.setValue(current);
  }

  // Xóa 1 user khỏi danh sách đã chọn
  removeUser(userId: any): void {
    const current = this.selectedUserIds.filter(id => id !== userId);
    this.form.get('selectedUserIds')?.setValue(current);
  }

  private loadTenants(): void {
    this.loadingTenants = true;
    this.userService.getTenants().subscribe({
      next: (tenants: User[]) => {
        this.tenants = tenants;
        this.loadingTenants = false;
        console.log('Danh sách khách thuê:', this.tenants);
      },
      error: (err) => {
        console.error('Lỗi tải tenants:', err);
        this.showError('Không tải được danh sách khách thuê');
        this.tenants = [];
        this.loadingTenants = false;
      }
    });
  }

  send(): void {
    if (this.form.invalid) return;

    this.sending = true;

    const value = this.form.value;

    const payload: any = {
      title: value.title,
      message: value.message,
      sendTo: value.sendTo,
      sendEmail: value.sendEmail
    };

    if (value.sendTo === 'ROOMS') {
      payload.roomIds = value.selectedRoomIds.map((id: any) => Number(id));
    }

    if (value.sendTo === 'USERS') {
      payload.userIds = value.selectedUserIds.map((id: any) => Number(id));
    }

    this.notificationService.send(payload).subscribe({
      next: () => {
        this.sending = false;
        this.alertType = 'success';
        this.alertMessage = 'Gửi thông báo thành công ✅';
        // Tự động ẩn thông báo sau 1 giây (1000ms)
        setTimeout(() => {
          this.alertMessage = '';
          this.alertType = '';
        }, 1000);
        // Reset form sau khi gửi thành công
        setTimeout(() => {
          this.form.get('title')?.setValue('');
          this.form.get('message')?.setValue('');
          this.form.get('selectedRoomIds')?.setValue([]);
          this.form.get('selectedUserIds')?.setValue([]);
          this.openRoomSelector = false;
          this.openUserSelector = false;
        }, 0);
      },
      error: () => {
        this.sending = false;
        this.alertType = 'error';
        this.alertMessage = 'Gửi thất bại ❌';
      }
    });
  }

  // ========== Helper để hiển thị thông báo đẹp ==========
  private showSuccess(message: string): void {
    this.alertMessage = message;
    this.alertType = 'success';
    setTimeout(() => this.alertMessage = '', 0); 
  }

  private showError(message: string): void {
    this.alertMessage = message;
    this.alertType = 'error';
    setTimeout(() => this.alertMessage = '', 8000);
  }

  // Thêm hàm để kiểm tra xem user có được chọn không
  isUserSelected(userId: any): boolean {
    return this.selectedUserIds.includes(userId);
  }

  // Thêm hàm để kiểm tra xem room có được chọn không
  isRoomSelected(roomId: any): boolean {
    return this.selectedRoomIds.includes(roomId);
  }
}