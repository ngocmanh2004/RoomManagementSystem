import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { NotificationService } from '../../../services/notification.service';
import { SendNotificationRequest } from '../../../models/notification.model';

interface Room {
  id: number;
  name: string;
  building?: { name: string };
  tenant?: { user: { fullName: string } };
}

@Component({
  selector: 'app-send-notification',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './send-notification.component.html',
  styleUrls: ['./send-notification.component.css']
})
export class SendNotificationComponent implements OnInit {
  rooms: Room[] = [];
  selectedRooms: number[] = [];
  loading = false;
  sending = false;

  notification: SendNotificationRequest = {
    title: '',
    message: '',
    sendTo: 'ROOMS',
    roomIds: [],
    sendEmail: false
  };

  sentHistory: any[] = [];

  private apiUrl = 'http://localhost:8081/api';

  constructor(
    private notificationService: NotificationService,
    private http: HttpClient
  ) {}

  ngOnInit() {
    this.loadRooms();
    this.loadSentHistory();
  }

  loadRooms() {
    this.loading = true;
    this.http.get<Room[]>(`${this.apiUrl}/rooms/my`).subscribe({
      next: (data) => {
        this.rooms = data;
        this.loading = false;
      },
      error: (err) => {
        console.error('Error loading rooms:', err);
        this.loading = false;
      }
    });
  }

  loadSentHistory() {
    // Mock sent history - có thể tạo API riêng để lấy lịch sử
    this.sentHistory = [
      {
        id: 1,
        title: 'Nhắc nhở đóng tiền phòng tháng 12',
        message: 'Kính gửi quý khách, đây là thông báo nhắc nhở...',
        recipient: 'Tất cả phòng',
        sentAt: '2024-12-01 09:00',
        sentCount: 15
      }
    ];
  }

  toggleRoomSelection(roomId: number) {
    const index = this.selectedRooms.indexOf(roomId);
    if (index > -1) {
      this.selectedRooms.splice(index, 1);
    } else {
      this.selectedRooms.push(roomId);
    }
  }

  selectAllRooms() {
    if (this.selectedRooms.length === this.rooms.length) {
      this.selectedRooms = [];
    } else {
      this.selectedRooms = this.rooms.map((r: Room) => r.id);
    }
  }

  sendNotification() {
    if (!this.notification.title || !this.notification.message) {
      alert('Vui lòng điền đầy đủ tiêu đề và nội dung!');
      return;
    }

    if (this.notification.sendTo === 'ROOMS' && this.selectedRooms.length === 0) {
      alert('Vui lòng chọn ít nhất một phòng!');
      return;
    }

    this.sending = true;
    this.notification.roomIds = this.selectedRooms;

    this.notificationService.send(this.notification).subscribe({
      next: (response) => {
        alert(`Gửi thông báo thành công đến ${response.sentToCount} khách thuê!`);
        this.resetForm();
        this.loadSentHistory();
        this.sending = false;
      },
      error: (err) => {
        console.error('Error sending notification:', err);
        alert('Có lỗi xảy ra khi gửi thông báo!');
        this.sending = false;
      }
    });
  }

  resetForm() {
    this.notification = {
      title: '',
      message: '',
      sendTo: 'ROOMS',
      roomIds: [],
      sendEmail: false
    };
    this.selectedRooms = [];
  }

  getCurrentDate(): string {
    const today = new Date();
    return today.toISOString().split('T')[0];
  }

  get selectedRoomsCount(): number {
    return this.selectedRooms.length;
  }
}