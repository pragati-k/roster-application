import { NgModule } from '@angular/core';
import {CommonModule, DatePipe} from '@angular/common';
import { ViewStoreComponent } from './view-store/view-store.component';
import {BrowserModule} from "@angular/platform-browser";
import { TimeFormatPipe } from './time-format.pipe';



@NgModule({
  declarations: [
    ViewStoreComponent,
    TimeFormatPipe
  ],
  exports: [
    ViewStoreComponent
  ],
  imports: [
    CommonModule,
    BrowserModule
  ],
  providers: [DatePipe]
})
export class StoreModule { }
