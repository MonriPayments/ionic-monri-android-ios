export type MonriApiOptions = {
    authenticityToken: string;
    developmentMode: boolean;
};

export type SavedPaymentMethod = {
    type: string;
    data: { [id: string]: any };
};

export type PaymentResult = {
    status: string;
    currency?: string;
    amount?: number;
    orderNumber?: string;
    panToken?: string;
    createdAt?: string;
    transactionType?: string;
    paymentMethod?: SavedPaymentMethod;
    errors?: string[];
};

export type Card = {
    pan: string;
    cvv: string;
    expiryYear: number;
    expiryMonth: number;
    saveCard?: boolean;
};

export type Transaction = {
    email?: string;
    fullName?: string;
    address?: string;
    phone?: string;
    country?: string;
    city?: string;
    zip?: string;
    orderInfo?: string;
};

export type SavedCard = {
    panToken: string,
    cvv: string
}

export type ConfirmPaymentParams = {
    clientSecret: string;
    card?: Card;
    savedCard?: SavedCard;
    transaction: Transaction;
};

export interface IonicMonriPlugin {
    confirmPayment(
        options: {
            options: MonriApiOptions,
            params: ConfirmPaymentParams
        }
    ): Promise<PaymentResult>;
}
