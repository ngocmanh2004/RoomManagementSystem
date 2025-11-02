import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { RoomService } from '../../services/room.service';

@Component({
  selector: 'app-room-detail',
  standalone: true,
  imports: [CommonModule], 
  templateUrl: './room-detail.component.html',
  styleUrls: ['./room-detail.component.css']
})

export class RoomDetailComponent implements OnInit {
  roomId: string | null = null;
  room: any;
  currentMainImage = '';
  currentImageIndex = 0;

  constructor(private route: ActivatedRoute, private roomService: RoomService) {}

  ngOnInit(): void {
    this.roomId = this.route.snapshot.paramMap.get('id');
    if (this.roomId) {
      this.roomService.getRoomById(+this.roomId).subscribe((data) => {
        this.room = data;
        const first =
          this.room?.imageUrl ||
          this.room?.mainImage ||
          this.room?.images?.[0]?.imageUrl ||
          '';
        this.currentMainImage = this.normalizeImage(first);
      });
    }
  }

  normalizeImage(raw: string): string {
    if (!raw) return 'assets/images/default-room.jpg';
    if (/^https?:\/\//i.test(raw)) return raw;
    if (raw.startsWith('/images/')) return raw;
    return `/images/${raw.replace(/^\/+/, '')}`;
  }

  changeMainImage(image: string, index: number): void {
    this.currentMainImage = this.normalizeImage(image);
    this.currentImageIndex = index;
  }

  nextImage(): void {
    const arr = this.room?.images || [];
    if (!arr.length) return;
    this.currentImageIndex = (this.currentImageIndex + 1) % arr.length;
    this.currentMainImage = this.normalizeImage(arr[this.currentImageIndex].imageUrl);
  }

  prevImage(): void {
    const arr = this.room?.images || [];
    if (!arr.length) return;
    this.currentImageIndex =
      (this.currentImageIndex - 1 + arr.length) % arr.length;
    this.currentMainImage = this.normalizeImage(arr[this.currentImageIndex].imageUrl);
  }
}
