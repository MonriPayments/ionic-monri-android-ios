import {WebPlugin} from '@capacitor/core';

import type {ConfirmPaymentParams, IonicMonriPlugin, MonriApiOptions, PaymentResult} from './definitions';

export class IonicMonriWeb extends WebPlugin implements IonicMonriPlugin {
    async echo(options: { value: string }): Promise<{ value: string }> {
        console.log('ECHO', options);
        return options;
    }

    confirmPayment(options: { options: MonriApiOptions; params: ConfirmPaymentParams }): Promise<PaymentResult> {
        console.log('confirmPayment method called');
        return Promise.resolve(undefined);
    }

    showMessage(): void {
        console.log('Message in browser but not on phones');
        window.alert('Message in browser but not on phones');
    }

    anotherMethod(options: MonriApiOptions): void {
    }

}

