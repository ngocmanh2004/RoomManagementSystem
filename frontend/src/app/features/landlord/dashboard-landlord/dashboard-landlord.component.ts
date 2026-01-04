import { Component, OnInit, ViewChild, ElementRef, AfterViewInit } from '@angular/core';
import { CommonModule} from '@angular/common';
import { AuthService } from '../../../services/auth.service';
import { RoomService } from '../../../services/room.service';
import { InvoiceService } from '../../../services/invoice.service';
import { TenantService } from '../../../services/tenant.service';
import { Room } from '../../../models/room.model'; // Chú ý đường dẫn model Room
import { Invoice } from '../../../models/invoice.model';
import { Chart, registerables } from 'chart.js';

Chart.register(...registerables);

@Component({
  selector: 'app-dashboard-landlord',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './dashboard-landlord.component.html',
  styleUrls: ['./dashboard-landlord.component.css']
})
export class DashboardLandlordComponent implements OnInit, AfterViewInit {
  landlordId: number | null = null;

  currentDate = new Date();

  // Stats
  totalRooms = 0;
  emptyRooms = 0;
  occupiedRooms = 0;
  totalTenants = 0;
  monthlyRevenue = 0;

  // Data
  recentInvoices: Invoice[] = [];
  
  // Charts
  @ViewChild('revenueChart') revenueChartRef!: ElementRef;
  @ViewChild('roomStatusChart') roomStatusChartRef!: ElementRef;
  revenueChart: any;
  roomStatusChart: any;

  constructor(
    private authService: AuthService,
    private roomService: RoomService,
    private invoiceService: InvoiceService,
    private tenantService: TenantService
  ) {}

  ngOnInit(): void {
    this.landlordId = this.authService.getCurrentLandlordId();
    if (this.landlordId) {
      this.loadRoomStats();
      this.loadFinancialStats();
    }
  }

  ngAfterViewInit(): void {
    setTimeout(() => this.initCharts(), 1000);
  }

  loadRoomStats(): void {
    if (!this.landlordId) return;

    this.roomService.getRoomsByLandlord(this.landlordId).subscribe(rooms => {
      this.totalRooms = rooms.length;
      this.emptyRooms = rooms.filter(r => r.status === 'AVAILABLE').length;
      this.occupiedRooms = rooms.filter(r => r.status === 'OCCUPIED').length;
      
      // Số khách thuê xấp xỉ bằng số phòng đã thuê (hoặc gọi API tenant riêng nếu có)
      this.totalTenants = this.occupiedRooms; 

      this.updateRoomChart();
    });
  }

