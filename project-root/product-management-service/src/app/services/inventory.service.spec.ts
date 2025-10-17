import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { InventoryService } from './inventory.service';
import { InventoryResponse } from '../models/inventory.model';
import { environment } from '../../environments/environment';

describe('InventoryService', () => {
  let service: InventoryService;
  let httpMock: HttpTestingController;

  const mockInventory: InventoryResponse = {
    productId: 1,
    quantity: 50,
    name: 'Product 1',
    price: 100,
    description: 'Description 1'
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [InventoryService]
    });
    service = TestBed.inject(InventoryService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('getInventoryByProductId', () => {
    it('should return inventory data for a product', (done) => {
      service.getInventoryByProductId(1).subscribe(inventory => {
        expect(inventory).toEqual(mockInventory);
        expect(inventory.productId).toBe(1);
        expect(inventory.quantity).toBe(50);
        done();
      });

      const req = httpMock.expectOne(`${environment.apiUrls.inventory}/1`);
      expect(req.request.method).toBe('GET');
      expect(req.request.headers.get('X-API-KEY')).toBe(environment.apiKeys.inventory);
      req.flush(mockInventory);
    });

    it('should handle error when product has no inventory', (done) => {
      service.getInventoryByProductId(999).subscribe({
        next: () => fail('should have failed'),
        error: (error) => {
          expect(error.status).toBe(404);
          done();
        }
      });

      const req = httpMock.expectOne(`${environment.apiUrls.inventory}/999`);
      req.flush('Not found', { status: 404, statusText: 'Not Found' });
    });

    it('should include correct API key in headers', (done) => {
      service.getInventoryByProductId(1).subscribe(() => {
        done();
      });

      const req = httpMock.expectOne(`${environment.apiUrls.inventory}/1`);
      expect(req.request.headers.has('X-API-KEY')).toBe(true);
      expect(req.request.headers.get('X-API-KEY')).toBe(environment.apiKeys.inventory);
      req.flush(mockInventory);
    });
  });
});
