import { UtilityStatus, UtilitySource } from './electricity.model';

export interface WaterRecord {
  id: number;
  roomId: number;
  name: string;
  fullName: string;
  oldIndex: number;
  newIndex: number;
  usage?: number;
  unitPrice: number;
  totalAmount: number;
  month: string;
  status: UtilityStatus;
}
