import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Product } from '../models/product.model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ProductService {
  private apiUrl = environment.apiUrls.products;
  private apiKey = environment.apiKeys.products;

  constructor(private http: HttpClient) { }

  getAllProducts(): Observable<Product[]> {
    const headers = new HttpHeaders({
      'X-API-KEY': this.apiKey
    });

    return this.http.get<Product[]>(this.apiUrl, { headers });
  }

  getProductById(id: number): Observable<Product> {
    const headers = new HttpHeaders({
      'X-API-KEY': this.apiKey
    });

    return this.http.get<Product>(`${this.apiUrl}/${id}`, { headers });
  }

}