  loadFinancialStats(): void {
    if (!this.landlordId) return;

    const currentMonth = new Date().toISOString().slice(0, 7); // YYYY-MM

    // Lấy hóa đơn để tính doanh thu tháng này
    this.invoiceService.getByLandlordId(this.landlordId).subscribe(res => {
      if (res.data) {
        const invoices = res.data;
        
        // 1. Tính doanh thu tháng hiện tại (chỉ tính hóa đơn PAID)
        this.monthlyRevenue = invoices
          .filter(inv => inv.month === currentMonth && inv.status === 'PAID')
          .reduce((sum, inv) => sum + inv.totalAmount, 0);

        // 2. Lấy danh sách hóa đơn gần đây
        this.recentInvoices = invoices
          .sort((a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime())
          .slice(0, 5);

        // 3. Chuẩn bị dữ liệu cho biểu đồ doanh thu (6 tháng gần nhất)
        this.prepareRevenueChartData(invoices);
      }
    });
  }

 // --- Charts ---
  
  revenueData: number[] = [];
  revenueLabels: string[] = []; // Label hiển thị trục X (Tháng 1, Tháng 2...)
  revenueFullLabels: string[] = []; // Label đầy đủ cho Tooltip (Tháng 1/2026...)

  prepareRevenueChartData(invoices: Invoice[]): void {
    const months = [];
    const fullLabels = [];
    const data = [];
    
    // Lấy ngày hiện tại làm mốc
    const today = new Date();

    // Duyệt qua 6 tháng gần nhất (từ 5 tháng trước đến hiện tại)
    for (let i = 5; i >= 0; i--) {
      // Tạo object Date cho tháng đang xét
      const d = new Date(today.getFullYear(), today.getMonth() - i, 1);
      
      // Tạo chuỗi YYYY-MM chuẩn để so sánh với data từ Backend
      // Lưu ý: getMonth() trả về 0-11, cần +1. padStart để đảm bảo 2 chữ số (01, 02...)
      const year = d.getFullYear();
      const month = d.getMonth() + 1;
      const monthStr = `${year}-${month.toString().padStart(2, '0')}`; 
      
      // Tạo label hiển thị
      months.push(`Tháng ${month}`);
      fullLabels.push(`Tháng ${month}/${year}`);
      
      // Tính tổng doanh thu của tháng đó
      // Quan trọng: Chỉ lấy hóa đơn có trạng thái PAID và khớp chuỗi YYYY-MM
      const total = invoices
        .filter(inv => {
          // Kiểm tra null safety và format
          if (!inv.month || inv.status !== 'PAID') return false;
          // So sánh chuỗi tháng (Giả sử API trả về '2026-01')
          return inv.month === monthStr; 
        })
        .reduce((sum, inv) => sum + inv.totalAmount, 0);
      
      data.push(total);
    }

    this.revenueLabels = months;
    this.revenueFullLabels = fullLabels; // Lưu lại để dùng trong tooltip
    this.revenueData = data;
    
    this.initRevenueChart();
  }

  initCharts(): void {
    this.updateRoomChart();
    // Revenue chart will be initialized after data is ready in prepareRevenueChartData
  }

  initRevenueChart(): void {
    if (!this.revenueChartRef || this.revenueData.length === 0) return;
    
    const ctx = this.revenueChartRef.nativeElement.getContext('2d');
    if (this.revenueChart) this.revenueChart.destroy();

    this.revenueChart = new Chart(ctx, {
      type: 'line',
      data: {
        labels: this.revenueLabels,
        datasets: [{
          label: 'Doanh thu',
          data: this.revenueData,
          borderColor: '#3b82f6',
          backgroundColor: 'rgba(59, 130, 246, 0.1)',
          tension: 0.4,
          fill: true,
          pointBackgroundColor: '#fff',
          pointBorderColor: '#3b82f6',
          pointRadius: 5,
          pointHoverRadius: 7
        }]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        interaction: {
          mode: 'index',
          intersect: false,
        },
        plugins: { 
          legend: { display: false },
          tooltip: {
            backgroundColor: 'rgba(0, 0, 0, 0.8)',
            padding: 12,
            titleFont: { size: 14 },
            bodyFont: { size: 14, weight: 'bold' },
            displayColors: false,
            callbacks: {
              title: (tooltipItems) => {
                const index = tooltipItems[0].dataIndex;
                return this.revenueFullLabels[index]; 
              },
              label: (context) => {
                // ✅ SỬA LỖI Ở ĐÂY: Thêm '|| 0' để đảm bảo value luôn là số
                let value = context.parsed.y || 0; 
                
                return new Intl.NumberFormat('vi-VN', { 
                  style: 'currency', 
                  currency: 'VND' 
                }).format(value);
              }
            }
          }
        },
        scales: {
          y: { 
            beginAtZero: true,
            grid: { color: '#f3f4f6' },
            ticks: { 
              callback: (val) => {
                const num = val as number;
                if (num >= 1000000) return (num / 1000000) + 'M';
                if (num >= 1000) return (num / 1000) + 'k';
                return num;
              } 
            }
          },
          x: { 
            grid: { display: false } 
          }
        }
      }
    });
  }

  updateRoomChart(): void {
    if (!this.roomStatusChartRef) return;
    const ctx = this.roomStatusChartRef.nativeElement.getContext('2d');
    
    if (this.roomStatusChart) this.roomStatusChart.destroy();

    const repairing = this.totalRooms - this.emptyRooms - this.occupiedRooms;

    this.roomStatusChart = new Chart(ctx, {
      type: 'doughnut',
      data: {
        labels: ['Trống', 'Đã thuê', 'Đang sửa'],
        datasets: [{
          data: [this.emptyRooms, this.occupiedRooms, repairing],
          backgroundColor: ['#10b981', '#3b82f6', '#f59e0b'],
          borderWidth: 0,
          hoverOffset: 4
        }]
      },
      options: {
        responsive: true,
        cutout: '75%',
        plugins: {
          legend: { position: 'bottom', labels: { usePointStyle: true } }
        }
      }
    });
  }
}