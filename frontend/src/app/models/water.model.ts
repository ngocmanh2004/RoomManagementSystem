import { UtilityStatus, UtilitySource } from './electricity.model';

export interface WaterRecord {
  id: number;
  roomId: number;
  roomName?: string;
  tenantName?: string;
  oldIndex: number;
  newIndex: number;
  usage?: number;
  unitPrice: number;
  totalAmount: number;
  month: string;
  status: UtilityStatus;
}
