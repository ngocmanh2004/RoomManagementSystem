export interface ChatMessage {
  text: string;
  sender: 'user' | 'bot';
  timestamp: Date;
  roomLinks?: RoomLink[];
}

export interface RoomLink {
  roomId: number;
  roomName: string;
  price: number;
  buildingName: string;
}
