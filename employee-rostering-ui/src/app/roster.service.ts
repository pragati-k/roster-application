import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {Roster} from "./roster.model";

@Injectable({
  providedIn: 'root'
})
export class RosterService {

  private apiUrl = 'http://localhost:8080/api/roster';

  constructor(private http: HttpClient) { }

  solveRoster(formData: FormData): Observable<string> {
    return this.http.post<string>(`${this.apiUrl}/solve`, formData);
  }

  getSolveRoster(problemId: string): Observable<Roster> {
    return this.http.get<Roster>(`${this.apiUrl}/solution/` + problemId);
  }

  getScoreExplanation(problemId: string): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/score-explanation/` + problemId);
  }

  terminateSolveRoster(problemId: string): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/terminate/` + problemId);
  }
}
