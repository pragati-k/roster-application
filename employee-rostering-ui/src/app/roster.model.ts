export interface Employee {
  id?: number;
  name: string;
  skills: string[];
  availabilities?: Shift[];
}

export interface Shift {
  id?: number;
  skills: string[];
  days: string[];
  startTime: string;
  endTime: string;
}

export interface ShiftAssignment {
  id?: number;
  shift: Shift;
  employee?: Employee;
}

export interface Roster {
  employeeList: Employee[];
  shiftList: Shift[];
  shiftAssignmentList?: ShiftAssignment[];
  score?: any;
}
