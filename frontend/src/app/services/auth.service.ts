/**
 * Lấy landlordId từ user hiện tại (nếu có)
 */

import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, BehaviorSubject } from 'rxjs';
import { tap, map, catchError } from 'rxjs/operators';
import { AuthResponse, UserInfo, RefreshTokenRequest } from '../models/users';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private apiUrl = 'http://localhost:8081/api/auth';

  private currentUserSubject = new BehaviorSubject<UserInfo | null>(this.getCurrentUser());
  public currentUser$ = this.currentUserSubject.asObservable();

  constructor(private http: HttpClient) { }

  private convertRoleToNumber(role: string | number): number {
    if (typeof role === 'number') return role;

    const roleMap: { [key: string]: number } = {
      'ADMIN': 0,
      'LANDLORD': 1,
      'TENANT': 2
    };
    return roleMap[role] ?? 2;
  }

  private normalizeUser(user: any): UserInfo {
    return {
      ...user,
      role: this.convertRoleToNumber(user.role)
    };
  }

  register(user: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/register`, user);
  }

  login(username: string, password: string): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/login`, { username, password })
      .pipe(
        tap(response => {
          this.saveTokens(response.accessToken, response.refreshToken);
          const normalizedUser = this.normalizeUser(response.user);
          this.saveUser(normalizedUser);
          this.currentUserSubject.next(normalizedUser);

          console.log('✅ Login - Saved user:', normalizedUser);
        })
      );
  }

  refreshToken(): Observable<AuthResponse> {
    const refreshToken = this.getRefreshToken();
    if (!refreshToken) {
      throw new Error('No refresh token available');
    }

    const request: RefreshTokenRequest = { refreshToken };
    return this.http.post<AuthResponse>(`${this.apiUrl}/refresh`, request)
      .pipe(
        tap(response => {
          this.saveTokens(response.accessToken, response.refreshToken);
        })
      );
  }

  saveTokens(accessToken: string, refreshToken: string) {
    localStorage.setItem('accessToken', accessToken);
    localStorage.setItem('refreshToken', refreshToken);
  }

  saveUser(user: UserInfo) {
    localStorage.setItem('currentUser', JSON.stringify(user));
    this.currentUserSubject.next(user);
  }

  updateUserInfo(partialUser: Partial<UserInfo>) {
    const current = this.currentUserSubject.value;
    if (!current) return;

    const updated = { ...current, ...partialUser };
    this.saveUser(updated);
  }

  getAccessToken(): string | null {
    return localStorage.getItem('accessToken');
  }

  getRefreshToken(): string | null {
    return localStorage.getItem('refreshToken');
  }

  getCurrentUser(): UserInfo | null {
    const userStr = localStorage.getItem('currentUser');
    if (!userStr) return null;

    try {
      const user = JSON.parse(userStr);
      return this.normalizeUser(user);
    } catch (e) {
      console.error('Error parsing currentUser:', e);
      return null;
    }
  }

  getCurrentUserId(): number | null {
    const user = this.getCurrentUser();
    return user ? user.id : null;
  }

  getCurrentLandlordId(): number | null {
    const user = this.getCurrentUser();
    if (user && user.landlord && user.landlord.id) {
      return user.landlord.id;
    }
    return null;
  }

  /**
   * Fetch current user info from backend and update local cache.
   */
  fetchCurrentUser(): Observable<UserInfo> {
    return this.http.get<UserInfo>(`${this.apiUrl}/me`).pipe(
      tap(user => {
        const normalized = this.normalizeUser(user);
        this.saveUser(normalized);
      })
    );
  }

  getUserRole(): number | null {
    const user = this.getCurrentUser();
    return user ? user.role : null;
  }

  isAdmin(): boolean {
    return this.getUserRole() === 0;
  }

  isLandlord(): boolean {
    return this.getUserRole() === 1;
  }

  isTenant(): boolean {
    return this.getUserRole() === 2;
  }

  /**
   * Check if current tenant has an active contract
   * Used for smart routing: tenant with contract → dashboard, without → homepage
   */
  hasActiveContract(): Observable<boolean> {
    const userId = this.getCurrentUserId();
    if (!userId) {
      return new BehaviorSubject<boolean>(false).asObservable();
    }

    // Call backend to check for active contracts
    return this.http.get<any>(`/api/bookings/my-contract`, { withCredentials: true }).pipe(
      map((response: any) => {
        // If we get a contract back, tenant has active contract
        return response && response.data !== null;
      }),
      tap(hasContract => console.log('🔍 Has active contract:', hasContract)),
      catchError((error: any) => {
        console.log('⚠️ No active contract found:', error.status);
        // If 404 or any error, assume no active contract
        return new BehaviorSubject<boolean>(false).asObservable();
      })
    );
  }

  logout(): Observable<any> {
    const refreshToken = this.getRefreshToken();

    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
    localStorage.removeItem('currentUser');
    this.currentUserSubject.next(null);

    if (refreshToken) {
      const request: RefreshTokenRequest = { refreshToken };
      return this.http.post(`${this.apiUrl}/logout`, request);
    }

    return new Observable(subscriber => {
      subscriber.next(null);
      subscriber.complete();
    });
  }

  isLoggedIn(): boolean {
    return !!this.getAccessToken();
  }

  isTokenExpired(): boolean {
    const token = this.getAccessToken();
    if (!token) return true;

    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      return payload.exp * 1000 < Date.now();
    } catch (e) {
      return true;
    }
  }
}