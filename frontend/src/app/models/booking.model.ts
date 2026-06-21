export interface BookingRequest {
  roomId: number;
  startDate: string;
  endDate?: string;
  deposit: number;
  notes?: string;
  fullName: string;
  cccd: string;
  phone: string;
  address: string;
}

export interface BookingContract {
  id: number;
  room: {
    id: number;
    name: string;
    price: number;
  };
  tenant: {
    id: number;
    fullName: string;
  };
  startDate: string;
  endDate?: string;
  deposit: number;
  notes: string;
  rejectionReason?: string;
  status:
    | 'PENDING'
    | 'APPROVED'
    | 'REJECTED'
    | 'ACTIVE'
    | 'EXPIRED'
    | 'CANCELLED';
  createdAt: string;
  updatedAt: string;
}

export interface ContractResponse {
  content: BookingContract[];
  totalPages: number;
  totalElements: number;
}
