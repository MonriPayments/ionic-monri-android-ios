import Foundation
import Capacitor
import Monri

/**
 * Please read the Capacitor iOS Plugin Development Guide
 * here: https://capacitorjs.com/docs/plugins/ios
 */
@objc(IonicMonriPlugin)
public class IonicMonriPlugin: CAPPlugin {

    @objc func confirmPayment(_ call: CAPPluginCall) {
        DispatchQueue.main.async { [weak self] in
            guard let module = self else {
                return
            }

            module.__call(call)
        }
    }

    private func __call(_ call: CAPPluginCall){
        if(!isPaymentMethodSupported(call)){
            return
        }

        do {
            let options = try parseMonriApiOptions(call)
            let confirmPaymentParams = try parseConfirmPaymentParams(call)
            let delegate = UIApplication.shared.delegate!
            let vc = delegate.window!!.rootViewController!
            let monri = MonriApi(vc, options: options)

            writeMetaData()

            monri.confirmPayment(confirmPaymentParams) { [weak self] result in
                switch (result) {
                case .result(let paymentResult):
                    var rv: [String: Any] = [
                        "status": paymentResult.status,
                        "currency": paymentResult.currency,
                        "amount": paymentResult.amount,
                        "orderNumber": paymentResult.orderNumber,
                        "createdAt": paymentResult.createdAt,
                        "transactionType": paymentResult.transactionType,
                    ]

                    if let pm = paymentResult.paymentMethod {
                        rv["paymentMethod"] = [
                            "type": pm.type,
                            "data": [
                                "brand": pm.data["brand"]!,
                                "expirationDate": pm.data["expiration_date"]!,
                                "issuer": pm.data["issuer"]!,
                                "masked": pm.data["masked"]!,
                                "token": pm.data["token"]!
                            ]
                        ]
                    }

                    if let panToken = paymentResult.panToken {
                        rv["panToken"] = panToken
                    }

                    rv["errors"] = paymentResult.errors

                    call.resolve(rv)
                case .error(let e):
                    call.resolve(["status": "error", "errors": [e.localizedDescription]])
                case .declined(let d):
                    call.resolve(["status": d.status])
                case .pending:
                    call.resolve(["status": "pending"])
                }
            }


        } catch {
            if let configurationError = error as? MonriAndroidIosConfirmPaymentError {
                switch (configurationError) {
                case .configurationError(let m):
                    call.reject(MonriAndroidIosConfirmPaymentErrorCodes.configurationError.rawValue)
                case .parsingError(let m):
                    call.reject(MonriAndroidIosConfirmPaymentErrorCodes.parsingError.rawValue, m, error)
                case .failedToParseMonriApiOptions:
                    call.reject(MonriAndroidIosConfirmPaymentErrorCodes.failedToParseMonriApiOptions.rawValue, "Failed to parse api options", error)
                case .missingRequiredAttribute(let m):
                    call.reject(MonriAndroidIosConfirmPaymentErrorCodes.missingRequiredAttribute.rawValue, m, error)
                }

            } else {
                call.reject(MonriAndroidIosConfirmPaymentErrorCodes.unknown.rawValue, error.localizedDescription, error)
            }
        }
    }

     private func writeMetaData(){
            let version: String = Bundle(identifier: "org.cocoapods.IonicMonriAndroidIos")?.infoDictionary?["CFBundleShortVersionString"] as? String ?? "1.0.0"

            let defaults = UserDefaults.standard
            defaults.set("iOS-SDK:Ionic:\(version)", forKey: "com.monri.meta.library")
        }

    private func isPaymentMethodSupported(_ call: CAPPluginCall) -> Bool{
        let paramsObject: JSObject? = call.getObject("params")
        if(paramsObject == nil){
            call.reject("missing params object");
            return false
        }

        let savedCardJSObject: JSObject? = paramsObject!["savedCard"] as? JSObject
        let cardJSObject: JSObject? = paramsObject!["card"] as? JSObject

        let doesContainValidPaymentMethod: Bool = (savedCardJSObject != nil || cardJSObject != nil)

        if (!doesContainValidPaymentMethod) {
            call.reject("Unsupported payment method, 'card' or 'savedCard' not found");
        }
        return doesContainValidPaymentMethod;
    }

