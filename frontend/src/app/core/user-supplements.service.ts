import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import { UserSupplement } from './models';

@Injectable({ providedIn: 'root' })
export class UserSupplementsService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = '/api/user-supplements';

  getActive(): Observable<UserSupplement[]> {
    return this.http.get<UserSupplement[]>(this.baseUrl);
  }

  activate(supplementId: number): Observable<UserSupplement> {
    return this.http.post<UserSupplement>(`${this.baseUrl}/${supplementId}`, {});
  }

  deactivate(supplementId: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${supplementId}`);
  }
}
