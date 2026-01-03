export interface Building {
  id: number;
  name: string;
  address: string;
  description?: string;
  landlord?: {
    id: number;
    fullName: string;
    phoneNumber: string;
    email?: string;
  };
  totalRooms?: number;
  availableRooms?: number;
  rooms?: Array<{
    id: number;
    images?: Array<{
      imageUrl: string;
    }>;
  }>;
  createdAt?: string;
}
