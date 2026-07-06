import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatDialog } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { RouterLink } from '@angular/router';
import {
  BehaviorSubject,
  combineLatest,
  debounceTime,
  distinctUntilChanged,
  startWith,
  switchMap,
} from 'rxjs';

import { SupplementsApiService } from '../../core/supplements-api.service';
import { AddSupplementDialogComponent } from './add-supplement-dialog.component';

@Component({
  selector: 'app-catalog',
  imports: [
    ReactiveFormsModule,
    MatButtonModule,
    MatCardModule,
    MatFormFieldModule,
    MatIconModule,
    MatInputModule,
    RouterLink,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <div class="page">
      <mat-form-field appearance="outline" class="search">
        <mat-label>Search supplements</mat-label>
        <input matInput [formControl]="searchControl" />
        <mat-icon matSuffix>search</mat-icon>
      </mat-form-field>

      @if (supplements(); as list) {
        @if (list.length === 0) {
          <p class="empty-state muted">No supplements match "{{ searchControl.value }}".</p>
        } @else {
          <div class="list">
            @for (supplement of list; track supplement.id) {
              <mat-card appearance="outlined">
                <mat-card-content class="row">
                  <a class="item-text" [routerLink]="['/supplements', supplement.id]">
                    <span class="name">{{ supplement.name }}</span>
                    <span class="muted">
                      {{ supplement.category }}
                      @if (supplement.isCustom) {
                        · Custom
                      }
                    </span>
                  </a>
                  @if (supplement.tracked) {
                    <mat-icon class="tracked" aria-label="Tracked">check_circle</mat-icon>
                  }
                </mat-card-content>
              </mat-card>
            }
          </div>
        }
      } @else {
        <p class="empty-state muted">Loading…</p>
      }

      <button matFab class="fab" aria-label="Add supplement" (click)="openAddDialog()">
        <mat-icon>add</mat-icon>
      </button>
    </div>
  `,
  styles: `
    .search {
      width: 100%;
    }
    .list {
      display: flex;
      flex-direction: column;
      gap: 8px;
      padding-bottom: 88px;
    }
    .row {
      display: flex;
      align-items: center;
      gap: 8px;
    }
    .item-text {
      flex: 1;
      display: flex;
      flex-direction: column;
      text-decoration: none;
      color: inherit;
    }
    .name {
      font-weight: 500;
    }
    .tracked {
      color: var(--mat-sys-primary, #1976d2);
    }
    .fab {
      position: fixed;
      right: 24px;
      bottom: 24px;
    }
  `,
})
export class CatalogComponent {
  private readonly api = inject(SupplementsApiService);
  private readonly dialog = inject(MatDialog);

  private readonly refresh$ = new BehaviorSubject<void>(undefined);

  protected readonly searchControl = new FormControl('', { nonNullable: true });

  protected readonly supplements = toSignal(
    combineLatest([
      this.searchControl.valueChanges.pipe(startWith(''), debounceTime(200), distinctUntilChanged()),
      this.refresh$,
    ]).pipe(switchMap(([search]) => this.api.search(search))),
    { initialValue: null },
  );

  protected openAddDialog(): void {
    this.dialog
      .open(AddSupplementDialogComponent)
      .afterClosed()
      .subscribe((created) => {
        if (created) {
          this.refresh$.next();
        }
      });
  }
}
