export enum UtilityStatus {
  PAID = 'PAID',
  UNPAID = 'UNPAID',
}

export enum UtilitySource {
  LANDLORD = 'LANDLORD',
  TENANT = 'TENANT',
  SYSTEM = 'SYSTEM',
}

export interface ElectricRecord {
  id: number;
  roomId: number;
  tenantName?: string;
  oldIndex: number;
  newIndex: number;
  unitPrice: number;
  totalAmount: number;
  meterPhotoUrl?: string;
  month: string;
  status: UtilityStatus;
  source: UtilitySource;
}
export interface FilterState {
  month: string;
  status: UtilityStatus | 'ALL';
  search: string;
}
