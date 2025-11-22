import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';
import { RoomService } from '../../services/room.service';
import { AmenityService } from '../../services/amenity.service';
import { ReviewListComponent } from '../review/review-list/review-list.component';
import { Room } from '../../models/room.model';
import { Amenity } from '../../models/amenity.model';

@Component({
  selector: 'app-room-detail',
  standalone: true,
  imports: [CommonModule, RouterModule, ReviewListComponent],
  templateUrl: './room-detail.component.html',
  styleUrls: ['./room-detail.component.css'],
})
export class RoomDetailComponent implements OnInit {
  room: Room | null = null;
  roomId!: number;
  amenities: Amenity[] = [];
  currentImageIndex = 0;
  isLightboxOpen = false;
  lightboxImage: string = '';
  currentMainImage: string = '';
  mapsUrl: SafeResourceUrl | null = null;

  constructor(
    private route: ActivatedRoute,
    private roomService: RoomService,
    private amenityService: AmenityService,
    private sanitizer: DomSanitizer
  ) {}

  ngOnInit() {
    this.route.params.subscribe((params) => {
      this.roomId = +params['id'];
      this.loadRoomDetail();
    });
  }

  loadRoomDetail() {
    this.roomService.getRoomById(this.roomId).subscribe({
      next: (data) => {
        this.room = data;

        if (this.room?.building?.address) {
          const mapsUrl = `https://maps.google.com/maps?q=${encodeURIComponent(
            this.room.building.address
          )}&output=embed`;
          this.mapsUrl = this.sanitizer.bypassSecurityTrustResourceUrl(mapsUrl);
        }

        if (this.room?.images && this.room.images.length > 0) {
          this.currentMainImage = this.normalizeImage(
            this.room.images[0].imageUrl
          );
        }
        this.loadAmenities();
      },
      error: (err) => console.error('Lỗi tải phòng:', err),
    });
  }

  loadAmenities() {
    if (!this.room?.id) return;
    this.amenityService.getAmenitiesByRoom(this.room.id).subscribe({
      next: (data) => (this.amenities = data),
      error: (err) => console.error('Lỗi tải tiện nghi:', err),
    });
  }

  changeMainImage(imageUrl: string, index: number) {
    this.currentMainImage = this.normalizeImage(imageUrl);
    this.currentImageIndex = index;
  }

  normalizeImage(url: string): string {
    if (!url) return '/assets/images/no-image.jpg';

    if (url.startsWith('http://') || url.startsWith('https://')) {
      return url;
    }

    return `http://localhost:8081${url}`;
  }

  prevImage(event: Event) {
    event.stopPropagation();
    if (!this.room?.images || this.room.images.length === 0) return;
    this.currentImageIndex =
      (this.currentImageIndex - 1 + this.room.images.length) %
      this.room.images.length;
    this.currentMainImage = this.normalizeImage(
      this.room.images[this.currentImageIndex].imageUrl
    );
  }

  nextImage(event: Event) {
    event.stopPropagation();
    if (!this.room?.images || this.room.images.length === 0) return;
    this.currentImageIndex =
      (this.currentImageIndex + 1) % this.room.images.length;
    this.currentMainImage = this.normalizeImage(
      this.room.images[this.currentImageIndex].imageUrl
    );
  }

  openLightbox(index: number) {
    if (!this.room?.images || this.room.images.length === 0) return;
    this.isLightboxOpen = true;
    this.lightboxImage = this.normalizeImage(this.room.images[index].imageUrl);
  }

  closeLightbox(event: Event) {
    if (event.target === event.currentTarget) {
      this.isLightboxOpen = false;
    }
  }

  prevLightboxImage(event: Event) {
    event.stopPropagation();
    if (!this.room?.images || this.room.images.length === 0) return;
    this.currentImageIndex =
      (this.currentImageIndex - 1 + this.room.images.length) %
      this.room.images.length;
    this.lightboxImage = this.normalizeImage(
      this.room.images[this.currentImageIndex].imageUrl
    );
  }

  nextLightboxImage(event: Event) {
    event.stopPropagation();
    if (!this.room?.images || this.room.images.length === 0) return;
    this.currentImageIndex =
      (this.currentImageIndex + 1) % this.room.images.length;
    this.lightboxImage = this.normalizeImage(
      this.room.images[this.currentImageIndex].imageUrl
    );
  }
}