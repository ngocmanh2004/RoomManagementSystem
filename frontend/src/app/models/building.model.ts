export interface Building {
  id: number;
  name: string;
  address: string;
  provinceCode?: number;
  districtCode?: number;
  description?: string;
  imageUrl?: string;
  landlord?: {
    id: number;
    fullName: string;
    phoneNumber: string;
    email?: string;
  };
  totalRooms?: number;
  availableRooms?: number;
  minPrice?: number;
  maxPrice?: number;
  rooms?: Array<{
    id: number;
    images?: Array<{
      imageUrl: string;
    }>;
  }>;
  createdAt?: string;
}
