import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AboutComponent } from './about.component';
import { StatsSectionComponent } from '../../shared/components/stats-section/stats-section.component';

@Component({
  selector: 'app-stats-section',
  standalone: true,
  template: '<div>Mock Stats Section</div>',
})
class MockStatsSectionComponent {}

describe('AboutComponent', () => {
  let component: AboutComponent;
  let fixture: ComponentFixture<AboutComponent>;
  let compiled: HTMLElement;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AboutComponent],
    })
    .overrideComponent(AboutComponent, {
      set: {
        imports: [CommonModule, MockStatsSectionComponent]
      }
    })
    .compileComponents();

    fixture = TestBed.createComponent(AboutComponent);
    component = fixture.componentInstance;
    compiled = fixture.nativeElement;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render the main title', () => {
    const titleElement = compiled.querySelector('.hero-section h1');
    expect(titleElement?.textContent).toContain('Về TechRoom');
  });

  it('should render the mission title', () => {
    const missionTitle = compiled.querySelector('.mission-text h2');
    expect(missionTitle?.textContent).toContain('Sứ mệnh của chúng tôi');
  });

  it('should render the leader name', () => {
    const leaderName = compiled.querySelector('.leader-card h4');
    expect(leaderName?.textContent).toContain('Nguyễn Ngọc Mạnh');
  });

  it('should render the timeline section title', () => {
    const timelineTitle = compiled.querySelector('.timeline-section h2');
    expect(timelineTitle?.textContent).toContain('Hành trình phát triển');
  });
});