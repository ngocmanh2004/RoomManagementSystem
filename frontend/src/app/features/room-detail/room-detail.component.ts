import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';
import { RoomService } from '../../services/room.service';
import { AmenityService } from '../../services/amenity.service';
import { AuthService } from '../../services/auth.service';
import { ReviewListComponent } from '../review/review-list/review-list.component';
import { BookingModalComponent } from '../booking/components/booking-modal/booking-modal.component';
import { Room } from '../../models/room.model';
import { Amenity } from '../../models/amenity.model';

@Component({
  selector: 'app-room-detail',
  standalone: true,
  imports: [CommonModule, RouterModule, ReviewListComponent, BookingModalComponent],
  templateUrl: './room-detail.component.html',
  styleUrls: ['./room-detail.component.css'],
})
export class RoomDetailComponent implements OnInit {
  room: Room | null = null;
  roomId: number = 0;
  amenities: Amenity[] = [];
  currentImageIndex = 0;
  isLightboxOpen = false;
  lightboxImage: string = '';
  currentMainImage: string = '';
  mapsUrl: SafeResourceUrl | null = null;
  mapsEmbedUrl: string = '';
  isLoading = true;
  error = '';
  isBookingModalOpen = false;

  get isUserLoggedIn(): boolean {
    return this.authService.isLoggedIn();
  }

  get userRole(): number | null {
    return this.authService.getUserRole();
  }

  constructor(
    private route: ActivatedRoute,
    private roomService: RoomService,
    private amenityService: AmenityService,
    private sanitizer: DomSanitizer,
    private authService: AuthService
  ) {}

  ngOnInit() {
    this.route.params.subscribe((params) => {
      const id = +params['id'];
      
      if (!id || id <= 0) {
        this.error = 'Không tìm thấy phòng này';
        this.isLoading = false;
        return;
      }

      this.roomId = id;
      this.loadRoomDetail();
    });
  }

  loadRoomDetail() {
    if (!this.roomId || this.roomId <= 0) {
      this.error = 'Không tìm thấy phòng này';
      this.isLoading = false;
      return;
    }

    this.isLoading = true;
    this.error = '';

    this.roomService.getRoomById(this.roomId).subscribe({
      next: (data) => {
        this.room = data;

        if (this.room?.building?.address) {
          const address = encodeURIComponent(this.room.building.address);
          this.mapsEmbedUrl = `https://www.google.com/maps?q=${address}&output=embed`;
          this.mapsUrl = this.sanitizer.bypassSecurityTrustResourceUrl(this.mapsEmbedUrl);
        }

        if (this.room?.images && this.room.images.length > 0) {
          this.currentMainImage = this.normalizeImage(this.room.images[0].imageUrl);
        }

        this.loadAmenities();
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Error loading room:', err);
        this.error = 'Không thể tải thông tin phòng';
        this.isLoading = false;
      }
    });
  }

  loadAmenities() {
    if (!this.room?.id) return;
    this.amenityService.getAmenitiesByRoom(this.room.id).subscribe({
      next: (data) => {
        console.log('Amenities loaded:', data);
        this.amenities = data;
      },
      error: (err) => console.error('Error loading amenities:', err)
    });
  }

  openBookingModal() {
    const isLoggedIn = this.authService.isLoggedIn();
    const userRole = this.authService.getUserRole();
    
    console.log('🔐 openBookingModal - isLoggedIn:', isLoggedIn, 'userRole:', userRole);
    
    if (!isLoggedIn) {
      alert('Vui lòng đăng nhập để đặt thuê phòng');
      return;
    }
    
    if (userRole !== 2) {
      alert('Chỉ khách thuê có thể đặt thuê phòng');
      return;
    }
    
    if (!this.room || this.room.status !== 'AVAILABLE') {
      alert('Phòng này hiện không khả dụng');
      return;
    }
    
    this.isBookingModalOpen = true;
  }

  closeBookingModal() {
    this.isBookingModalOpen = false;
  }

  onBookingSuccess() {
    alert('Yêu cầu đặt thuê phòng đã được gửi thành công!');
    this.isBookingModalOpen = false;
    this.loadRoomDetail();
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

    // Backend trả về: "/images/1/1/detail1.png" hoặc "1/1/detail1.png"
    // Cần build thành: "http://localhost:8081/images/1/1/detail1.png"
    const imagePath = url.startsWith('/') ? url : '/images/' + url;
    return `http://localhost:8081${imagePath}`;
  }

  prevImage(event: Event) {
    event.stopPropagation();
    if (!this.room?.images || this.room.images.length === 0) return;
    this.currentImageIndex = (this.currentImageIndex - 1 + this.room.images.length) % this.room.images.length;
    this.currentMainImage = this.normalizeImage(this.room.images[this.currentImageIndex].imageUrl);
  }

  nextImage(event: Event) {
    event.stopPropagation();
    if (!this.room?.images || this.room.images.length === 0) return;
    this.currentImageIndex = (this.currentImageIndex + 1) % this.room.images.length;
    this.currentMainImage = this.normalizeImage(this.room.images[this.currentImageIndex].imageUrl);
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
    this.currentImageIndex = (this.currentImageIndex - 1 + this.room.images.length) % this.room.images.length;
    this.lightboxImage = this.normalizeImage(this.room.images[this.currentImageIndex].imageUrl);
  }

  nextLightboxImage(event: Event) {
    event.stopPropagation();
    if (!this.room?.images || this.room.images.length === 0) return;
    this.currentImageIndex = (this.currentImageIndex + 1) % this.room.images.length;
    this.lightboxImage = this.normalizeImage(this.room.images[this.currentImageIndex].imageUrl);
  }
}