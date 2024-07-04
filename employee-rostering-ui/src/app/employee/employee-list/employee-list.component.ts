import { Component, OnInit } from '@angular/core';
import {RosterService} from "../../roster.service";
import {ContactInfo, Employee, Roster} from "../../roster.model";

@Component({
  selector: 'app-employee-list',
  templateUrl: './employee-list.component.html',
  styleUrls: ['./employee-list.component.scss']
})
export class EmployeeListComponent implements OnInit {

  employee: any;
  constructor(private rosterService: RosterService) {
    this.getSolveRoster();
  }

  ngOnInit(): void {
  }

  getSolveRoster(){
    this.rosterService.getEmployeeList().subscribe(roster =>{
      this.employee = roster;
    })
  }
}
