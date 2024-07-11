import {Component, Input, OnChanges, OnInit} from '@angular/core';
import * as moment from 'moment';
import {RosterService} from "../../roster.service";

@Component({
  selector: 'app-view-store',
  templateUrl: './view-store.component.html',
  styleUrls: ['./view-store.component.scss']
})
export class ViewStoreComponent implements OnInit, OnChanges {


  @Input("roster")roster: any = null;
  shiftsByTypeAndDate: any = {};
  shiftTypes: Set<string> = new Set();
  dates: string[] = [];
  startDate: string = '';
  endDate: string = '';

  constructor(private rosterService: RosterService) { }
  ngOnInit(): void {
    if(this.roster == null){
      this.rosterService.getStoreRequirement().subscribe(data =>{
        this.roster = data;
        this.startDate = this.roster.shiftAssignmentList[0].startDate;
        this.endDate = this.roster.shiftAssignmentList[0].endDate
        this.organizeShiftsByTypeAndDate(this.roster.shiftAssignmentList);
        this.dates = this.getDates();
      })
    }
  }

  ngOnChanges(changes: any) {
    this.roster = changes.roster.currentValue;
    console.log(this.roster)
      this.startDate = this.roster.shiftAssignmentList[0].startDate;
      this.endDate = this.roster.shiftAssignmentList[0].endDate
      this.organizeShiftsByTypeAndDate(this.roster.shiftAssignmentList);
      this.dates = this.getDates();
  }


  private organizeShiftsByTypeAndDate(shifts: any[]): void {
    this.shiftsByTypeAndDate = {};
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

    Object.keys(this.shiftsByTypeAndDate).forEach(type => {
      Object.keys(this.shiftsByTypeAndDate[type]).forEach(date => {
        this.shiftsByTypeAndDate[type][date].sort((a:any, b:any) => {
          return a.dateShift.startTime.localeCompare(b.dateShift.startTime);
        });
      });
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

  isSunday(date: string): boolean {
    const day = new Date(date).getDay();
    return day === 0; // 0 corresponds to Sunday
  }
}
