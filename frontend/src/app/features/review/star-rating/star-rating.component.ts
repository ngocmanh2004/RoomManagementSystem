import { Component, Input, Output, EventEmitter, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-star-rating',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './star-rating.component.html',
  styleUrl: './star-rating.component.css'
})
export class StarRatingComponent implements OnInit {
  @Input() rating: number = 0;              // ✅ Rating hiện tại (1-5)
  @Input() readOnly: boolean = false;       // ✅ Chỉ xem, không cho chỉnh
  @Input() size: 'small' | 'medium' | 'large' = 'medium';  // Size icon
  
  @Output() ratingChange = new EventEmitter<number>();  // Emit khi change

  stars: { id: number; filled: boolean }[] = [];
  hoveredRating = 0;

  ngOnInit() {
    this.initStars();
  }

  /**
   * ✅ Khởi tạo 5 sao
   */
  private initStars() {
    this.stars = [
      { id: 1, filled: this.rating >= 1 },
      { id: 2, filled: this.rating >= 2 },
      { id: 3, filled: this.rating >= 3 },
      { id: 4, filled: this.rating >= 4 },
      { id: 5, filled: this.rating >= 5 }
    ];
  }

  /**
   * ✅ Click vào sao
   */
  onStarClick(starId: number) {
    if (this.readOnly) {
      return; // Không cho edit nếu readOnly
    }
    
    console.log('⭐ StarRating: Star clicked:', starId);
    this.rating = starId;
    this.initStars();
    this.ratingChange.emit(this.rating);
  }

  /**
   * ✅ Hover vào sao
   */
  onStarHover(starId: number) {
    if (this.readOnly) {
      return;
    }
    this.hoveredRating = starId;
  }

  /**
   * ✅ Leave hover
   */
  onStarLeave() {
    this.hoveredRating = 0;
  }

  /**
   * ✅ Check sao có được highlight không
   */
  isStarFilled(starId: number): boolean {
    if (this.hoveredRating > 0) {
      return starId <= this.hoveredRating;
    }
    return starId <= this.rating;
  }

  /**
   * ✅ Get CSS class cho sao
   */
  getStarClass(starId: number): string {
    let classes = `star star-${this.size}`;
    
    if (this.isStarFilled(starId)) {
      classes += ' filled';
    }
    
    if (!this.readOnly) {
      classes += ' interactive';
    }
    
    return classes;
  }
}