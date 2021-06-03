package com.monri.ionic;
import android.content.Context;
import android.widget.Toast;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.getcapacitor.Bridge;
import com.getcapacitor.JSObject;
import com.getcapacitor.PluginCall;
import com.monri.android.ResultCallback;
import com.monri.android.model.PaymentResult;
import org.json.JSONException;
import java.lang.ref.WeakReference;

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

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(JsonParser.Feature.IGNORE_UNDEFINED, false);
        JSObject paymentResultJSObject;

        try {
            paymentResultJSObject = new JSObject(mapper.writeValueAsString(paymentResult));
            savedCall.resolve(paymentResultJSObject);
        } catch (JSONException | JsonProcessingException e) {
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
