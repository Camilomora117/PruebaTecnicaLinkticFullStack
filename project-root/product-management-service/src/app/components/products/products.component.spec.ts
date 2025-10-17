import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { of, throwError } from 'rxjs';
import { ProductsComponent } from './products.component';
import { ProductService } from '../../services/product.service';
import { Product } from '../../models/product.model';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';

describe('ProductsComponent', () => {
  let component: ProductsComponent;
  let fixture: ComponentFixture<ProductsComponent>;
  let productService: jest.Mocked<ProductService>;
  let router: jest.Mocked<Router>;

  const mockProducts: Product[] = [
    { id: 1, name: 'Product 1', price: 100, description: 'Description 1' },
    { id: 2, name: 'Product 2', price: 200, description: 'Description 2' }
  ];

  beforeEach(async () => {
    const productServiceMock = {
      getAllProducts: jest.fn()
    };

    const routerMock = {
      navigate: jest.fn()
    };

    await TestBed.configureTestingModule({
      imports: [
        ProductsComponent,
        HttpClientTestingModule,
        NoopAnimationsModule
      ],
      providers: [
        { provide: ProductService, useValue: productServiceMock },
        { provide: Router, useValue: routerMock }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(ProductsComponent);
    component = fixture.componentInstance;
    productService = TestBed.inject(ProductService) as jest.Mocked<ProductService>;
    router = TestBed.inject(Router) as jest.Mocked<Router>;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('ngOnInit', () => {
    it('should load products on init', (done) => {
      productService.getAllProducts.mockReturnValue(of(mockProducts));

      fixture.detectChanges();

      component.products$.subscribe(products => {
        expect(products).toEqual(mockProducts);
        expect(component.isLoading).toBe(false);
        expect(component.hasError).toBe(false);
        done();
      });
    });

    it('should set isLoading to true initially', () => {
      productService.getAllProducts.mockReturnValue(of(mockProducts));
      
      expect(component.isLoading).toBe(true);
    });

    it('should handle error when loading products fails', (done) => {
      const error = new Error('API Error');
      productService.getAllProducts.mockReturnValue(throwError(() => error));

      fixture.detectChanges();

      setTimeout(() => {
        expect(component.isLoading).toBe(false);
        expect(component.hasError).toBe(true);
        done();
      }, 100);
    });

    it('should return empty array on error', (done) => {
      const error = new Error('API Error');
      productService.getAllProducts.mockReturnValue(throwError(() => error));

      fixture.detectChanges();

      component.products$.subscribe(products => {
        expect(products).toEqual([]);
        done();
      });
    });
  });

  describe('viewDetails', () => {
    it('should navigate to product detail page', () => {
      const productId = 1;

      component.viewDetails(productId);

      expect(router.navigate).toHaveBeenCalledWith(['/products', productId]);
    });

    it('should navigate with correct product id', () => {
      component.viewDetails(42);

      expect(router.navigate).toHaveBeenCalledWith(['/products', 42]);
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

    it('should handle large numbers', () => {
      const price = 1000000;
      const formatted = component.formatPrice(price);

      expect(formatted).toContain('1.000.000');
    });
  });

  describe('Template rendering', () => {
    it('should display loader when isLoading is true', () => {
      productService.getAllProducts.mockReturnValue(of(mockProducts));
      component.isLoading = true;
      fixture.detectChanges();

      const loader = fixture.nativeElement.querySelector('.loading-container');
    });

    it('should display products grid when loaded successfully', (done) => {
      productService.getAllProducts.mockReturnValue(of(mockProducts));
      
      fixture.detectChanges();

      setTimeout(() => {
        fixture.detectChanges();
        const productsGrid = fixture.nativeElement.querySelector('.products-grid');
        expect(productsGrid).toBeTruthy();
        done();
      }, 100);
    });
  });
});
