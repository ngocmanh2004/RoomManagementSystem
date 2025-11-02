import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RoomCardComponent } from '../../shared/components/room-card/room-card.component';
import { StatsSectionComponent } from '../../shared/components/stats-section/stats-section.component';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [
    CommonModule,
    RoomCardComponent,
    StatsSectionComponent 
  ],
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent {
  // Dữ liệu mẫu, sau này sẽ lấy từ API
  rooms = [1, 2, 3, 4, 5]; 
}