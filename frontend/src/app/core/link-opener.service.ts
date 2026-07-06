import { Injectable } from '@angular/core';

/**
 * Opens external links in the system browser.
 *
 * Seam for the Capacitor Android build: replace this implementation with the
 * Capacitor Browser plugin (`Browser.open({ url })`) — components only depend
 * on this service, never on window directly.
 */
@Injectable({ providedIn: 'root' })
export class LinkOpenerService {
  open(url: string): void {
    window.open(url, '_blank', 'noopener');
  }
}
