import { DatePipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { MatListModule } from '@angular/material/list';

import { IntakeLogService } from '../../core/intake-log.service';

@Component({
  selector: 'app-history',
  imports: [DatePipe, MatListModule],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <div class="page">
      @if (days(); as list) {
        @if (list.length === 0) {
          <p class="empty-state muted">
            No history yet.

            Check off supplements on the Today tab and they will show up here.
          </p>
        } @else {
          <mat-list>
            @for (day of list; track day.date) {
              <h3 matSubheader>{{ day.date | date: 'EEEE, d MMMM y' }}</h3>
              @for (log of day.logs; track log.id) {
                <mat-list-item>
                  <span matListItemTitle>{{ log.supplementName }}</span>
                  <span matListItemMeta>{{ log.takenAt | date: 'HH:mm' }}</span>
                </mat-list-item>
              }
            }
          </mat-list>
        }
      } @else {
        <p class="empty-state muted">Loading…</p>
      }
    </div>
  `,
})
export class HistoryComponent {
  private readonly intakeLogs = inject(IntakeLogService);

  protected readonly days = toSignal(this.intakeLogs.history(30), { initialValue: null });
}
