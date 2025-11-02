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
  @Input() room: any;

  get mainImage(): string {
    const raw =
      this.room?.mainImage ||
      this.room?.imageUrl ||
      this.room?.images?.[0]?.imageUrl || '';

    if (!raw) return 'assets/images/default-room.jpg';
    if (/^https?:\/\//i.test(raw)) return raw;
    
    if (raw.startsWith('/images/')) return raw;
    return `/images/${raw.replace(/^\/+/, '')}`;
  }
}
