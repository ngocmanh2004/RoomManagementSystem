import { ApplicationConfig, ErrorHandler, LOCALE_ID } from '@angular/core';
import { provideZoneChangeDetection } from '@angular/core';
import { provideRouter } from '@angular/router';
import { routes } from './app.routes';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { provideAnimations } from '@angular/platform-browser/animations';
import { importProvidersFrom } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { tokenInterceptor } from './services/token.interceptor';
import { GlobalErrorHandler } from './services/global-error-handler.service';

import localeVi from '@angular/common/locales/vi';
import { registerLocaleData } from '@angular/common';
registerLocaleData(localeVi);

export const appConfig: ApplicationConfig = {
  providers: [
    { provide: ErrorHandler, useClass: GlobalErrorHandler },

    provideZoneChangeDetection({ eventCoalescing: true }),

    provideRouter(routes),
    provideAnimations(),

    provideHttpClient(withInterceptors([tokenInterceptor])),
    importProvidersFrom(FormsModule),
    { provide: LOCALE_ID, useValue: 'vi' }
  ]
};
