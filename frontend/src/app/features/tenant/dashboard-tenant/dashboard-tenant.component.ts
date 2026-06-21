import { Component, OnInit, ViewChild, ElementRef, AfterViewInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../../services/auth.service';
import { BookingService } from '../../../services/booking.service';
import { InvoiceService } from '../../../services/invoice.service';
import { NotificationService } from '../../../services/notification.service';
import { TenantService } from '../../../services/tenant.service';
import { Contract } from '../../../models/contract.model';
import { Invoice } from '../../../models/invoice.model';
import { Chart, registerables } from 'chart.js';

Chart.register(...registerables);

@Component({
    selector: 'app-dashboard-tenant',
    standalone: true,
    imports: [CommonModule, RouterLink],
    templateUrl: './dashboard-tenant.component.html',
    styleUrls: ['./dashboard-tenant.component.css']
})
export class DashboardTenantComponent implements OnInit, AfterViewInit {
    currentDate = new Date();
    userId: number | null = null;

    // Stats
    currentContract: Contract | null = null;
    totalInvoices = 0;
    unpaidInvoices = 0;
    unreadNotifications = 0;
    nextPaymentAmount = 0;

    // Data
    recentInvoices: Invoice[] = [];
    recentNotifications: any[] = [];

    // Charts
    @ViewChild('paymentHistoryChart') paymentHistoryChartRef!: ElementRef;
    @ViewChild('invoiceStatusChart') invoiceStatusChartRef!: ElementRef;
    paymentHistoryChart: any;
    invoiceStatusChart: any;

    constructor(
        private authService: AuthService,
        private bookingService: BookingService,
        private invoiceService: InvoiceService,
        private notificationService: NotificationService,
        private tenantService: TenantService
    ) { }

    ngOnInit(): void {
        this.userId = this.authService.getCurrentUserId();
        console.log('🔍 Dashboard init - UserId:', this.userId);
        if (this.userId) {
            this.loadDashboardData();
        } else {
            console.error('❌ UserId is null!');
        }
    }

    ngAfterViewInit(): void {
        setTimeout(() => this.initCharts(), 1000);
    }

    loadDashboardData(): void {
        // Load active contract
        this.bookingService.getMyActiveContract().subscribe({
            next: (contract) => {
                this.currentContract = contract;
            },
            error: (err) => {
                console.log('No active contract found:', err);
            }
        });

        // Load invoices for stats
        if (this.userId) {
            this.tenantService.getTenantByUserId(this.userId).subscribe({
                next: (tenant: any) => {
                    const tenantId = tenant?.id;
                    if (!tenantId) {
                        console.error('❌ Could not find tenantId for user', this.userId);
                        return;
                    }

                    console.log('📊 Loading invoices for tenantId:', tenantId);
                    this.invoiceService.getByTenantId(tenantId).subscribe({
                        next: (res) => {
                            console.log('📥 Raw invoice response:', res);

                            // Handle different response formats
                            let invoices: Invoice[] = [];
                            if (Array.isArray(res)) {
                                invoices = res;
                            } else if (res.data && Array.isArray(res.data)) {
                                invoices = res.data;
                            } else if (res && typeof res === 'object') {
                                // Might be single invoice or other format
                                invoices = res.data || [];
                            }

                            console.log('✅ Processed invoices:', invoices);
                            console.log('📊 Total invoices:', invoices.length);

                            if (invoices.length > 0) {
                                this.totalInvoices = invoices.length;

                                // Count unpaid (UNPAID or OVERDUE)
                                const unpaid = invoices.filter((inv: Invoice) =>
                                    inv.status === 'UNPAID' || inv.status === 'OVERDUE'
                                );
                                this.unpaidInvoices = unpaid.length;

                                console.log('💰 Unpaid invoices:', this.unpaidInvoices);

                                // Get most recent unpaid invoice for next payment
                                const nextUnpaid = unpaid.find((inv: Invoice) => inv.status === 'UNPAID' || inv.status === 'OVERDUE');
                                this.nextPaymentAmount = nextUnpaid ? nextUnpaid.totalAmount : 0;

                                this.recentInvoices = invoices
                                    .sort((a: any, b: any) => {
                                        const dateA = new Date(a.createdAt || a.createdDate || 0).getTime();
                                        const dateB = new Date(b.createdAt || b.createdDate || 0).getTime();
                                        return dateB - dateA;
                                    })
                                    .slice(0, 5);
                            } else {
                                console.warn('No invoices found - initializing empty charts');
                                this.totalInvoices = 0;
                                this.unpaidInvoices = 0;
                                this.nextPaymentAmount = 0;
                                this.recentInvoices = [];
                            }

                            this.preparePaymentHistoryData(invoices);
                            this.prepareInvoiceStatusData(invoices);
                        },
                        error: (err) => {
                            console.error('Error loading invoices:', err);
                        }
                    });
                },
                error: (err) => {
                    console.error('Error loading tenant info:', err);
                }
            });
        }

        // Load notifications
        if (this.userId) {
            this.notificationService.getNotificationsByUserId(this.userId).subscribe({
                next: (notifications: any[]) => {
                    this.recentNotifications = notifications.slice(0, 5);
                    this.unreadNotifications = notifications.filter((n: any) => !n.read).length;
                },
                error: (err: any) => console.error('Error loading notifications:', err)
            });
        }
    }

    // Payment History Chart Data
    paymentHistoryLabels: string[] = [];
    paymentHistoryData: number[] = [];

    preparePaymentHistoryData(invoices: Invoice[]): void {
        const months = [];
        const data = [];
        const today = new Date();

        for (let i = 5; i >= 0; i--) {
            const d = new Date(today.getFullYear(), today.getMonth() - i, 1);
            const year = d.getFullYear();
            const month = d.getMonth() + 1;
            const monthStr = `${year}-${month.toString().padStart(2, '0')}`;

            months.push(`Tháng ${month}`);

            const total = invoices
                .filter((inv: Invoice) => {
                    const status = inv.status ? inv.status.toUpperCase() : '';

                    // Case 1: Database returns "YYYY-MM" (Standard)
                    if (inv.month === monthStr) return status === 'PAID';

                    // Case 2: Database returns "MM/YYYY" (Review screen shows this format)
                    const [m, y] = inv.month.includes('/') ? inv.month.split('/') : [];
                    if (m && y) {
                        const normalized = `${y}-${m.padStart(2, '0')}`;
                        return normalized === monthStr && status === 'PAID';
                    }

                    // Case 3: Handle T01/2026 format seen in screenshot
                    if (inv.month.startsWith('T')) {
                        const cleanMonth = inv.month.substring(1); 
                        const [m2, y2] = cleanMonth.split('/');
                        if (m2 && y2) {
                            const normalized2 = `${y2}-${m2.padStart(2, '0')}`;
                            return normalized2 === monthStr && status === 'PAID';
                        }
                    }

                    return false;
                })
                .reduce((sum: number, inv: Invoice) => sum + inv.totalAmount, 0);

            data.push(total);
        }

        this.paymentHistoryLabels = months;
        this.paymentHistoryData = data;
        this.initPaymentHistoryChart();
    }

    // Invoice Status Chart Data
    paidCount = 0;
    unpaidCount = 0;
    overdueCount = 0;

    prepareInvoiceStatusData(invoices: Invoice[]): void {
        this.paidCount = invoices.filter((inv: Invoice) => inv.status === 'PAID').length;
        this.unpaidCount = invoices.filter((inv: Invoice) => inv.status === 'UNPAID').length;
        this.overdueCount = invoices.filter((inv: Invoice) => inv.status === 'OVERDUE').length;
        this.initInvoiceStatusChart();
    }

    // Chart Initialization
    initCharts(): void {
        // Charts will be initialized after data is loaded
    }

    initPaymentHistoryChart(): void {
        if (!this.paymentHistoryChartRef || this.paymentHistoryData.length === 0) return;

        const ctx = this.paymentHistoryChartRef.nativeElement.getContext('2d');
        if (this.paymentHistoryChart) this.paymentHistoryChart.destroy();

        this.paymentHistoryChart = new Chart(ctx, {
            type: 'line',
            data: {
                labels: this.paymentHistoryLabels,
                datasets: [{
                    label: 'Đã thanh toán',
                    data: this.paymentHistoryData,
                    borderColor: '#10b981',
                    backgroundColor: 'rgba(16, 185, 129, 0.1)',
                    tension: 0.4,
                    fill: true,
                    pointBackgroundColor: '#fff',
                    pointBorderColor: '#10b981',
                    pointRadius: 5,
                    pointHoverRadius: 7
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: { display: false },
                    tooltip: {
                        backgroundColor: 'rgba(0, 0, 0, 0.8)',
                        padding: 12,
                        callbacks: {
                            label: (context) => {
                                const value = context.parsed.y || 0;
                                return new Intl.NumberFormat('vi-VN', {
                                    style: 'currency',
                                    currency: 'VND'
                                }).format(value);
                            }
                        }
                    }
                },
                scales: {
                    y: {
                        beginAtZero: true,
                        grid: { color: '#f3f4f6' },
                        ticks: {
                            callback: (val) => {
                                const num = val as number;
                                if (num >= 1000000) return (num / 1000000) + 'M';
                                if (num >= 1000) return (num / 1000) + 'k';
                                return num;
                            }
                        }
                    },
                    x: { grid: { display: false } }
                }
            }
        });
    }

    initInvoiceStatusChart(): void {
        if (!this.invoiceStatusChartRef) return;
        const ctx = this.invoiceStatusChartRef.nativeElement.getContext('2d');

        if (this.invoiceStatusChart) this.invoiceStatusChart.destroy();

        this.invoiceStatusChart = new Chart(ctx, {
            type: 'doughnut',
            data: {
                labels: ['Đã thanh toán', 'Chưa thanh toán', 'Quá hạn'],
                datasets: [{
                    data: [this.paidCount, this.unpaidCount, this.overdueCount],
                    backgroundColor: ['#10b981', '#f59e0b', '#ef4444'],
                    borderWidth: 0,
                    hoverOffset: 4
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                cutout: '70%',
                plugins: {
                    legend: {
                        position: 'bottom',
                        labels: { usePointStyle: true, padding: 15 }
                    }
                }
            }
        });
    }

    getRoomName(): string {
        return this.currentContract?.roomName || 'Chưa có phòng';
    }

    getBuildingName(): string {
        return this.currentContract?.buildingName || 'N/A';
    }

    getContractStatus(): string {
        if (!this.currentContract) return 'Chưa có hợp đồng';

        switch (this.currentContract.status) {
            case 'ACTIVE': return 'Đang thuê';
            case 'PENDING': return 'Chờ duyệt';
            case 'EXPIRED': return 'Đã hết hạn';
            case 'CANCELLED': return 'Đã hủy';
            default: return this.currentContract.status;
        }
    }
}
