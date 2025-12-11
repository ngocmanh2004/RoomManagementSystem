export interface Invoice {
  id: number;
  contractId: number;
  contractCode: string;
  roomId: number;
  roomName: string;
  tenantName: string;
  month: string;
  roomRent: number;
  electricity: number;
  water: number;
  extraCost: number;
  totalAmount: number;
  status: 'UNPAID' | 'PAID' | 'OVERDUE';
  notes?: string;
  createdAt: Date;
  updatedAt: Date;
}

export interface InvoiceCreateRequest {
  contractId: number;
  month: string; // YYYY-MM
  electricity: number;
  water: number;
  extraCost: number;
  notes?: string;
}

export interface InvoiceResponse {
  id: number;
  contractId: number;
  contractCode: string;
  roomId: number;
  roomName: string;
  tenantName: string;
  month: string;
  roomRent: number;
  electricity: number;
  water: number;
  extraCost: number;
  totalAmount: number;
  status: 'UNPAID' | 'PAID' | 'OVERDUE';
  notes?: string;
}
