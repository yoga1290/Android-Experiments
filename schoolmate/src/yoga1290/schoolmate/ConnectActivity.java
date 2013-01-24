package yoga1290.schoolmate;

import yoga1290.schoolmate.R;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class ConnectActivity extends Activity
{
    private static final String TAG = "ActivityWebView";

    /**
     * Get these values after registering your oauth app at: https://foursquare.com/oauth/
     */
    public static final String CALLBACK_URL = "http://yoga1290.appspot.com/schoolmate/oauth/foursquare/callback/";
    public static final String CLIENT_ID = "BZ4QPVWSF213QA2ICE1QSHIGDMCNZBW20QD3EXBVH0OHG3IT";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_connect);
        
        String url =
            "https://foursquare.com/oauth2/authenticate" + 
                "?client_id=" + CLIENT_ID + 
                "&response_type=token" + 
                "&redirect_uri=" + CALLBACK_URL;
        
        // If authentication works, we'll get redirected to a url with a pattern like:  
        //
        //    http://YOUR_REGISTERED_REDIRECT_URI/#access_token=ACCESS_TOKEN
        //
        // We can override onPageStarted() in the web client and grab the token out.
        WebView webview = (WebView)findViewById(R.id.connectwebview);
        
        webview.getSettings().setJavaScriptEnabled(true);
        webview.setWebViewClient(new WebViewClient() {
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                String fragment = "#access_token=";
                int start = url.indexOf(fragment);
                if (start > -1) {
                    // You can use the accessToken for api calls now.
                    String accessToken = url.substring(start + fragment.length(), url.length());
        				System.out.println(accessToken);
//                    Log.v(TAG, "OAuth complete, token: [" + accessToken + "].");            	
//                    Toast.makeText(ActivityWebView.this, "Token: " + accessToken, Toast.LENGTH_SHORT).show();
                }
            }
        });
        webview.loadUrl(url);
    }

}
