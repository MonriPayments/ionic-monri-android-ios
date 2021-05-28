import { registerPlugin } from '@capacitor/core';

import type { IonicMonriPlugin } from './definitions';

const IonicMonri = registerPlugin<IonicMonriPlugin>('IonicMonri', {
  web: () => import('./web').then(m => new m.IonicMonriWeb()),
});

export * from './definitions';
export { IonicMonri };
