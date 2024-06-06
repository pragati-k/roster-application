export interface Employee {
  id?: number;
  name: string;
}

export interface Shift {
  id?: number;
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
