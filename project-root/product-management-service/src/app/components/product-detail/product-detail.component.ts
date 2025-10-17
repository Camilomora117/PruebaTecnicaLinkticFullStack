import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatChipsModule } from '@angular/material/chips';
import { MatDividerModule } from '@angular/material/divider';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { Observable, switchMap, tap, catchError, of, shareReplay } from 'rxjs';
import { InventoryResponse } from '../../models/inventory.model';
import { InventoryService } from '../../services/inventory.service';
import { PurchaseService } from '../../services/purchase.service';

@Component({
  selector: 'app-product-detail',
  standalone: true,
  imports: [
    CommonModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatSnackBarModule,
    MatChipsModule,
    MatDividerModule,
    MatProgressSpinnerModule
  ],
  templateUrl: './product-detail.component.html',
  styleUrls: ['./product-detail.component.scss']
})
export class ProductDetailComponent implements OnInit {
  inventory$!: Observable<InventoryResponse>;
  isLoading = true;
  hasError = false;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private inventoryService: InventoryService,
    private purchaseService: PurchaseService,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.isLoading = true;
    this.hasError = false;
    
    this.inventory$ = this.route.params.pipe(
      switchMap(params => {
        const id = Number(params['id']);
        return this.inventoryService.getInventoryByProductId(id);
      }),
      tap(() => {
        this.isLoading = false;
      }),
      catchError((error) => {
        this.isLoading = false;
        this.hasError = true;
        this.snackBar.open('Error al cargar el producto', 'Cerrar', {
          duration: 3000,
          panelClass: ['error-snackbar']
        });
        return of(null as any);
      }),
      shareReplay(1)
    );
    
    this.inventory$.subscribe();
  }

  goBack(): void {
    this.router.navigate(['/products']);
  }

  buyProduct(productId: number, currentQuantity: number): void {
    if (currentQuantity === 0) {
      this.snackBar.open('Producto sin stock disponible', 'Cerrar', {
        duration: 3000,
        panelClass: ['error-snackbar']
      });
      return;
    }

    this.purchaseService.processPurchase(productId, 1).subscribe({
      next: (response) => {
        this.snackBar.open(response.message, 'Cerrar', {
          duration: 4000,
          panelClass: ['success-snackbar']
        });
        
        this.isLoading = true;
        this.inventory$ = this.inventoryService.getInventoryByProductId(productId).pipe(
          tap(() => this.isLoading = false),
          catchError((error) => {
            this.isLoading = false;
            return of(null as any);
          }),
          shareReplay(1)
        );
        
        this.inventory$.subscribe();
      },
      error: (error) => {
        const errorMessage = error.error?.message || 'Error al procesar la compra';
        this.snackBar.open(errorMessage, 'Cerrar', {
          duration: 3000,
          panelClass: ['error-snackbar']
        });
      }
    });
  }

  formatPrice(price: number): string {
    return new Intl.NumberFormat('es-CO', {
      style: 'currency',
      currency: 'COP',
      minimumFractionDigits: 0
    }).format(price);
  }
}
