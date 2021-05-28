import { WebPlugin } from '@capacitor/core';

import type { IonicMonriPlugin } from './definitions';

export class IonicMonriWeb extends WebPlugin implements IonicMonriPlugin {
  async echo(options: { value: string }): Promise<{ value: string }> {
    console.log('ECHO', options);
    return options;
  }
}
