import {Pipe, PipeTransform} from '@angular/core';

@Pipe({
  name: 'timeFormat'
})
export class TimeFormatPipe implements PipeTransform {

  transform(value: string): string {
    const [hours, minutes, seconds] = value.split(':').map(Number);
    return `${this.pad(hours)}:${this.pad(minutes)}`;
  }

  private pad(value: number): string {
    return value < 10 ? '0' + value : value.toString();
  }

}
