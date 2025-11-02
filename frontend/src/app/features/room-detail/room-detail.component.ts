import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';

@Component({
  selector: 'app-room-detail',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './room-detail.component.html',
  styleUrls: ['./room-detail.component.css']
})
export class RoomDetailComponent implements OnInit {
  roomId: string | null = null;

  // Biến để quản lý ảnh đang hiển thị
  currentMainImage: string = '';
  currentImageIndex: number = 0;

  room: any = {
    title: 'Nhà trọ 47 Nguyễn Nhạc',
    address: '47 Nguyễn Nhạc, Phường Nhơn Hưng, Thị xã An Nhơn, Tỉnh Gia Lai',
    price: 1500000,
    deposit: 1000000,
    status: 'Còn phòng',
    postDate: '11/10/2025',
    description: 'Phòng sạch sẽ, gần đường, có gác lửng, có sân phơi, cây xanh, ban công không gian thoáng mát... Đây là một nơi lý tưởng cho sinh viên và người đi làm.',
    features: [ 'Gác lửng tiện lợi', 'Sân phơi rộng rãi', 'Có wifi tốc độ cao miễn phí', 'An ninh 24/7 với camera giám sát' ],
    notes: [ 'Giữ gìn vệ sinh chung', 'Không gây ồn ào sau 22h', 'Tiền điện 3.500đ/kWh, nước 15.000đ/m³' ],
    rating: 4.5, reviews: 12, area: 20, capacity: 2,
    // THÊM ẢNH ĐỂ TEST SLIDESHOW
    images: [
      'https://cdn.thuviennhadat.vn/upload/tin-dang/hinh-anh/20250503081626/20250503081626638818569866969428.jpg',
      'https://pt123.cdn.static123.com/images/thumbs/900x600/fit/2023/03/10/z4170468185022-34da8500e91e7ab9fd73dcb1272b2b77_1678418703.jpg',
      'https://cdn.thuviennhadat.vn/Upload/images/post-image/2502280252038903-6753-926d1e9c9d40201e79519-5cfb3u-20241225202609.jpg',
      'https://pt123.cdn.static123.com/images/thumbs/900x600/fit/2023/03/10/z4170468194789-d908f09114423ed9f788731f28b94afc_1678418703.jpg',
      'https://pt123.cdn.static123.com/images/thumbs/450x300/fit/2025/09/20/phong-tro-1_1758360832.jpg'
    ]
  };

  constructor(private route: ActivatedRoute) { }

  ngOnInit(): void {
    this.roomId = this.route.snapshot.paramMap.get('id');
    // Khởi tạo ảnh chính là ảnh đầu tiên trong danh sách
    if (this.room.images && this.room.images.length > 0) {
      this.currentMainImage = this.room.images[0];
    }
  }

  // Hàm để thay đổi ảnh chính khi bấm vào thumbnail
  changeMainImage(image: string, index: number): void {
    this.currentMainImage = image;
    this.currentImageIndex = index;
  }

  // Hàm cho nút Next
  nextImage(): void {
    this.currentImageIndex++;
    if (this.currentImageIndex >= this.room.images.length) {
      this.currentImageIndex = 0; // Quay về ảnh đầu
    }
    this.currentMainImage = this.room.images[this.currentImageIndex];
  }

  // Hàm cho nút Previous
  prevImage(): void {
    this.currentImageIndex--;
    if (this.currentImageIndex < 0) {
      this.currentImageIndex = this.room.images.length - 1; // Tới ảnh cuối
    }
    this.currentMainImage = this.room.images[this.currentImageIndex];
  }
}