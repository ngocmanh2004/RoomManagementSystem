export enum ExtraCostStatus {
  PAID = 'PAID',
  UNPAID = 'UNPAID',
}

export enum CostType {
  INTERNET = 'INTERNET',
  GARBAGE = 'GARBAGE',
  MAINTENANCE = 'MAINTENANCE',
  OTHERS = 'OTHERS',
}

export interface ExtraCost {
  id: number;
  roomId: number;
  roomName?: string;
  tenantName?: string;
  code?: string;
  type: CostType;
  name?: string;
  description?: string;
  amount: number;
  month: string;
  status: ExtraCostStatus;
  createdAt?: string;
}
