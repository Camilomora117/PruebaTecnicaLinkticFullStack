import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { InventoryResponse } from '../models/inventory.model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class InventoryService {
  private apiUrl = environment.apiUrls.inventory;
  private apiKey = environment.apiKeys.inventory;

  constructor(private http: HttpClient) { }

  getInventoryByProductId(productId: number): Observable<InventoryResponse> {
    const headers = new HttpHeaders({
      'X-API-KEY': this.apiKey
    });

    return this.http.get<InventoryResponse>(`${this.apiUrl}/${productId}`, { headers });
  }
}
