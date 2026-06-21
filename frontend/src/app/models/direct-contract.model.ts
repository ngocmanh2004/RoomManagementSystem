export interface DirectContractRequest {
  roomId: number;
  tenantId: number;
  fullName: string;
  cccd: string;
  phone: string;
  address: string;
  startDate: string; 
  endDate?: string; 
  deposit: number;
  notes?: string;
}