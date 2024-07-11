import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { EmployeeListComponent } from './employee-list/employee-list.component';
import {BrowserModule} from "@angular/platform-browser";



@NgModule({
  declarations: [
    EmployeeListComponent,

  ],
  imports: [
    CommonModule,
    BrowserModule,
  ]
})
export class EmployeeModule { }
