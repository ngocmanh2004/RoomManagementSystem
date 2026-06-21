import { User } from './users'; 

export interface Tenant {
  id: number;
  cccd?: string; 
  dateOfBirth?: string;
  provinceCode?: number;
  districtCode?: number;
  address?: string;
  user: User;
}