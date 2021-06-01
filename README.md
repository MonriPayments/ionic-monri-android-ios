# ionic-monri-android-ios

Encapsulated Monri's Android and SDK payment features

## Install

```bash
npm install ionic-monri-android-ios
npx cap sync
```

## API

<docgen-index>

* [`echo(...)`](#echo)
* [`confirmPayment(...)`](#confirmpayment)
* [`showMessage()`](#showmessage)
* [`anotherMethod(...)`](#anothermethod)

</docgen-index>

<docgen-api>
<!--Update the source file JSDoc comments and rerun docgen to update the docs below-->

### echo(...)

```typescript
echo(options: { value: string; }) => any
```

| Param         | Type                            |
| ------------- | ------------------------------- |
| **`options`** | <code>{ value: string; }</code> |

**Returns:** <code>any</code>

--------------------


### confirmPayment(...)

```typescript
confirmPayment(options: { options: MonriApiOptions; params: ConfirmPaymentParams; }) => any
```

| Param         | Type                                                                     |
| ------------- | ------------------------------------------------------------------------ |
| **`options`** | <code>{ options: MonriApiOptions; params: ConfirmPaymentParams; }</code> |

**Returns:** <code>any</code>

--------------------


### showMessage()

```typescript
showMessage() => void
```

--------------------


### anotherMethod(...)

```typescript
anotherMethod(options: MonriApiOptions) => void
```

| Param         | Type                                                                  |
| ------------- | --------------------------------------------------------------------- |
| **`options`** | <code>{ authenticityToken: string; developmentMode: boolean; }</code> |

--------------------

</docgen-api>
