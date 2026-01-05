export interface PageResponse<T> {
  content: T[];
  number: number;          // Current page (0-indexed)
  size: number;            // Items per page
  totalElements: number;   // Total items
  totalPages: number;      // Total pages
  last: boolean;           // Is last page
}
