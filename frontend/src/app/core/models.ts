export interface BuyLink {
  storeName: string;
  url: string;
}

export interface Supplement {
  id: number;
  name: string;
  description: string;
  typicalDosage: string;
  category: string;
  isCustom: boolean;
  benefits: string[];
  buyLinks: BuyLink[];
  tracked: boolean;
}

export interface CreateSupplementRequest {
  name: string;
  description: string;
  typicalDosage: string;
  category: string;
  benefits: string[];
  buyLinks: BuyLink[];
}

export interface UserSupplement {
  id: number;
  supplementId: number;
  name: string;
  typicalDosage: string;
  addedDate: string;
  active: boolean;
}

export interface IntakeLog {
  id: number;
  userSupplementId: number;
  supplementId: number;
  supplementName: string;
  takenAt: string;
  date: string;
}

export interface DayLogs {
  date: string;
  logs: IntakeLog[];
}
