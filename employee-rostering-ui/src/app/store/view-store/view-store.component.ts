import {Component, Input, OnInit} from '@angular/core';
import * as moment from 'moment';
import {RosterService} from "../../roster.service";
import {ShiftAssignment} from "../../roster.model";

@Component({
  selector: 'app-view-store',
  templateUrl: './view-store.component.html',
  styleUrls: ['./view-store.component.scss']
})
export class ViewStoreComponent implements OnInit {


  @Input("roster")roster: any = null;
  shiftsByTypeAndDate: any = {};
  shiftTypes: Set<string> = new Set();
  dates: string[] = [];
  startDate: string = '';
  endDate: string = '';
  problemID='';

  constructor(private rosterService: RosterService) { }

  ngOnInit(): void {
    console.log(this.roster)
        this.startDate = this.roster.shiftAssignmentList[0].startDate;
        this.endDate = this.roster.shiftAssignmentList[0].endDate
        this.organizeShiftsByTypeAndDate(this.roster.shiftAssignmentList);
        this.dates = this.getDates();
  }

  private organizeShiftsByTypeAndDate(shifts: any[]): void {
    shifts.forEach(shift => {
      const type = shift.dateShift.type;
      const date = shift.dateShift.date;
      this.shiftTypes.add(type);
      if (!this.shiftsByTypeAndDate[type]) {
        this.shiftsByTypeAndDate[type] = {};
      }
      if (!this.shiftsByTypeAndDate[type][date]) {
        this.shiftsByTypeAndDate[type][date] = [];
      }
      this.shiftsByTypeAndDate[type][date].push(shift);
    });
  }

  getDates(): string[] {
    const dates = [];
    let currentDate = moment(this.startDate);
    const endDate = moment(this.endDate);

    while (currentDate <= endDate) {
      dates.push(currentDate.format('YYYY-MM-DD'));
      currentDate = currentDate.add(1, 'days');
    }

    return dates;
  }
}
