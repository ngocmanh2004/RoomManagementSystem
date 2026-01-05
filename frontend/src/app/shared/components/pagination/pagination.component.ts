import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-pagination',
  standalone: true,
  imports: [CommonModule],
  template: `
    <nav *ngIf="totalPages > 1" class="pagination-container" aria-label="Page navigation">
      <ul class="pagination">
        <!-- Previous Button -->
        <li class="page-item" [class.disabled]="currentPage === 0">
          <button 
            class="page-link" 
            (click)="onPageChange(currentPage - 1)"
            [disabled]="currentPage === 0"
            aria-label="Previous">
            <span aria-hidden="true">&laquo;</span>
          </button>
        </li>

        <!-- Page Numbers -->
        <li 
          *ngFor="let page of getVisiblePages()" 
          class="page-item"
          [class.active]="page === currentPage">
          <button 
            class="page-link" 
            (click)="onPageChange(page)">
            {{ page + 1 }}
          </button>
        </li>

        <!-- Next Button -->
        <li class="page-item" [class.disabled]="currentPage === totalPages - 1">
          <button 
            class="page-link" 
            (click)="onPageChange(currentPage + 1)"
            [disabled]="currentPage === totalPages - 1"
            aria-label="Next">
            <span aria-hidden="true">&raquo;</span>
          </button>
        </li>
      </ul>
    </nav>
  `,
  styles: [`
    .pagination-container {
      display: flex;
      justify-content: center;
      margin: 2rem 0;
    }

    .pagination {
      display: flex;
      list-style: none;
      padding: 0;
      margin: 0;
      gap: 0.5rem;
    }

    .page-item {
      display: inline-block;
    }

    .page-link {
      padding: 0.5rem 0.75rem;
      border: 1px solid #dee2e6;
      background-color: #fff;
      color: #007bff;
      cursor: pointer;
      border-radius: 0.25rem;
      transition: all 0.2s;
      font-size: 0.875rem;
    }

    .page-link:hover:not(:disabled) {
      background-color: #e9ecef;
      border-color: #dee2e6;
    }

    .page-item.active .page-link {
      background-color: #007bff;
      border-color: #007bff;
      color: #fff;
      cursor: default;
    }

    .page-item.disabled .page-link {
      color: #6c757d;
      background-color: #fff;
      border-color: #dee2e6;
      cursor: not-allowed;
      opacity: 0.5;
    }
  `]
})
export class PaginationComponent {
  @Input() currentPage: number = 0;
  @Input() totalPages: number = 0;
  @Input() maxVisiblePages: number = 5;
  @Output() pageChange = new EventEmitter<number>();

  onPageChange(page: number): void {
    if (page >= 0 && page < this.totalPages && page !== this.currentPage) {
      this.pageChange.emit(page);
    }
  }

  getVisiblePages(): number[] {
    const pages: number[] = [];
    const half = Math.floor(this.maxVisiblePages / 2);
    
    let start = Math.max(0, this.currentPage - half);
    let end = Math.min(this.totalPages - 1, start + this.maxVisiblePages - 1);
    
    if (end - start < this.maxVisiblePages - 1) {
      start = Math.max(0, end - this.maxVisiblePages + 1);
    }
    
    for (let i = start; i <= end; i++) {
      pages.push(i);
    }
    
    return pages;
  }
}