    private func parseMonriApiOptions(_ call: CAPPluginCall) throws -> MonriApiOptions {
        guard let monriApiOptionsJSObject: JSObject = call.getObject("options") else {
            throw MonriAndroidIosConfirmPaymentError.missingRequiredAttribute("params")
        }

        guard let authenticityToken = monriApiOptionsJSObject["authenticityToken"] as? String else {
            throw MonriAndroidIosConfirmPaymentError.missingRequiredAttribute("authenticityToken")
        }


        let developmentMode = (monriApiOptionsJSObject["developmentMode"] as? NSNumber) != 0


        return MonriApiOptions(authenticityToken: authenticityToken, developmentMode: developmentMode)
    }

    private func parseConfirmPaymentParams(_ call: CAPPluginCall) throws -> ConfirmPaymentParams {

        guard let paramsObject: JSObject = call.getObject("params") else {
            throw MonriAndroidIosConfirmPaymentError.missingRequiredAttribute("params")
        }

        guard let clientSecret: String = paramsObject["clientSecret"] as? String else {
            throw MonriAndroidIosConfirmPaymentError.missingRequiredAttribute("clientSecret")
        }

        let savedCardJSObject: JSObject? = paramsObject["savedCard"] as? JSObject
        let cardJSObject: JSObject? = paramsObject["card"] as? JSObject

        if(savedCardJSObject == nil && cardJSObject == nil){
            throw MonriAndroidIosConfirmPaymentError.missingRequiredAttribute("card or savedCard")
        }

        let paymentMethod = cardJSObject == nil ? SavedCard(
            panToken: try requiredStringAttribute(savedCardJSObject!, "panToken", "params.savedCard.panToken"),
            cvc: try requiredStringAttribute(savedCardJSObject!, "cvv", "params.savedCard.cvv")
        ).toPaymentMethodParams()
        :Card(
            number: try requiredStringAttribute(cardJSObject!, "pan", "params.card.pan"),
            cvc: try requiredStringAttribute(cardJSObject!, "cvv", "params.card.cvv"),
            expMonth: try requiredIntAttribute(cardJSObject!, "expiryMonth", "params.card.expiryMonth"),
            expYear: try requiredIntAttribute(cardJSObject!, "expiryYear", "params.card.expiryYear"),
            tokenizePan: (cardJSObject!["saveCard"] as? Bool) ?? false
        ).toPaymentMethodParams()

        guard let transactionJSObject: JSObject = paramsObject["transaction"] as? JSObject else {
            throw MonriAndroidIosConfirmPaymentError.missingRequiredAttribute("transaction")

        }

        let customerParams = CustomerParams(email: getString(transactionJSObject, "email"),
                                            fullName: getString(transactionJSObject, "fullName"),
                                            address: getString(transactionJSObject, "address"),
                                            city: getString(transactionJSObject, "city"),
                                            zip: getString(transactionJSObject, "zip"),
                                            phone: getString(transactionJSObject, "phone"),
                                            country: getString(transactionJSObject, "country")
        )

        return ConfirmPaymentParams(paymentId: clientSecret, paymentMethod: paymentMethod, transaction: TransactionParams.create()
                                        .set(customerParams: customerParams)
                                        .set("order_info", transactionJSObject["orderInfo"] as? String)
        )
    }


    private func requiredStringAttribute(_ jsObject: JSObject, _ key: String, _ path: String? = nil) throws -> String {
        guard let value = jsObject[key] as? String else {
            throw MonriAndroidIosConfirmPaymentError.missingRequiredAttribute(path ?? key)
        }

        return value
    }

    private func getString(_ jsObject: JSObject, _ key: String) -> String {
        return jsObject[key] as! String
    }

    private func requiredIntAttribute(_ jsObject: JSObject, _ key: String, _ path: String? = nil) throws -> Int {
        guard let value = jsObject[key] as? Int else {
            throw MonriAndroidIosConfirmPaymentError.missingRequiredAttribute(path ?? key)
        }

        return value
    }

    enum MonriAndroidIosConfirmPaymentErrorCodes: String {
        case failedToParseMonriApiOptions
        case configurationError
        case parsingError
        case missingRequiredAttribute
        case unknown
    }

    enum MonriAndroidIosConfirmPaymentError: Error {
        case failedToParseMonriApiOptions
        case configurationError(String)
        case parsingError(String)
        case missingRequiredAttribute(String)
    }

}
