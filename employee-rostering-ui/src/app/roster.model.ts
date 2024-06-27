export interface Employee {
  workerId?: number;
  name: string;
  position: string[];
  assignedStoreId: number;
  contactInfo: ContactInfo;
  scheduleModel?: any;
}

export interface ContactInfo{

  email: string;
  phone: string;

}
export interface Shift {
  id?: number;
  skills: string[];
  days: string[];
  startTime: string;
  endTime: string;
  employee_required: number;
}

export interface DateShift {
  id?: number;
  type: string;
  day: string;
  startTime: string;
  endTime: string;
  required: number;
  date: string;
}

export interface ShiftAssignment {
  id?: number;
  dateShift: DateShift;
  store_id: number;
  employee?: Employee;
}

export interface Roster {
  employeeList: Employee[];
  shiftList: Shift[];
  shiftAssignmentList?: ShiftAssignment[];
  score?: any;
}
