# ionic-monri-android-ios

Encapsulated Monri's Android and SDK payment features

## Installation

Clone project in the same tree view as your project. In package.json add

```json
{
  "dependencies": {
    "ionic-monri-android-ios": "file:../ionic-monri-android-ios"
  }
}
```

```bash
npm install
npx cap sync
```

## Usage

```ts
import {IonicMonri} from '../../../../ionic-monri-android-ios';

// ...
//new card
const result = await IonicMonri.confirmPayment({
        options: {
            authenticityToken: '6a13d79bde8da9320e88923cb3472fb638619ccb',
            developmentMode: true,
        },
        params: {
            clientSecret: "client_secret", // create one on your backend
            card: {
                pan: '4111 1111 1111 1111',
                cvv: '123',
                expiryMonth: 12,
                expiryYear: 2032,
                saveCard: true
            },
            transaction: {
                email: 'ionic.monri@gmail.com',
                orderInfo: 'Ionic monri order info',
                phone: '061123213',
                city: 'Sarajevo',
                country: 'BA',
                address: 'Ferhadija',
                fullName: 'Ionic Monri Example',
                zip: '71210',
            },
        }
    }
);

//saved card
const result = await IonicMonri.confirmPayment({
        options: {
            authenticityToken: '6a13d79bde8da9320e88923cb3472fb638619ccb',
            developmentMode: true,
        },
        params: {
            clientSecret: "client_secret", // create one on your backend
            savedCard: {
                panToken: 'd5719409d1b8eb92adae0feccd2964b805f93ae3936fdd9d8fc01a800d094584', //retrive one via customers API
                cvv: '123',//allow the user to enter cvv..
            },
            transaction: {
                email: 'ionic.saved.card.monri@gmail.com',
                orderInfo: 'Ionic monri order info saved card no 3DS',
                phone: '061123213',
                city: 'Sarajevo',
                country: 'BA',
                address: 'Ferhadija',
                fullName: 'Ionic Monri Example',
                zip: '71210',
            },
        }
    }
);

```

## Contributing

See the [contributing guide](CONTRIBUTING.md) to learn how to contribute to the repository and the development workflow.

## License

MIT
