import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {RosterComponent} from "./roster/roster.component";
import {EmployeeListComponent} from "./employee/employee-list/employee-list.component";
import {ViewStoreComponent} from "./store/view-store/view-store.component";

const routes: Routes = [{
  path: '',
  component: RosterComponent
},
  {
    path:'employee-list',
    component: EmployeeListComponent
  },
  {
    path:'view-store',
    component: ViewStoreComponent
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
