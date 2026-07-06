import { DatePipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { MatCardModule } from '@angular/material/card';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { RouterLink } from '@angular/router';
import { BehaviorSubject, Observable, forkJoin, switchMap } from 'rxjs';

import { IntakeLog, UserSupplement } from '../../core/models';
import { IntakeLogService } from '../../core/intake-log.service';
import { UserSupplementsService } from '../../core/user-supplements.service';

interface TodayItem {
  userSupplement: UserSupplement;
  log: IntakeLog | null;
}

@Component({
  selector: 'app-today',
  imports: [DatePipe, MatCardModule, MatCheckboxModule, MatProgressBarModule, RouterLink],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <div class="page">
      @if (items(); as list) {
        @if (list.length === 0) {
          <p class="empty-state muted">
            Nothing tracked yet.

            Go to the Supplements tab, open a supplement and turn on "I take this" to see it here.
          </p>
        } @else {
          <h2>{{ today | date: 'EEEE, d MMMM' }}</h2>
          <p class="muted">{{ takenCount() }} of {{ list.length }} taken</p>
          <mat-progress-bar mode="determinate" [value]="progress()" />

          <div class="checklist">
            @for (item of list; track item.userSupplement.id) {
              <mat-card appearance="outlined">
                <mat-card-content class="row">
                  <mat-checkbox
                    [checked]="item.log !== null"
                    (change)="toggle(item)"
                  />
                  <a class="item-text" [routerLink]="['/supplements', item.userSupplement.supplementId]">
                    <span class="name">{{ item.userSupplement.name }}</span>
                    @if (item.userSupplement.typicalDosage) {
                      <span class="muted dosage">{{ item.userSupplement.typicalDosage }}</span>
                    }
                  </a>
                  @if (item.log) {
                    <span class="muted">{{ item.log.takenAt | date: 'HH:mm' }}</span>
                  }
                </mat-card-content>
              </mat-card>
            }
          </div>
        }
      } @else {
        <p class="empty-state muted">Loading…</p>
      }
    </div>
  `,
  styles: `
    .checklist {
      display: flex;
      flex-direction: column;
      gap: 8px;
      margin-top: 16px;
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
    .dosage {
      font-size: 12px;
    }
  `,
})
export class TodayComponent {
  private readonly userSupplements = inject(UserSupplementsService);
  private readonly intakeLogs = inject(IntakeLogService);

  private readonly refresh$ = new BehaviorSubject<void>(undefined);

  protected readonly today = new Date();

  private readonly data = toSignal(
    this.refresh$.pipe(
      switchMap(() =>
        forkJoin({
          checklist: this.userSupplements.getActive(),
          todayLogs: this.intakeLogs.history(1),
        }),
      ),
    ),
    { initialValue: null },
  );

  protected readonly items = computed<TodayItem[] | null>(() => {
    const data = this.data();
    if (!data) {
      return null;
    }
    const logs = data.todayLogs[0]?.logs ?? [];
    return data.checklist.map((userSupplement) => ({
      userSupplement,
      log: logs.find((log) => log.userSupplementId === userSupplement.id) ?? null,
    }));
  });

  protected readonly takenCount = computed(
    () => this.items()?.filter((item) => item.log !== null).length ?? 0,
  );

  protected readonly progress = computed(() => {
    const list = this.items();
    return list && list.length > 0 ? (this.takenCount() / list.length) * 100 : 0;
  });

  protected toggle(item: TodayItem): void {
    const action: Observable<unknown> = item.log
      ? this.intakeLogs.delete(item.log.id)
      : this.intakeLogs.log(item.userSupplement.id);
    action.subscribe(() => this.refresh$.next());
  }
}
