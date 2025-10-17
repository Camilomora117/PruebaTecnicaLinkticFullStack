import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';
import { of, throwError } from 'rxjs';
import { ProductDetailComponent } from './product-detail.component';
import { InventoryService } from '../../services/inventory.service';
import { PurchaseService } from '../../services/purchase.service';
import { InventoryResponse } from '../../models/inventory.model';
import { PurchaseResponse } from '../../models/purchase.model';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';

describe('ProductDetailComponent', () => {
  let component: ProductDetailComponent;
  let fixture: ComponentFixture<ProductDetailComponent>;
  let inventoryService: jest.Mocked<InventoryService>;
  let purchaseService: jest.Mocked<PurchaseService>;
  let router: jest.Mocked<Router>;
  let snackBar: jest.Mocked<MatSnackBar>;
  let activatedRoute: any;

  const mockInventory: InventoryResponse = {
    productId: 1,
    quantity: 50,
    name: 'Product 1',
    price: 100000,
    description: 'Description 1'
  };

  const mockPurchaseResponse: PurchaseResponse = {
    message: 'Compra realizada exitosamente',
    remainingQuantity: 49
  };

  beforeEach(async () => {
    const inventoryServiceMock = {
      getInventoryByProductId: jest.fn()
    };

    const purchaseServiceMock = {
      processPurchase: jest.fn()
    };

    const routerMock = {
      navigate: jest.fn()
    };

    const snackBarMock = {
      open: jest.fn()
    };

    activatedRoute = {
      params: of({ id: '1' })
    };

    await TestBed.configureTestingModule({
      imports: [
        ProductDetailComponent,
        HttpClientTestingModule,
        NoopAnimationsModule
      ],
      providers: [
        { provide: InventoryService, useValue: inventoryServiceMock },
        { provide: PurchaseService, useValue: purchaseServiceMock },
        { provide: Router, useValue: routerMock },
        { provide: MatSnackBar, useValue: snackBarMock },
        { provide: ActivatedRoute, useValue: activatedRoute }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(ProductDetailComponent);
    component = fixture.componentInstance;
    inventoryService = TestBed.inject(InventoryService) as jest.Mocked<InventoryService>;
    purchaseService = TestBed.inject(PurchaseService) as jest.Mocked<PurchaseService>;
    router = TestBed.inject(Router) as jest.Mocked<Router>;
    snackBar = TestBed.inject(MatSnackBar) as jest.Mocked<MatSnackBar>;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('ngOnInit', () => {
    it('should load inventory on init', (done) => {
      inventoryService.getInventoryByProductId.mockReturnValue(of(mockInventory));

      fixture.detectChanges();

      component.inventory$.subscribe(inventory => {
        expect(inventory).toEqual(mockInventory);
        expect(component.isLoading).toBe(false);
        expect(component.hasError).toBe(false);
        done();
      });
    });

    it('should extract product id from route params', (done) => {
      inventoryService.getInventoryByProductId.mockReturnValue(of(mockInventory));

      fixture.detectChanges();

      setTimeout(() => {
        expect(inventoryService.getInventoryByProductId).toHaveBeenCalledWith(1);
        done();
      }, 100);
    });
  });

  describe('goBack', () => {
    it('should navigate back to products list', () => {
      component.goBack();

      expect(router.navigate).toHaveBeenCalledWith(['/products']);
    });
  });

  describe('buyProduct', () => {
    beforeEach(() => {
      inventoryService.getInventoryByProductId.mockReturnValue(of(mockInventory));
    });

    it('should reload inventory after successful purchase', (done) => {
      purchaseService.processPurchase.mockReturnValue(of(mockPurchaseResponse));
      inventoryService.getInventoryByProductId.mockReturnValue(of(mockInventory));

      component.buyProduct(1, 50);

      setTimeout(() => {
        expect(inventoryService.getInventoryByProductId).toHaveBeenCalledTimes(1);
        done();
      }, 100);
    });
  });

  describe('formatPrice', () => {
    it('should format price in Colombian pesos', () => {
      const price = 100000;
      const formatted = component.formatPrice(price);

      expect(formatted).toContain('100.000');
      expect(formatted).toContain('$');
    });

    it('should format price without decimals', () => {
      const price = 50000;
      const formatted = component.formatPrice(price);

      expect(formatted).not.toContain(',');
    });

    it('should handle zero price', () => {
      const formatted = component.formatPrice(0);

      expect(formatted).toContain('0');
    });
  });

  describe('Template rendering', () => {

    it('should display product details when loaded', (done) => {
      inventoryService.getInventoryByProductId.mockReturnValue(of(mockInventory));
      
      fixture.detectChanges();

      setTimeout(() => {
        fixture.detectChanges();
        const detailContent = fixture.nativeElement.querySelector('.detail-content');
        expect(detailContent).toBeTruthy();
        done();
      }, 100);
    });
  });
});
