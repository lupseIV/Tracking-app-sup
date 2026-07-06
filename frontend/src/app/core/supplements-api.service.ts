import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import { CreateSupplementRequest, Supplement } from './models';

@Injectable({ providedIn: 'root' })
export class SupplementsApiService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = '/api/supplements';

  search(search: string): Observable<Supplement[]> {
    const params = search ? new HttpParams().set('search', search) : new HttpParams();
    return this.http.get<Supplement[]>(this.baseUrl, { params });
  }

  getById(id: number): Observable<Supplement> {
    return this.http.get<Supplement>(`${this.baseUrl}/${id}`);
  }

  createCustom(request: CreateSupplementRequest): Observable<Supplement> {
    return this.http.post<Supplement>(this.baseUrl, request);
  }

  deleteCustom(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}
