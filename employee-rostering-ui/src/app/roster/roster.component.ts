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
  startDate = "";
  endDate: string = "";
  constructor(private http: HttpClient, private rosterService: RosterService) {
  }

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
    if (this.shiftFile) {
      const formData: FormData = new FormData();
      formData.append('stores', this.shiftFile, this.shiftFile.name);
      formData.append('startDate', this.startDate);
      formData.append('endDate', this.endDate);

      this.rosterService.solveRoster(formData).subscribe(response => {
        this.problemId = response;
        localStorage.setItem("problemId", this.problemId);
        console.log('Files uploaded successfully');
      }, error => {
        console.error('Error uploading files', error);
      });
    } else {
      alert('Please select store file.');
    }
  }

  getSolveRoster(){
    let problemId: any = localStorage.getItem("problemId");
      this.rosterService.getSolveRoster(problemId).subscribe(roster =>{
        this.roster = roster;
      })
    this.getScoreExplation();
    }

    terminateSolveRoster(){
      let problemId: any = localStorage.getItem("problemId");
      this.rosterService.terminateSolveRoster(problemId).subscribe(roster =>{
        this.message = roster.message;
      })
    }

    getScoreExplation(){
      let problemId: any = localStorage.getItem("problemId");
      this.rosterService.getScoreExplanation(problemId).subscribe(response =>{
        console.log(response);
        this.scoreExplanation = response;
      })
    }
}
