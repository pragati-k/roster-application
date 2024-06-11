import { Component, OnInit } from '@angular/core';
import {Employee, Roster, Shift, ShiftAssignment} from "../roster.model";
import {RosterService} from "../roster.service";
import {HttpClient} from "@angular/common/http";

@Component({
  selector: 'app-roster',
  templateUrl: './roster.component.html',
  styleUrls: ['./roster.component.scss']
})
export class RosterComponent {

  roster: Roster = { employeeList: [], shiftList: [], shiftAssignmentList:[] };
  private employeeFile: File | null = null;
  private shiftFile: File | null = null;
  problemId = '';
  message = '';
  scoreExplanation:any;

  constructor(private http: HttpClient, private rosterService: RosterService) {}

  onFileSelected(event: Event, fileType: string): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      if (fileType === 'employees') {
        this.employeeFile = input.files[0];
      } else if (fileType === 'shifts') {
        this.shiftFile = input.files[0];
      }
    }
  }

  onSubmit(): void {
    if (this.employeeFile && this.shiftFile) {
      const formData: FormData = new FormData();
      formData.append('employees', this.employeeFile, this.employeeFile.name);
      formData.append('stores', this.shiftFile, this.shiftFile.name);

      this.rosterService.solveRoster(formData).subscribe(response => {
        this.problemId = response;
        console.log('Files uploaded successfully');
      }, error => {
        console.error('Error uploading files', error);
      });
    } else {
      alert('Please select both employee and shift files.');
    }
  }

  getSolveRoster(){
      this.rosterService.getSolveRoster(this.problemId).subscribe(roster =>{
        this.roster = roster;
      })
    this.getScoreExplation();
    }

    terminateSolveRoster(){
      this.rosterService.terminateSolveRoster(this.problemId).subscribe(roster =>{
        console.log(roster);
        this.message = roster.message;
      })
    }

    getScoreExplation(){
      this.rosterService.getScoreExplanation(this.problemId).subscribe(response =>{
        console.log(response);
        this.scoreExplanation = response;
      })
    }
}
