import { Component, OnInit } from '@angular/core';
import {RosterService} from "../../roster.service";
import {EmployeeService} from "../employee.service";

@Component({
  selector: 'app-employee-list',
  templateUrl: './employee-list.component.html',
  styleUrls: ['./employee-list.component.scss']
})
export class EmployeeListComponent implements OnInit {

  employee: any;
  expandedEmployeeId: number | null = null; // Track the expanded row

  constructor(private rosterService: RosterService, private employeeService: EmployeeService) {
    this.getAllEmployee();
  }

  ngOnInit(): void {
  }


  getAllEmployee(){
    this.employeeService.getAllEmployee().subscribe(data =>{
      this.employee = data;
    })
  }
  toggleRow(employeeId: number): void {
    this.expandedEmployeeId = this.expandedEmployeeId === employeeId ? null : employeeId;
  }

  groupShiftTimingsByDay(shiftTimings: any[]): { day: string, timings: any[] }[] {
    const daysOfWeek = ['Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday', 'Sunday'];

    // Group shift timings by day
    const grouped = shiftTimings.reduce((acc, timing) => {
      const dayName = daysOfWeek[timing.day - 1];
      if (!acc[dayName]) {
        acc[dayName] = [];
      }
      acc[dayName].push(timing);
      return acc;
    }, {} as { [key: string]: any[] });

    // Convert grouped object to array and sort by day
    return Object.keys(grouped).map(day => ({
      day: day,
      timings: grouped[day]
    })).sort((a, b) => daysOfWeek.indexOf(a.day) - daysOfWeek.indexOf(b.day));
  }
}
