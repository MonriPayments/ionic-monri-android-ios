package com.monri.ionic;
import android.content.Context;
import com.getcapacitor.Bridge;
import com.getcapacitor.JSObject;
import com.getcapacitor.PluginCall;
import com.monri.android.ResultCallback;
import com.monri.android.model.PaymentResult;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PaymentResultCallback implements ResultCallback<PaymentResult> {
    private final WeakReference<Context> contextWeakReference;
    private final WeakReference<Bridge> bridgeWeakReference;
    private final String pluginCallId;

    public PaymentResultCallback(
            final WeakReference<Context> contextWeakReference,
            final WeakReference<Bridge> bridgeWeakReference,
            final String pluginCallId
    ) {
        this.contextWeakReference = contextWeakReference;
        this.bridgeWeakReference = bridgeWeakReference;
        this.pluginCallId = pluginCallId;
    }

    @Override
    public void onSuccess(final PaymentResult paymentResult) {
        final Context context = contextWeakReference.get();
        final Bridge bridge = bridgeWeakReference.get();
        final PluginCall savedCall = bridge.getSavedCall(pluginCallId);

        Map<String, Object> response = new HashMap<>();
        Map<String, Object> data = new HashMap<>();
        final String status = paymentResult.getStatus();
        data.put("status", paymentResult.getStatus());
        data.put("amount", paymentResult.getAmount());
        data.put("order_number", paymentResult.getOrderNumber());
        data.put("transaction_type", paymentResult.getTransactionType());
        data.put("pan_token", paymentResult.getPanToken());
        List<String> errors = paymentResult.getErrors();
        if (errors != null) {
            data.put("errors", errors);
        }
        data.put("created_at", paymentResult.getCreatedAt());
        data.put("amount", paymentResult.getAmount());
        data.put("currency", paymentResult.getCurrency());
        response.put("status", status);
        response.put("data", data);

        JSONObject JSONObjectResponse = new JSONObject(response);

        try {
            JSObject jsObjectResponse = JSObject.fromJSONObject(JSONObjectResponse);
            savedCall.resolve(jsObjectResponse);
        } catch (JSONException e) {
            e.printStackTrace();
            savedCall.reject(e.getMessage());
        }

    }

    @Override
    public void onError(final Throwable throwable) {
        final Bridge bridge = bridgeWeakReference.get();
        final PluginCall savedCall = bridge.getSavedCall(pluginCallId);
        savedCall.reject(throwable.getMessage());
    }
}
