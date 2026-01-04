import { Component, OnInit, AfterViewInit, ViewChild, ElementRef } from '@angular/core';
import { CommonModule, DecimalPipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { UserService, User } from '../../../services/user.service';
import { LandlordService } from '../../../services/landlord.service';
import { RoomService } from '../../../services/room.service';
import { ReviewService } from '../../../services/review.service';
import { LandlordRequest } from '../../../models/landlord.model';
import { ReviewReport } from '../../../models/review-report.model';
import { Chart, registerables } from 'chart.js';

Chart.register(...registerables);

@Component({
  selector: 'app-dashboard-admin',
  standalone: true,
  imports: [CommonModule, RouterLink, DecimalPipe],
  templateUrl: './dasboard-admin.component.html',
  styleUrls: ['./dasboard-admin.component.css']
})
export class DashboardAdminComponent implements OnInit, AfterViewInit {
  currentDate = new Date();
  
  // Thống kê
  totalUsers = 0;
  totalLandlords = 0;
  totalRooms = 0;

  // Dữ liệu bảng
  pendingRequests: LandlordRequest[] = [];
  recentReports: ReviewReport[] = [];

  // Charts references
  @ViewChild('userGrowthChart') userGrowthChartRef!: ElementRef;
  @ViewChild('userRatioChart') userRatioChartRef!: ElementRef;

  // Chart instances
  chartUserGrowth: any;
  chartUserRatio: any;

  constructor(
    private userService: UserService,
    private landlordService: LandlordService,
    private roomService: RoomService,
    private reviewService: ReviewService
  ) {}

  ngOnInit(): void {
    this.loadStatsAndCharts();
    this.loadRecentData();
  }

  ngAfterViewInit(): void {
    // Biểu đồ sẽ được khởi tạo sau khi có dữ liệu từ API gọi trong ngOnInit
  }

  loadStatsAndCharts(): void {
    // 1. Lấy TẤT CẢ người dùng (Role = undefined để lấy hết, Size lớn để tính biểu đồ)
    this.userService.getUsers('', undefined, '', 0, 1000).subscribe(res => {
      const allUsers = res.content || [];
      
      // Cập nhật tổng số người dùng (Bao gồm Admin, Chủ trọ, Khách thuê)
      this.totalUsers = res.totalElements;

      // Xử lý dữ liệu cho biểu đồ tăng trưởng từ createdAt
      this.processUserGrowthData(allUsers);

      // Cập nhật biểu đồ tỉ lệ (Người thuê vs Chủ trọ vs Admin)
      this.processUserRatioData(allUsers);
    });

    // 2. Lấy tổng số chủ trọ (để hiển thị thẻ stats riêng)
    this.landlordService.getAllLandlords().subscribe(res => {
      if (res.data) {
        this.totalLandlords = res.data.length;
      }
    });

    // 3. Lấy tổng số phòng
    this.roomService.getAllRooms().subscribe(rooms => {
      this.totalRooms = rooms.length;
    });
  }

  loadRecentData(): void {
    // Lấy yêu cầu đăng ký chủ trọ gần đây
    this.landlordService.getPendingRequests().subscribe(res => {
      if (res.data) {
        this.pendingRequests = res.data.slice(0, 5);
      }
    });

    // Lấy báo cáo vi phạm gần đây
    this.reviewService.getReviewReports().subscribe((response: any) => {
      if (response.content && Array.isArray(response.content)) {
        this.recentReports = response.content.slice(0, 5);
      } else if (Array.isArray(response)) {
        this.recentReports = response.slice(0, 5);
      } else {
        this.recentReports = [];
      }
    });
  }

  // --- XỬ LÝ BIỂU ĐỒ TĂNG TRƯỞNG (LOGIC MỚI) ---
  processUserGrowthData(users: User[]): void {
    const months: string[] = [];
    const counts: number[] = [];
    
    const today = new Date();

    // Loop 6 tháng gần nhất (bao gồm tháng hiện tại)
    for (let i = 5; i >= 0; i--) {
      // Tạo ngày mốc (ví dụ: tháng 8, tháng 9...)
      const date = new Date(today.getFullYear(), today.getMonth() - i, 1);
      const monthLabel = `T${date.getMonth() + 1}`;
      
      months.push(monthLabel);

      // Đếm số user được tạo trong tháng này
      const count = users.filter(user => {
        if (!user.createdAt) return false;
        const created = new Date(user.createdAt);
        return created.getMonth() === date.getMonth() && 
               created.getFullYear() === date.getFullYear();
      }).length;

      counts.push(count);
    }

    // Vẽ biểu đồ sau khi tính toán xong
    setTimeout(() => {
      this.initUserGrowthChart(months, counts);
    }, 500); 
  }

  processUserRatioData(users: User[]): void {
    // Đếm dựa trên role trong danh sách users tải về
    const tenants = users.filter(u => u.role === 2).length;
    const landlords = users.filter(u => u.role === 1).length;
    const admins = users.filter(u => u.role === 0).length;

    setTimeout(() => {
      this.updateRatioChart(tenants, landlords, admins);
    }, 500);
  }

  // --- VẼ BIỂU ĐỒ ---

  initUserGrowthChart(labels: string[], data: number[]): void {
    if (!this.userGrowthChartRef) return;
    const ctx = this.userGrowthChartRef.nativeElement.getContext('2d');
    
    if (this.chartUserGrowth) this.chartUserGrowth.destroy();

    this.chartUserGrowth = new Chart(ctx, {
      type: 'bar',
      data: {
        labels: labels,
        datasets: [{
          label: 'Người dùng mới',
          data: data, // Dữ liệu thật từ createdAt
          backgroundColor: '#3b82f6',
          borderRadius: 4,
          barThickness: 30
        }]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          legend: { display: false },
          tooltip: {
            callbacks: {
              label: (context) => `Người dùng mới: ${context.raw}`
            }
          }
        },
        scales: {
          y: { 
            beginAtZero: true, 
            grid: { display: false },
            ticks: { stepSize: 1 } // Vì số lượng ít nên chia vạch số nguyên
          },
          x: { grid: { display: false } }
        }
      }
    });
  }

  updateRatioChart(tenants: number, landlords: number, admins: number): void {
    if (!this.userRatioChartRef) return;
    const ctx = this.userRatioChartRef.nativeElement.getContext('2d');
    
    if (this.chartUserRatio) this.chartUserRatio.destroy();

    this.chartUserRatio = new Chart(ctx, {
      type: 'doughnut',
      data: {
        labels: ['Người thuê', 'Chủ trọ', 'Quản trị viên'],
        datasets: [{
          data: [tenants, landlords, admins],
          backgroundColor: ['#3b82f6', '#10b981', '#f97316'],
          hoverOffset: 4
        }]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        cutout: '70%',
        plugins: {
          legend: { 
            position: 'bottom',
            labels: {
              usePointStyle: true,
              padding: 20
            }
          },
          tooltip: {
            callbacks: {
              label: function(context) {
                const label = context.label || '';
                const value = context.raw || 0;
                
                // --- SỬA LỖI Ở ĐÂY ---
                // Ép kiểu context.chart về any để truy cập thuộc tính ẩn _metasets
                const total = (context.chart as any)._metasets[context.datasetIndex].total;
                
                const percentage = Math.round((value as number / total) * 100) + '%';
                return `${label}: ${value} (${percentage})`;
              }
            }
          }
        }
      }
    });
  }

  // --- Xử lý hành động bảng ---
  approveRequest(id: number): void {
    if(confirm('Duyệt yêu cầu này?')) {
      this.landlordService.approveRequest(id).subscribe(() => {
        alert('Đã duyệt thành công!');
        this.loadRecentData();
        this.loadStatsAndCharts();
      });
    }
  }

  rejectRequest(id: number): void {
    const reason = prompt('Nhập lý do từ chối:');
    if (reason) {
      this.landlordService.rejectRequest(id, reason).subscribe(() => {
        alert('Đã từ chối yêu cầu.');
        this.loadRecentData();
      });
    }
  }
}