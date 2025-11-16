// src/app/models/province.model.ts
export interface Province {
  code: number;
  name: string;
  division_type?: string;
  hasRooms?: boolean;
}

export interface District {
  code: number;
  name: string;
  province_code: number;
  division_type?: string;
}

export interface ProvinceApiResponse {
  code: number;
  name: string;
  division_type: string;
  codename: string;
  phone_code: number;
  districts?: District[];
}