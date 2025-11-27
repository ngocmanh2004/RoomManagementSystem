export type RoomStatus = 'AVAILABLE' | 'OCCUPIED' | 'REPAIRING';

export interface Building {
  id: number;
  name: string;
  address: string;
  description?: string;
}

export interface RoomImage {
  id: number;
  imageUrl: string;
  createdAt?: string;
}

export interface Room {
  id?: number;
  buildingId?: number;
  building?: Building;  // ← THÊM CÁI NÀY
  name: string;
  price: number;
  area: number;
  status: RoomStatus;
  description?: string;
  images?: RoomImage[];  // ← CẬP NHẬT TYPE
  tenantName?: string;
  amenities?: any[];
  createdAt?: string;
}