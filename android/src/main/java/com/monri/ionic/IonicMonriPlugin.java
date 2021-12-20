package com.monri.ionic;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;
import com.monri.android.Monri;
import com.monri.android.model.Card;
import com.monri.android.model.ConfirmPaymentParams;
import com.monri.android.model.CustomerParams;
import com.monri.android.model.MonriApiOptions;
import com.monri.android.model.TransactionParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;

@CapacitorPlugin(name = "IonicMonri")
public class IonicMonriPlugin extends Plugin {
    private Monri monri;
    private String savedPluginId;

    @PluginMethod
    public void confirmPayment(PluginCall call) {
        getBridge().saveCall(call);
        savedPluginId =  call.getCallbackId();

        MonriApiOptions monriApiOptions = parseMonriApiOptions(call);
        ConfirmPaymentParams confirmPaymentParams = parseConfirmPaymentParams(call);

        IonicMonriPlugin.writeMetaData(getContext(), String.format("Android-SDK:Ionic:%s", BuildConfig.MONRI_IONIC_PLUGIN_VERSION));

        monri = new Monri(getActivity(), monriApiOptions);
        monri.confirmPayment(getActivity(), confirmPaymentParams);
    }

    public void monriHandleOnActivityResult(final int requestCode, final int resultCode, final Intent data) {
        final boolean monriPaymentResult = monri.onPaymentResult(
                requestCode,
                data,
                new PaymentResultCallback(
                        new WeakReference<>(getContext()),
                        new WeakReference<>(getBridge()),
                        savedPluginId
                ));
    }

    private ConfirmPaymentParams parseConfirmPaymentParams(final PluginCall params) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        final JSObject paramsObject = params.getObject("params");
        final String clientSecret = paramsObject.getString("clientSecret");
        final JSObject cardJSObject = paramsObject.getJSObject("card");
        final JSObject transactionJSObject = paramsObject.getJSObject("transaction");

        final Card card = new Card(
                getNullableString(cardJSObject,"pan"),
                Integer.valueOf(getNullableString(cardJSObject,"expiryMonth")),
                Integer.valueOf(getNullableString(cardJSObject,"expiryYear")),
                getNullableString(cardJSObject,"cvv")
        );

        final CustomerParams customerParams = new CustomerParams()
                .setAddress(getNullableString(transactionJSObject, "address"))
                .setFullName(getNullableString(transactionJSObject, "fullName"))
                .setCity(getNullableString(transactionJSObject, "city"))
                .setZip(getNullableString(transactionJSObject, "zip"))
                .setPhone(getNullableString(transactionJSObject, "phone"))
                .setCountry(getNullableString(transactionJSObject, "country"))
                .setEmail(getNullableString(transactionJSObject, "email"));

        ConfirmPaymentParams confirmPaymentParams = ConfirmPaymentParams.create(
                clientSecret,
                card.toPaymentMethodParams(),
                new TransactionParams().set(customerParams)
        );

        return confirmPaymentParams;
    }

    private MonriApiOptions parseMonriApiOptions(final PluginCall options) {
        final JSObject object = options.getObject("options");

        String authenticityToken = getNullableString(object, "authenticityToken");
        boolean developmentMode = object.getBool("developmentMode");
        MonriApiOptions monriApiOptions = new MonriApiOptions(authenticityToken, developmentMode);
        return monriApiOptions;
    }

    private String getNullableString(
            JSObject jsonObject,
            String key
    ) {
        return jsonObject.getString(key);

    }

    private static void writeMetaData(Context context, String library) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferences.edit().putString("com.monri.meta.library", library).apply();
    }

}
