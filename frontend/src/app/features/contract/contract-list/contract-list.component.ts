import { Component, OnInit } from '@angular/core';
import { ContractService } from '../../../services/contract.service';
import { Contract } from '../../../models/contract.model';
import { CommonModule } from '@angular/common';       
import { FormsModule } from '@angular/forms'; 
import { HttpClientModule } from '@angular/common/http';
@Component({
  selector: 'app-contract-list',
  standalone: true,              
  imports: [CommonModule, FormsModule, HttpClientModule],
  templateUrl: './contract-list.component.html',
  styleUrls: ['./contract-list.component.css']
})
export class ContractListComponent implements OnInit {
  contracts: Contract[] = [];
  filterStatus: string = '';

  constructor(private contractService: ContractService) {}

  ngOnInit(): void {
    this.loadContracts();
  }
  convertStatus(status: string): string {
    switch (status) {
      case 'PENDING': return 'Đang chờ duyệt';
      case 'ACTIVE': return 'Đang thuê';
      case 'EXPIRED': return 'Hết hạn';
      case 'CANCELLED': return 'Hủy';
      default: return status;
    }
  }

  loadContracts() {
    this.contractService.getContracts(this.filterStatus).subscribe(data => {
      this.contracts = data;
    });
  }

  onFilterChange() {
    this.loadContracts();
  }
}
