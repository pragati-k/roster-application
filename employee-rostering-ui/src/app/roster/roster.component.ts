import { Component, OnInit } from '@angular/core';
import {Employee, Roster, Shift, ShiftAssignment} from "../roster.model";
import {RosterService} from "../roster.service";

@Component({
  selector: 'app-roster',
  templateUrl: './roster.component.html',
  styleUrls: ['./roster.component.scss']
})
export class RosterComponent {

  roster: Roster = { employeeList: [], shiftList: [], shiftAssignmentList:[] };
  newEmployee: Employee = { name: '' , preferredShifts: []};
  newPreferredShift: Shift = { startTime: '', endTime: ''};
  newShift: Shift = { startTime: '', endTime: ''};
  problemId = '';
  message = '';
  count = 0;

  constructor(private rosteringService: RosterService) {}

  addEmployee() {
    this.roster.employeeList.push({ ...this.newEmployee });
    this.newEmployee = { name: '' , preferredShifts: []};
  }

  addPreferredShift(employee: Employee) {
    employee.preferredShifts?.push({ ...this.newPreferredShift });
    this.newPreferredShift = { startTime: '', endTime: '' };
  }

  addShift() {
    let newShiftAssignment = {
      id:0,
      shift: {startTime: '', endTime: ''}
    }
    this.roster.shiftList.push({ ...this.newShift });
    newShiftAssignment.shift = this.newShift;
    this.newShift = { startTime: '', endTime:''};
    newShiftAssignment.id = this.count;
    this.roster.shiftAssignmentList?.push(newShiftAssignment);
    this.count++;
  }

  solveRoster(){
    console.log(this.roster);
    this.rosteringService.solveRoster(this.roster).subscribe(solvedRoster => {
        this.problemId = solvedRoster;
    });
  }

  getSolveRoster(){
    this.rosteringService.getSolveRoster(this.problemId).subscribe(roster =>{
      this.roster = roster;
    })
  }

  terminateSolveRoster(){
    this.rosteringService.terminateSolveRoster(this.problemId).subscribe(roster =>{
      console.log(roster);
      this.message = roster.message;
    })
  }
}
