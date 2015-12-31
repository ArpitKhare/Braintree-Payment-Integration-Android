package app.com.braintree;

import com.loopj.android.http.*;
import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.braintreepayments.api.BraintreePaymentActivity;
import com.braintreepayments.api.PaymentRequest;
import com.braintreepayments.api.models.PaymentMethodNonce;


import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {

    private  static int REQUEST_CODE=100;
    private String clientToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        generateClientToken();

    }

    private void generateClientToken() {
        String strurl = "https://braintree/client_token";
        //String strurl = "https://www.google.com";

        System.out.println(strurl);

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(strurl, new AsyncHttpResponseHandler() {



            @Override
            public void onStart() {
                // called before request is started
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                clientToken = new String(response);
                System.out.println("success" + statusCode + "response: " + clientToken);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
            }

            @Override
            public void onRetry(int retryNo) {
                // called when request is retried
            }
        });
    }

    public void onBraintreeSubmit(View v) {
        System.out.println("In onBraintreeSubmit"+clientToken);
        PaymentRequest paymentRequest = new PaymentRequest()
                .clientToken(clientToken);
        startActivityForResult(paymentRequest.getIntent(this), REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE) {
            switch (resultCode) {
                case Activity.RESULT_OK:
                    PaymentMethodNonce paymentMethodNonce = data.getParcelableExtra(
                            BraintreePaymentActivity.EXTRA_PAYMENT_METHOD_NONCE
                    );
                    String nonce = paymentMethodNonce.getNonce();
                    postPayment(nonce);
                    break;
                case BraintreePaymentActivity.BRAINTREE_RESULT_DEVELOPER_ERROR:
                case BraintreePaymentActivity.BRAINTREE_RESULT_SERVER_ERROR:
                case BraintreePaymentActivity.BRAINTREE_RESULT_SERVER_UNAVAILABLE:
                    // handle errors here, a throwable may be available in
                    // data.getSerializableExtra(BraintreePaymentActivity.EXTRA_ERROR_MESSAGE)
                    break;
                default:
                    break;
            }
        }
    }

    private void postPayment(String nonce) {
        String strurl = "https://braintree/checkout";

        System.out.println(strurl);

        RequestParams params = new RequestParams();
        params.put("amount", 10);
        params.put("payment_method_nonce", nonce);

        AsyncHttpClient client = new AsyncHttpClient();
        client.post(strurl, params,new AsyncHttpResponseHandler() {


            @Override
            public void onStart() {
                // called before request is started
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                String result = new String(response);
                System.out.println("success" + statusCode + "result: " + result);
                Toast.makeText(MainActivity.this,result,Toast.LENGTH_SHORT).show();


            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
            }

            @Override
            public void onRetry(int retryNo) {
                // called when request is retried
            }
        });
    }

}
