import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HeaderComponent } from './presentation/components/header/header.component';
import { CatalogComponent } from './presentation/pages/catalog/catalog.component';
import { FooterComponent } from './presentation/components/footer/footer.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [
    CommonModule,
    HeaderComponent,
    CatalogComponent,
    FooterComponent
  ],
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title = 'Catálogo de Productos - LinkTIC';
}

