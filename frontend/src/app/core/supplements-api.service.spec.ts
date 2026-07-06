import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { Supplement } from './models';
import { SupplementsApiService } from './supplements-api.service';

describe('SupplementsApiService', () => {
  let service: SupplementsApiService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    service = TestBed.inject(SupplementsApiService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpMock.verify());

  it('searches without a query param when the search text is empty', () => {
    service.search('').subscribe();
    httpMock.expectOne('/api/supplements').flush([]);
  });

  it('passes the search text as a query param', () => {
    let result: Supplement[] | undefined;
    service.search('magnesium').subscribe((supplements) => (result = supplements));

    const request = httpMock.expectOne('/api/supplements?search=magnesium');
    expect(request.request.method).toBe('GET');
    request.flush([
      {
        id: 5,
        name: 'Magnesium',
        description: '',
        typicalDosage: '',
        category: 'Mineral',
        isCustom: false,
        benefits: [],
        buyLinks: [],
        tracked: false,
      },
    ]);

    expect(result?.length).toBe(1);
    expect(result?.[0].name).toBe('Magnesium');
  });

  it('deletes custom supplements', () => {
    service.deleteCustom(42).subscribe();
    const request = httpMock.expectOne('/api/supplements/42');
    expect(request.request.method).toBe('DELETE');
    request.flush(null);
  });
});
