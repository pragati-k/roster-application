import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ViewStoreComponent } from './view-store/view-store.component';
import {BrowserModule} from "@angular/platform-browser";



@NgModule({
  declarations: [
    ViewStoreComponent
  ],
  exports: [
    ViewStoreComponent
  ],
  imports: [
    CommonModule,
    BrowserModule
  ]
})
export class StoreModule { }
