export interface Contract {
  id: number;
  contractCode: string; 
  contractCodeGenerated: string;
  roomName: string;  
  buildingName: string; 
  fullName: string;
  cccd: string;
  phone: string;
  address: string;
  landlordName: string;  
  landlordPhone: string;
  startDate: string;
  endDate: string;
  deposit: number;
  monthlyRent: number;
  monthlyRentCalculated: number;  
  totalInitialCost: number; 
  notes: string;
  rejectionReason?: string;
  status: string;  
  statusDisplayName: string;  
  durationMonths: number;  
  terms: string[];  
  createdAt: string;
  updatedAt: string;
}