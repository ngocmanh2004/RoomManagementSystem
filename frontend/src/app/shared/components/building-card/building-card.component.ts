import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { Building } from '../../../models/building.model';

@Component({
  selector: 'app-building-card',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './building-card.component.html',
  styleUrls: ['./building-card.component.css']
})
export class BuildingCardComponent {
  @Input() building!: Building;

  get mainImage(): string {
    // Lấy ảnh từ phòng đầu tiên của dãy trọ
    if (this.building.rooms && this.building.rooms.length > 0) {
      const firstRoom = this.building.rooms[0];
      if (firstRoom.images && firstRoom.images.length > 0) {
        return firstRoom.images[0].imageUrl;
      }
    }
    return 'https://via.placeholder.com/400x300?text=Dãy+Trọ';
  }
}
