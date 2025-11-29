import { Component, EventEmitter, Output, OnInit } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../../services/auth.service';
interface User {
  id: number;
  username: string;
  fullName: string;
  email: string;
  role: number;
  roleName: string;
}
@Component({
  selector: 'app-landlord-header',
  standalone: true,
  imports: [RouterModule, CommonModule],
  templateUrl: './landlord-header.component.html',
  styleUrl: './landlord-header.component.css'
})
export class LandlordHeaderComponent {
  @Output() toggleSidebar = new EventEmitter<void>();
  
  currentUser: User | null = null;
  showUserDropdown = false;

  constructor(
    private router: Router,
    private authService: AuthService
  ) {}

  ngOnInit() {
    this.loadCurrentUser();
  }

  loadCurrentUser() {
    const userStr = localStorage.getItem('currentUser');
    if (userStr) {
      this.currentUser = JSON.parse(userStr);
    }
  }

  onToggleSidebar() {
    this.toggleSidebar.emit();
  }

  toggleUserDropdown() {
    this.showUserDropdown = !this.showUserDropdown;
  }

  getRoleName(role: number): string {
    switch(role) {
      case 0: return 'Admin';
      case 1: return 'Chủ trọ';
      case 2: return 'Khách thuê';
      default: return 'User';
    }
  }

  goToHome() {
    this.router.navigate(['/']);
    this.showUserDropdown = false;
  }

  goToProfile() {
    this.router.navigate(['/profile']);
    this.showUserDropdown = false;
  }

  logout() {
  this.authService.logout().subscribe({
    next: () => {
      this.router.navigate(['/login']);
    },
    error: err => {
      console.error(err);
      this.router.navigate(['/login']);
    }
  });
}

  // Click outside to close dropdown
  onClickOutside(event: Event) {
    if (this.showUserDropdown) {
      this.showUserDropdown = false;
    }
  }
}
