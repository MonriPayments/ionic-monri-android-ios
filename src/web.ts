import {WebPlugin} from '@capacitor/core';

import type {ConfirmPaymentParams, IonicMonriPlugin, MonriApiOptions, PaymentResult} from './definitions';

export class IonicMonriWeb extends WebPlugin implements IonicMonriPlugin {
    async confirmPayment(options: { options: MonriApiOptions; params: ConfirmPaymentParams }): Promise<PaymentResult> {
        console.error('confirmPayment method is not implemented for web');
        return Promise.resolve(undefined);
    }

}

