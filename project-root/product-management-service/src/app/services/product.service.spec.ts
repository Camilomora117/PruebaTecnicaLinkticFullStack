import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { ProductService } from './product.service';
import { Product } from '../models/product.model';
import { environment } from '../../environments/environment';

describe('ProductService', () => {
  let service: ProductService;
  let httpMock: HttpTestingController;

  const mockProducts: Product[] = [
    { id: 1, name: 'Product 1', price: 100, description: 'Description 1' },
    { id: 2, name: 'Product 2', price: 200, description: 'Description 2' }
  ];

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [ProductService]
    });
    service = TestBed.inject(ProductService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('getAllProducts', () => {
    it('should return an Observable<Product[]>', (done) => {
      service.getAllProducts().subscribe(products => {
        expect(products).toEqual(mockProducts);
        expect(products.length).toBe(2);
        done();
      });

      const req = httpMock.expectOne(environment.apiUrls.products);
      expect(req.request.method).toBe('GET');
      expect(req.request.headers.get('X-API-KEY')).toBe(environment.apiKeys.products);
      req.flush(mockProducts);
    });

    it('should handle error when API fails', (done) => {
      const errorMessage = 'Server error';

      service.getAllProducts().subscribe({
        next: () => fail('should have failed'),
        error: (error) => {
          expect(error.status).toBe(500);
          done();
        }
      });

      const req = httpMock.expectOne(environment.apiUrls.products);
      req.flush(errorMessage, { status: 500, statusText: 'Server Error' });
    });
  });

  describe('getProductById', () => {
    it('should return a single product', (done) => {
      const mockProduct = mockProducts[0];

      service.getProductById(1).subscribe(product => {
        expect(product).toEqual(mockProduct);
        expect(product.id).toBe(1);
        done();
      });

      const req = httpMock.expectOne(`${environment.apiUrls.products}/1`);
      expect(req.request.method).toBe('GET');
      expect(req.request.headers.get('X-API-KEY')).toBe(environment.apiKeys.products);
      req.flush(mockProduct);
    });

    it('should handle 404 error when product not found', (done) => {
      service.getProductById(999).subscribe({
        next: () => fail('should have failed'),
        error: (error) => {
          expect(error.status).toBe(404);
          done();
        }
      });

      const req = httpMock.expectOne(`${environment.apiUrls.products}/999`);
      req.flush('Not found', { status: 404, statusText: 'Not Found' });
    });
  });
});
