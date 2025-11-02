import { Component, Input } from '@angular/core'; 
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router'; 

@Component({
  selector: 'app-room-card',
  standalone: true,
  imports: [CommonModule, RouterLink], 
  templateUrl: './room-card.component.html',
  styleUrls: ['./room-card.component.css']
})
export class RoomCardComponent {
  @Input() roomId: string | number = 0; 
}