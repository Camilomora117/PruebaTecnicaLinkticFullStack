import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { PurchaseRequest, PurchaseResponse } from '../models/purchase.model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class PurchaseService {
  private apiUrl = environment.apiUrls.purchase;
  private apiKey = environment.apiKeys.inventory;

  constructor(private http: HttpClient) { }

  processPurchase(productId: number, quantity: number = 1): Observable<PurchaseResponse> {
    const headers = new HttpHeaders({
      'X-API-KEY': this.apiKey,
      'Content-Type': 'application/json'
    });

    const request: PurchaseRequest = {
      productId,
      quantity
    };

    return this.http.post<PurchaseResponse>(this.apiUrl, request, { headers });
  }
}
