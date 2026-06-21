export interface Feedback {
  id: number;
  title: string;
  content: string;
  attachmentUrl?: string;

  status:
    | 'PENDING'
    | 'PROCESSING'
    | 'RESOLVED'
    | 'TENANT_CONFIRMED'
    | 'TENANT_REJECTED'
    | 'CANCELED';

  room: {
    id: number;
    name?: string;
  };

  createdAt: string;
  updatedAt?: string; 
  resolvedAt?: string;

  landlordNote?: string;
  tenantFeedback?: string;
  tenantSatisfied?: boolean;
}
