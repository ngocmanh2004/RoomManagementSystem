import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink, RouterLinkActive, RouterModule } from '@angular/router'; 
import { AuthService } from '../../../services/auth.service';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [CommonModule, RouterModule, RouterLink, RouterLinkActive], 
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css']
})
export class HeaderComponent implements OnInit {
  isLoggedIn = false;
  currentUser: any = null;

  constructor(
    public authService: AuthService,
    private router: Router
  ) {}

  ngOnInit() {
    this.authService.currentUser$.subscribe(user => {
      this.currentUser = user;
      this.isLoggedIn = !!user;
    });
  }

  logout() {
    if (confirm('Bạn có chắc chắn muốn đăng xuất?')) {
      this.authService.logout().subscribe({
        next: () => {
          alert('Đăng xuất thành công!');
          this.router.navigate(['/']);
        },
        error: (err) => {
          console.error('Logout error:', err);
          this.router.navigate(['/']);
        }
      });
    }
  }

  getRoleName(role: number): string {
    switch(role) {
      case 0: return 'Admin';
      case 1: return 'Chủ trọ';
      case 2: return 'Khách thuê';
      default: 
        console.error('Invalid role:', role);
        return 'Không xác định'; 
    }
  }

  getDashboardLink(): string {
    if (!this.currentUser) return '/';
    
    switch(this.currentUser.role) {
      case 0: return '/admin/dashboard';
      case 1: return '/landlord/dashboard';
      case 2: return '/tenant/dashboard';
      default: 
        console.error('Invalid role for dashboard:', this.currentUser.role);
        return '/';
    }
  }
}