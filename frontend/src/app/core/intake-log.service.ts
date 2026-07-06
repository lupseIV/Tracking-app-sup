import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import { DayLogs, IntakeLog } from './models';

@Injectable({ providedIn: 'root' })
export class IntakeLogService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = '/api/intake-logs';

  log(userSupplementId: number): Observable<IntakeLog> {
    return this.http.post<IntakeLog>(this.baseUrl, { userSupplementId });
  }

  history(days: number): Observable<DayLogs[]> {
    return this.http.get<DayLogs[]>(this.baseUrl, {
      params: new HttpParams().set('days', days),
    });
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}
