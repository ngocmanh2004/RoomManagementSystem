import { Component, Input } from '@angular/core';
import { RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-tenant-sidebar',
  standalone: true,
  imports: [RouterModule, CommonModule],
  templateUrl: './tenant-sidebar.component.html',
  styleUrl: './tenant-sidebar.component.css'
})
export class TenantSidebarComponent {
  @Input() isCollapsed = false;
}
