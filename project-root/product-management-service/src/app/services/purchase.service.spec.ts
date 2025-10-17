import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { PurchaseService } from './purchase.service';
import { PurchaseRequest, PurchaseResponse } from '../models/purchase.model';
import { environment } from '../../environments/environment';

describe('PurchaseService', () => {
  let service: PurchaseService;
  let httpMock: HttpTestingController;

  const mockPurchaseResponse: PurchaseResponse = {
    message: 'Compra realizada exitosamente',
    remainingQuantity: 49
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [PurchaseService]
    });
    service = TestBed.inject(PurchaseService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('processPurchase', () => {
    it('should process a purchase successfully', (done) => {
      const productId = 1;
      const quantity = 1;

      service.processPurchase(productId, quantity).subscribe(response => {
        expect(response).toEqual(mockPurchaseResponse);
        expect(response.message).toBe('Compra realizada exitosamente');
        expect(response.remainingQuantity).toBe(49);
        done();
      });

      const req = httpMock.expectOne(environment.apiUrls.purchase);
      expect(req.request.method).toBe('POST');
      expect(req.request.headers.get('X-API-KEY')).toBe(environment.apiKeys.inventory);
      expect(req.request.headers.get('Content-Type')).toBe('application/json');
      expect(req.request.body).toEqual({ productId, quantity });
      req.flush(mockPurchaseResponse);
    });

    it('should use default quantity of 1 when not provided', (done) => {
      service.processPurchase(1).subscribe(() => {
        done();
      });

      const req = httpMock.expectOne(environment.apiUrls.purchase);
      expect(req.request.body.quantity).toBe(1);
      req.flush(mockPurchaseResponse);
    });

    it('should handle error when insufficient stock', (done) => {
      const errorResponse = { message: 'Stock insuficiente' };

      service.processPurchase(1, 100).subscribe({
        next: () => fail('should have failed'),
        error: (error) => {
          expect(error.status).toBe(400);
          expect(error.error.message).toBe('Stock insuficiente');
          done();
        }
      });

      const req = httpMock.expectOne(environment.apiUrls.purchase);
      req.flush(errorResponse, { status: 400, statusText: 'Bad Request' });
    });

    it('should handle server error', (done) => {
      service.processPurchase(1, 1).subscribe({
        next: () => fail('should have failed'),
        error: (error) => {
          expect(error.status).toBe(500);
          done();
        }
      });

      const req = httpMock.expectOne(environment.apiUrls.purchase);
      req.flush('Server error', { status: 500, statusText: 'Internal Server Error' });
    });

    it('should send correct request body', (done) => {
      const productId = 5;
      const quantity = 3;

      service.processPurchase(productId, quantity).subscribe(() => {
        done();
      });

      const req = httpMock.expectOne(environment.apiUrls.purchase);
      const expectedBody: PurchaseRequest = { productId, quantity };
      expect(req.request.body).toEqual(expectedBody);
      req.flush(mockPurchaseResponse);
    });
  });
});
