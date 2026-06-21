import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { LandlordHeaderComponent } from '../landlord-header/landlord-header.component';
import { LandlordSidebarComponent } from '../landlord-sidebar/landlord-sidebar.component';

@Component({
  selector: 'app-landlord-layout',
  standalone: true,
  imports: [RouterOutlet, LandlordHeaderComponent, LandlordSidebarComponent],
  templateUrl: './landlord-layout.component.html',
  styleUrl: './landlord-layout.component.css'
})
export class LandlordLayoutComponent {
  isSidebarCollapsed = false;

  toggleSidebar() {
    this.isSidebarCollapsed = !this.isSidebarCollapsed;
  }
}
