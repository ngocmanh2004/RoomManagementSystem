import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="dashboard">
      <header>
        <h1>Admin Dashboard</h1>
        <div class="user-info">
          <span>{{ currentUser?.fullName }}</span>
          <button (click)="logout()">Đăng xuất</button>
        </div>
      </header>
      
      <div class="content">
        <div class="card">
          <i class="fa-solid fa-users"></i>
          <h3>Quản lý người dùng</h3>
        </div>
        
        <div class="card">
          <i class="fa-solid fa-building"></i>
          <h3>Quản lý phòng trọ</h3>
        </div>
        
        <div class="card">
          <i class="fa-solid fa-chart-line"></i>
          <h3>Thống kê</h3>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .dashboard { padding: 2rem; }
    header { display: flex; justify-content: space-between; margin-bottom: 2rem; }
    .content { display: grid; grid-template-columns: repeat(auto-fit, minmax(250px, 1fr)); gap: 1rem; }
    .card { background: white; padding: 2rem; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); text-align: center; }
    button { padding: 0.5rem 1rem; background: #dc3545; color: white; border: none; border-radius: 4px; cursor: pointer; }
  `]
})
export class AdminDashboardComponent implements OnInit {
  currentUser: any;

  constructor(private authService: AuthService, private router: Router) {}

  ngOnInit() {
    this.currentUser = this.authService.getCurrentUser();
  }

  logout() {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}