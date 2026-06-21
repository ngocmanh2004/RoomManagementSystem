import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { StatsSectionComponent } from '../../shared/components/stats-section/stats-section.component';

@Component({
  selector: 'app-about',
  standalone: true,
  imports: [CommonModule, StatsSectionComponent],
  templateUrl: './about.component.html',
  styleUrls: ['./about.component.css']
})
export class AboutComponent {

}