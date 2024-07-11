import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";

@Injectable({
  providedIn: 'root'
})
export class EmployeeService {

  private apiUrl = 'http://localhost:8080/employee';

  constructor(private http: HttpClient) { }


  getAllEmployee(){
    return this.http.get(`${this.apiUrl}/allEmployee`);
  }
}
