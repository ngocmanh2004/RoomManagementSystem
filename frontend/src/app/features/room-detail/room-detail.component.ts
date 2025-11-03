import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { RoomService } from '../../services/room.service';

@Component({
  selector: 'app-room-detail',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './room-detail.component.html',
  styleUrls: ['./room-detail.component.css'],
})
export class RoomDetailComponent implements OnInit {
  roomId: string | null = null;
  room: any;
  amenities: any[] = [];
  currentMainImage = '';
  currentImageIndex = 0;
  isLightboxOpen = false;
  lightboxImage = '';

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
        this.loadAmenities(+this.roomId!);
      });
    }
  }

  loadAmenities(roomId: number): void {
    this.roomService.getAmenitiesByRoomId(roomId).subscribe({
      next: (data) => (this.amenities = data || []),
      error: (err) => console.error('Lỗi tải tiện nghi:', err),
    });
  }

  normalizeImage(raw: string): string {
    if (!raw) return 'assets/images/default-room.jpg';
    if (/^https?:\/\//i.test(raw)) return raw;
    if (raw.startsWith('/images/')) return raw;
    return `/images/${raw.replace(/^\/+/, '')}`;
  }

  /** ====== ẢNH NGOÀI LIGHTBOX ====== */
  changeMainImage(image: string, index: number): void {
    this.currentMainImage = this.normalizeImage(image);
    this.currentImageIndex = index;
  }

  nextImage(event?: Event): void {
    event?.stopPropagation();
    const arr = this.room?.images || [];
    if (!arr.length) return;
    this.currentImageIndex = (this.currentImageIndex + 1) % arr.length;
    this.currentMainImage = this.normalizeImage(
      arr[this.currentImageIndex].imageUrl
    );
  }

  prevImage(event?: Event): void {
    event?.stopPropagation();
    const arr = this.room?.images || [];
    if (!arr.length) return;
    this.currentImageIndex =
      (this.currentImageIndex - 1 + arr.length) % arr.length;
    this.currentMainImage = this.normalizeImage(
      arr[this.currentImageIndex].imageUrl
    );
  }

  /** ====== LIGHTBOX ====== */
  openLightbox(index: number): void {
    const arr = this.room?.images || [];
    if (!arr.length) return;
    this.currentImageIndex = index;
    this.lightboxImage = this.normalizeImage(arr[index].imageUrl);
    this.isLightboxOpen = true;
  }

  prevLightboxImage(event: Event): void {
    event.stopPropagation();
    const arr = this.room?.images || [];
    if (!arr.length) return;
    this.currentImageIndex = (this.currentImageIndex - 1 + arr.length) % arr.length;
    this.lightboxImage = this.normalizeImage(arr[this.currentImageIndex].imageUrl);
  }

  nextLightboxImage(event: Event): void {
    event.stopPropagation();
    const arr = this.room?.images || [];
    if (!arr.length) return;
    this.currentImageIndex = (this.currentImageIndex + 1) % arr.length;
    this.lightboxImage = this.normalizeImage(arr[this.currentImageIndex].imageUrl);
  }

  closeLightbox(event: MouseEvent): void {
    if ((event.target as HTMLElement).classList.contains('lightbox')) {
      this.isLightboxOpen = false;
    }
  }
}
