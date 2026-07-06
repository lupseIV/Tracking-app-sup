import { ChangeDetectionStrategy, Component, inject, input } from '@angular/core';
import { toObservable, toSignal } from '@angular/core/rxjs-interop';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatDialog } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { MatListModule } from '@angular/material/list';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { Router, RouterLink } from '@angular/router';
import { BehaviorSubject, Observable, combineLatest, filter, switchMap } from 'rxjs';

import { Supplement } from '../../core/models';
import { LinkOpenerService } from '../../core/link-opener.service';
import { SupplementsApiService } from '../../core/supplements-api.service';
import { UserSupplementsService } from '../../core/user-supplements.service';
import {
  ConfirmDialogComponent,
  ConfirmDialogData,
} from '../../shared/confirm-dialog.component';

@Component({
  selector: 'app-detail',
  imports: [
    MatButtonModule,
    MatCardModule,
    MatIconModule,
    MatListModule,
    MatSlideToggleModule,
    RouterLink,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <div class="page">
      <a matButton routerLink="/supplements">
        <mat-icon>arrow_back</mat-icon>
        Back to catalog
      </a>

      @if (supplement(); as s) {
        <div class="header">
          <div>
            <h2>{{ s.name }}</h2>
            <span class="muted">{{ s.category }}@if (s.isCustom) { · Custom }</span>
          </div>
          <mat-slide-toggle [checked]="s.tracked" (change)="toggleTracked(s)">
            I take this
          </mat-slide-toggle>
        </div>

        @if (s.description) {
          <p>{{ s.description }}</p>
        }

        @if (s.typicalDosage) {
          <mat-card appearance="outlined">
            <mat-card-content>
              <strong>Typical dosage</strong>
              <p class="muted no-margin">{{ s.typicalDosage }}</p>
            </mat-card-content>
          </mat-card>
        }

        @if (s.benefits.length > 0) {
          <h3>Benefits</h3>
          <ul>
            @for (benefit of s.benefits; track benefit) {
              <li>{{ benefit }}</li>
            }
          </ul>
          <p class="muted disclaimer">
            General information, not medical advice. Talk to a doctor or pharmacist before
            starting a new supplement.
          </p>
        }

        @if (s.buyLinks.length > 0) {
          <h3>Where to buy</h3>
          <div class="buy-links">
            @for (link of s.buyLinks; track link.url) {
              <mat-card appearance="outlined" class="buy-link" (click)="open(link.url)">
                <mat-card-content class="row">
                  <span class="store">{{ link.storeName }}</span>
                  <mat-icon>open_in_new</mat-icon>
                </mat-card-content>
              </mat-card>
            }
          </div>
        }

        @if (s.isCustom) {
          <button matButton color="warn" class="delete" (click)="deleteSupplement(s)">
            <mat-icon>delete</mat-icon>
            Delete custom supplement
          </button>
        }
      } @else {
        <p class="empty-state muted">Loading…</p>
      }
    </div>
  `,
  styles: `
    .header {
      display: flex;
      align-items: center;
      justify-content: space-between;
      gap: 16px;
      flex-wrap: wrap;
    }
    .header h2 {
      margin-bottom: 4px;
    }
    .no-margin {
      margin: 0;
    }
    .disclaimer {
      font-size: 12px;
    }
    .buy-links {
      display: flex;
      flex-direction: column;
      gap: 8px;
    }
    .buy-link {
      cursor: pointer;
    }
    .row {
      display: flex;
      align-items: center;
      justify-content: space-between;
    }
    .store {
      font-weight: 500;
    }
    .delete {
      margin-top: 24px;
    }
  `,
})
export class DetailComponent {
  private readonly api = inject(SupplementsApiService);
  private readonly userSupplements = inject(UserSupplementsService);
  private readonly linkOpener = inject(LinkOpenerService);
  private readonly dialog = inject(MatDialog);
  private readonly router = inject(Router);

  /** Route param, bound via withComponentInputBinding. */
  readonly id = input.required<string>();

  private readonly refresh$ = new BehaviorSubject<void>(undefined);

  protected readonly supplement = toSignal(
    combineLatest([toObservable(this.id), this.refresh$]).pipe(
      switchMap(([id]) => this.api.getById(Number(id))),
    ),
    { initialValue: null },
  );

  protected toggleTracked(supplement: Supplement): void {
    const action: Observable<unknown> = supplement.tracked
      ? this.userSupplements.deactivate(supplement.id)
      : this.userSupplements.activate(supplement.id);
    action.subscribe(() => this.refresh$.next());
  }

  protected open(url: string): void {
    this.linkOpener.open(url);
  }

  protected deleteSupplement(supplement: Supplement): void {
    const data: ConfirmDialogData = {
      title: `Delete ${supplement.name}?`,
      message: 'This removes the supplement and its intake history. This cannot be undone.',
      confirmLabel: 'Delete',
    };
    this.dialog
      .open(ConfirmDialogComponent, { data })
      .afterClosed()
      .pipe(
        filter((confirmed) => confirmed === true),
        switchMap(() => this.api.deleteCustom(supplement.id)),
      )
      .subscribe(() => this.router.navigate(['/supplements']));
  }
}
