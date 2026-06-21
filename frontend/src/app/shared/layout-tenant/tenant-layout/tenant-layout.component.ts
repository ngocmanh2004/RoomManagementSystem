import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { TenantHeaderComponent } from '../tenant-header/tenant-header.component';
import { TenantSidebarComponent } from '../tenant-sidebar/tenant-sidebar.component';

@Component({
  selector: 'app-tenant-layout',
  standalone: true,
  imports: [RouterOutlet, TenantHeaderComponent, TenantSidebarComponent],
  templateUrl: './tenant-layout.component.html',
  styleUrl: './tenant-layout.component.css'
})
export class TenantLayoutComponent {
  isSidebarCollapsed = false;

  toggleSidebar() {
    this.isSidebarCollapsed = !this.isSidebarCollapsed;
  }
}
