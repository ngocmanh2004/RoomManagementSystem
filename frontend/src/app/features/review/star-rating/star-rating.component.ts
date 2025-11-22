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
  @Input() rating: number = 0;
  @Input() readOnly: boolean = false;
  @Input() size: 'small' | 'medium' | 'large' = 'medium';
  
  @Output() ratingChange = new EventEmitter<number>();

  stars: { id: number; filled: boolean }[] = [];
  hoveredRating = 0;

  ngOnInit() {
    this.initStars();
  }

  private initStars() {
    this.stars = [
      { id: 1, filled: this.rating >= 1 },
      { id: 2, filled: this.rating >= 2 },
      { id: 3, filled: this.rating >= 3 },
      { id: 4, filled: this.rating >= 4 },
      { id: 5, filled: this.rating >= 5 }
    ];
  }

  onStarClick(starId: number) {
    if (this.readOnly) {
      return;
    }
    
    this.rating = starId;
    this.initStars();
    this.ratingChange.emit(this.rating);
  }

  onStarHover(starId: number) {
    if (this.readOnly) {
      return;
    }
    this.hoveredRating = starId;
  }

  onStarLeave() {
    this.hoveredRating = 0;
  }

  isStarFilled(starId: number): boolean {
    if (this.hoveredRating > 0) {
      return starId <= this.hoveredRating;
    }
    return starId <= this.rating;
  }

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