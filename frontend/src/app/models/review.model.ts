export interface Review {
  id: number;
  roomId: number;
  tenantId: number;
  tenantName: string;
  rating: number;
  comment: string;
  createdAt: string;
  updatedAt: string;
  canEdit: boolean;
  canDelete: boolean;
}

export interface ReviewRequest {
  roomId: number;
  rating: number;
  comment?: string;
}

export interface ReviewResponse {
  content: Review[];
  totalElements: number;
  totalPages: number;
  currentPage: number;
  size: number;
}

export interface ApiResponse<T> {
  status: string;     
  message: string;
  data?: T;
}

export interface ErrorResponse {
  message: string;
  status?: string;
}