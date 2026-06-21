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
    const baseUrl = 'http://localhost:8081';
    
    // Ưu tiên lấy ảnh chính của building
    if (this.building.imageUrl) {
      const imagePath = this.building.imageUrl.startsWith('/') 
        ? this.building.imageUrl 
        : '/images/' + this.building.imageUrl;
      return baseUrl + imagePath;
    }
    
    // Fallback: Lấy ảnh từ phòng đầu tiên
    if (this.building.rooms && this.building.rooms.length > 0) {
      const firstRoom = this.building.rooms[0];
      if (firstRoom.images && firstRoom.images.length > 0) {
        const imagePath = firstRoom.images[0].imageUrl.startsWith('/')
          ? firstRoom.images[0].imageUrl
          : '/images/' + firstRoom.images[0].imageUrl;
        return baseUrl + imagePath;
      }
    }
    
    return 'https://via.placeholder.com/400x300?text=Dãy+Trọ';
  }

  get priceRange(): string {
    if (this.building.minPrice !== undefined && this.building.maxPrice !== undefined) {
      const min = this.formatPrice(this.building.minPrice);
      const max = this.formatPrice(this.building.maxPrice);
      
      // Luôn hiển thị min - max
      if (this.building.minPrice === this.building.maxPrice) {
        return min;
      }
      return `${min} - ${max}`;
    }
    return 'Liên hệ';
  }

  formatPrice(price: number): string {
    if (price >= 1000000) {
      const millions = price / 1000000;
      // Làm tròn 1 chữ số thập phân
      return `${millions.toFixed(1).replace(/\.0$/, '')} triệu`;
    }
    return `${(price / 1000).toFixed(0)}k`;
  }
}
