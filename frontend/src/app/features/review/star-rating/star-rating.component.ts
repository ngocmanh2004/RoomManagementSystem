import { Component, Input, Output, EventEmitter, OnInit, OnChanges, SimpleChanges } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-star-rating',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './star-rating.component.html',
  styleUrl: './star-rating.component.css'
})
export class StarRatingComponent implements OnInit, OnChanges {
  @Input() rating: number = 0;
  @Input() readOnly: boolean = false;
  @Input() size: 'small' | 'medium' | 'large' = 'medium';
  
  @Output() ratingChange = new EventEmitter<number>();

  stars: { id: number; filled: boolean }[] = [];
  hoveredRating = 0;

  ngOnInit() {
    this.initStars();
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes['rating']) {
      this.initStars();
    }
  }

  private initStars() {
    const numRating = parseInt(String(this.rating), 10) || 0;
    this.stars = [
      { id: 1, filled: numRating >= 1 },
      { id: 2, filled: numRating >= 2 },
      { id: 3, filled: numRating >= 3 },
      { id: 4, filled: numRating >= 4 },
      { id: 5, filled: numRating >= 5 }
    ];
  }

  onStarClick(starId: number) {
    if (this.readOnly) {
      return;
    }
    
    const newRating = parseInt(String(starId), 10);
    if (newRating < 1 || newRating > 5) {
      console.error('Invalid rating:', newRating);
      return;
    }

    this.rating = newRating;
    this.initStars();
    console.log('Star clicked:', newRating, 'Type:', typeof newRating);
    this.ratingChange.emit(newRating);
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
    return starId <= parseInt(String(this.rating), 10);
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