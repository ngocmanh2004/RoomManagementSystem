import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RoomCardComponent } from '../../shared/components/room-card/room-card.component';

@Component({
  selector: 'app-rooms',
  standalone: true,
  imports: [CommonModule, RoomCardComponent], 
  templateUrl: './rooms.component.html',
  styleUrls: ['./rooms.component.css']
})
export class RoomsComponent {
  rooms = [1, 2, 3, 4, 5, 6, 7, 8];
}