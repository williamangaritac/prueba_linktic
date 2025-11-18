import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-pagination',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './pagination.component.html',
  styleUrls: ['./pagination.component.css']
})
export class PaginationComponent {
  @Input() totalItems: number = 0;
  @Input() itemsPerPage: number = 6;
  @Input() currentPage: number = 1;
  @Output() pageChange = new EventEmitter<number>();

  get totalPages(): number {
    return Math.ceil(this.totalItems / this.itemsPerPage);
  }

  get isPreviousDisabled(): boolean {
    return this.currentPage === 1;
  }

  get isNextDisabled(): boolean {
    return this.currentPage === this.totalPages;
  }

  /**
   * Get array of page numbers to display
   * Shows max 7 page buttons with ellipsis for large page counts
   */
  get pageNumbers(): (number | string)[] {
    const total = this.totalPages;
    const current = this.currentPage;
    const pages: (number | string)[] = [];

    if (total <= 7) {
      // Show all pages if 7 or less
      for (let i = 1; i <= total; i++) {
        pages.push(i);
      }
    } else {
      // Always show first page
      pages.push(1);

      if (current <= 3) {
        // Near the beginning
        pages.push(2, 3, 4, '...', total);
      } else if (current >= total - 2) {
        // Near the end
        pages.push('...', total - 3, total - 2, total - 1, total);
      } else {
        // In the middle
        pages.push('...', current - 1, current, current + 1, '...', total);
      }
    }

    return pages;
  }

  goToPage(page: number): void {
    if (page >= 1 && page <= this.totalPages && page !== this.currentPage) {
      this.pageChange.emit(page);
    }
  }

  previousPage(): void {
    if (!this.isPreviousDisabled) {
      this.goToPage(this.currentPage - 1);
    }
  }

  nextPage(): void {
    if (!this.isNextDisabled) {
      this.goToPage(this.currentPage + 1);
    }
  }

  isNumber(value: number | string): boolean {
    return typeof value === 'number';
  }
}

