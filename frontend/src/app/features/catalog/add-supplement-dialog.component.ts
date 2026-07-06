import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';

import { SupplementsApiService } from '../../core/supplements-api.service';

@Component({
  selector: 'app-add-supplement-dialog',
  imports: [
    ReactiveFormsModule,
    MatButtonModule,
    MatDialogModule,
    MatFormFieldModule,
    MatIconModule,
    MatInputModule,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <h2 mat-dialog-title>Add supplement</h2>
    <mat-dialog-content>
      <form [formGroup]="form" class="form">
        <mat-form-field appearance="outline">
          <mat-label>Name</mat-label>
          <input matInput formControlName="name" required />
        </mat-form-field>
        <mat-form-field appearance="outline">
          <mat-label>Category</mat-label>
          <input matInput formControlName="category" placeholder="e.g. Vitamin, Herbal" />
        </mat-form-field>
        <mat-form-field appearance="outline">
          <mat-label>Typical dosage</mat-label>
          <input matInput formControlName="typicalDosage" placeholder="e.g. 500 mg daily" />
        </mat-form-field>
        <mat-form-field appearance="outline">
          <mat-label>Description</mat-label>
          <textarea matInput formControlName="description" rows="2"></textarea>
        </mat-form-field>
        <mat-form-field appearance="outline">
          <mat-label>Benefits (one per line)</mat-label>
          <textarea matInput formControlName="benefits" rows="3"></textarea>
        </mat-form-field>

        <h3>Where to buy</h3>
        <div formArrayName="buyLinks" class="buy-links">
          @for (group of buyLinks.controls; track $index) {
            <div [formGroupName]="$index" class="buy-link-row">
              <mat-form-field appearance="outline" class="store">
                <mat-label>Store</mat-label>
                <input matInput formControlName="storeName" />
              </mat-form-field>
              <mat-form-field appearance="outline" class="url">
                <mat-label>Link</mat-label>
                <input matInput formControlName="url" />
              </mat-form-field>
              <button matIconButton type="button" (click)="removeBuyLink($index)" aria-label="Remove store">
                <mat-icon>close</mat-icon>
              </button>
            </div>
          }
        </div>
        <button matButton type="button" (click)="addBuyLink()">
          <mat-icon>add</mat-icon>
          Add store
        </button>
      </form>
    </mat-dialog-content>
    <mat-dialog-actions align="end">
      <button matButton mat-dialog-close>Cancel</button>
      <button matButton="filled" [disabled]="form.invalid || saving" (click)="save()">Save</button>
    </mat-dialog-actions>
  `,
  styles: `
    .form {
      display: flex;
      flex-direction: column;
      min-width: min(420px, 80vw);
      padding-top: 8px;
    }
    .buy-link-row {
      display: flex;
      gap: 8px;
      align-items: center;
    }
    .store {
      flex: 2;
    }
    .url {
      flex: 3;
    }
  `,
})
export class AddSupplementDialogComponent {
  private readonly fb = inject(FormBuilder).nonNullable;
  private readonly api = inject(SupplementsApiService);
  private readonly dialogRef = inject(MatDialogRef<AddSupplementDialogComponent>);

  protected saving = false;

  protected readonly form = this.fb.group({
    name: this.fb.control('', Validators.required),
    category: this.fb.control(''),
    typicalDosage: this.fb.control(''),
    description: this.fb.control(''),
    benefits: this.fb.control(''),
    buyLinks: this.fb.array([this.buildBuyLinkGroup()]),
  });

  protected get buyLinks() {
    return this.form.controls.buyLinks;
  }

  protected addBuyLink(): void {
    this.buyLinks.push(this.buildBuyLinkGroup());
  }

  protected removeBuyLink(index: number): void {
    this.buyLinks.removeAt(index);
  }

  protected save(): void {
    if (this.form.invalid) {
      return;
    }
    this.saving = true;
    const value = this.form.getRawValue();
    this.api
      .createCustom({
        name: value.name.trim(),
        category: value.category.trim(),
        typicalDosage: value.typicalDosage.trim(),
        description: value.description.trim(),
        benefits: value.benefits
          .split('\n')
          .map((line) => line.trim())
          .filter((line) => line.length > 0),
        buyLinks: value.buyLinks
          .map((link) => ({ storeName: link.storeName.trim(), url: this.normalizeUrl(link.url) }))
          .filter((link) => link.storeName.length > 0 && link.url.length > 0),
      })
      .subscribe({
        next: (created) => this.dialogRef.close(created),
        error: () => (this.saving = false),
      });
  }

  private buildBuyLinkGroup() {
    return this.fb.group({
      storeName: this.fb.control(''),
      url: this.fb.control(''),
    });
  }

  private normalizeUrl(url: string): string {
    const trimmed = url.trim();
    if (!trimmed) {
      return '';
    }
    return trimmed.startsWith('http') ? trimmed : `https://${trimmed}`;
  }
}
