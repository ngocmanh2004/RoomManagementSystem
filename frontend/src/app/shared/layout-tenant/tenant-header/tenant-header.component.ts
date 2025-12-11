import { Component, Output, EventEmitter, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { AuthService } from '../../../services/auth.service';

@Component({
  selector: 'app-tenant-header',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './tenant-header.component.html',
  styleUrl: './tenant-header.component.css'
})
export class TenantHeaderComponent {
  @Output() toggleSidebar = new EventEmitter<void>();
  
  private authService = inject(AuthService);

  currentUser = this.authService.getCurrentUser();

  onToggleSidebar() {
    this.toggleSidebar.emit();
  }

  onLogout() {
    this.authService.logout();
  }
}
