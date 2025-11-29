import { Component, Input } from '@angular/core';
import { RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-landlord-sidebar',
  standalone: true,
  imports: [RouterModule, CommonModule],
  templateUrl: './landlord-sidebar.component.html',
  styleUrl: './landlord-sidebar.component.css'
})
export class LandlordSidebarComponent {
  @Input() isCollapsed = false;
}
