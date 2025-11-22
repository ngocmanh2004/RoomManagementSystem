import { User } from './users';

export interface LandlordRequest {
  id: number;
  user: User;
  cccd: string;
  address: string;
  expectedRoomCount: number;
  provinceCode?: number;
  districtCode?: number;
  frontImagePath: string;
  backImagePath: string;
  businessLicensePath: string;
  status: 'PENDING' | 'APPROVED' | 'REJECTED';
  rejectionReason?: string;
  createdAt: string;
  processedAt?: string;
}

export interface Landlord {
  id: number;
  user: User;
  cccd: string;
  address: string;
  expectedRoomCount: number;
  provinceCode?: number;
  districtCode?: number;
  frontImagePath: string;
  backImagePath: string;
  businessLicensePath: string;
  approved: 'APPROVED';
  utilityMode: 'LANDLORD_INPUT' | 'TENANT_SUBMIT';
  createdAt: string;
}

export interface RegistrationStatus {
  registered: boolean;
  status: string;
  request?: LandlordRequest;
}