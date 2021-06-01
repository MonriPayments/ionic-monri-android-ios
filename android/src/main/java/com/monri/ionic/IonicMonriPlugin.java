package com.monri.ionic;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.annotation.Nullable;

import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.ActivityCallback;
import com.getcapacitor.annotation.CapacitorPlugin;
import com.monri.android.Monri;
import com.monri.android.ResultCallback;
import com.monri.android.model.Card;
import com.monri.android.model.ConfirmPaymentParams;
import com.monri.android.model.MonriApiOptions;
import com.monri.android.model.PaymentResult;

@CapacitorPlugin(name = "IonicMonri")
public class IonicMonriPlugin extends Plugin {

    private Monri monri;
//    private MonriActivityEventListener monriActivityListeners;
//    private Promise confirmPaymentPromise;

    private IonicMonri implementation = new IonicMonri();

    @PluginMethod
    public void echo(PluginCall call) {
        Log.d("TEST1","TEST MESSAGE");
        Toast.makeText(getContext(), "TOAST echo", Toast.LENGTH_SHORT).show();

        String value = call.getString("value");

        JSObject ret = new JSObject();
        ret.put("value", implementation.echo(value));
        call.resolve(ret);
    }

    @PluginMethod
    public void showMessage(PluginCall call) {
        Log.d("TEST","TEST MESSAGE showMessage");
        Toast.makeText(getContext(), "TOAST showMessage", Toast.LENGTH_SHORT).show();
    }

    @PluginMethod
    public void confirmPayment(PluginCall call) {
        // init Monri API
        // invoke confirmPayment
        Log.d("CONFIRM PAYMENT call",call.toString());
        Toast.makeText(getContext(), "TOAST confirmPayment", Toast.LENGTH_SHORT).show();

        MonriApiOptions monriApiOptions = parseMonriApiOptions(call);
        ConfirmPaymentParams confirmPaymentParams = parseConfirmPaymentParams(call);

        monri = new Monri(getActivity(), monriApiOptions);

        monri.confirmPayment(getActivity(), confirmPaymentParams);

    }

    //todo this is new way but you have ot specify method when you call something for result eg. startActivityForResult(call, intent, "paymentResult"); call is PluginCall https://capacitorjs.com/docs/updating/plugins/3-0
    @ActivityCallback
    private void paymentResult(PluginCall call, ActivityResult result) {
        if (result.getResultCode() == Activity.RESULT_CANCELED) {
            call.reject("Activity canceled");
        } else {
            Intent data = result.getData();
//            monri.onPaymentResult(requestCode, data, callback);
            // do something with the result data
            // +        call.resolve("Success!");
        }
    }

    @Override
    protected void handleOnActivityResult(final int requestCode, final int resultCode, final Intent data) {
//        final boolean monriPaymentResult = monri.onPaymentResult(requestCode, data, callback);
//        if (!monriPaymentResult) {
//            super.handleOnActivityResult(requestCode, resultCode, data);
//        }
    }

    private ConfirmPaymentParams parseConfirmPaymentParams(final PluginCall params) {
        final JSObject paramsObject = params.getObject("params");
        Log.d("parseMonriApiOptions:", paramsObject.toString());
        return null;
    }

    private MonriApiOptions parseMonriApiOptions(final PluginCall options) {
        final JSObject object = options.getObject("options");
        Log.d("parseMonriApiOptions:", object.toString());
        return null;
    }

}
