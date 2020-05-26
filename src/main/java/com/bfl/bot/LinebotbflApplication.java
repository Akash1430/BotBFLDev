package com.bfl.bot;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.linecorp.bot.client.LineMessagingClient;
import com.linecorp.bot.model.event.Event;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.model.profile.UserProfileResponse;
import com.linecorp.bot.spring.boot.annotation.EventMapping;
import com.linecorp.bot.spring.boot.annotation.LineMessageHandler;

@SpringBootApplication
@LineMessageHandler
public class LinebotbflApplication {

	String loginAccessToken = null;
	
    static final String USERNAME = "jamaaloozeerally@icbworks.com";
    static final String PASSWORD = "ICBzamooz1!LabQvUsuXSAoT3MgKjTOYQgf1";
    static final String LOGINURL = "https://login.salesforce.com";
    static final String GRANTSERVICE = "/services/oauth2/token?grant_type=password";
    static final String CLIENTID = "3MVG9tzQRhEbH_K07OH84Qc2SYi8X8zEwJBtw__5XYfndQsVaTM04L3dq1JnJmuc7a8lSeTR.WQ==";
    static final String CLIENTSECRET = "80F71D61E8299EC7B971CCFFEC45469D02BC0ED77B34EC53770D54F8AD3A41B9";

    private static String REST_ENDPOINT = "/services/data";
    private static String API_VERSION = "/v43.0";
    private static String baseUri;
    private static Header oauthHeader;
    //private static Header prettyPrintHeader = new BasicHeader("X-PrettyPrint", "1");
    //private static String leadId;
    //private static String contactId;
    
	public static void main(String[] args) {
		SpringApplication.run(LinebotbflApplication.class, args);
	}

    @EventMapping
    public TextMessage handleTextMessageEvent(MessageEvent<TextMessageContent> event) {
    	String senderName = "Sender";
    	
    	final String followedUserId = event.getSource().getUserId();
    	String originalMessageText = event.getMessage().getText();
    	String replyBotMessage = getFromSalesforce(originalMessageText);
    	
        LineMessagingClient client = LineMessagingClient.builder(
                "h9CYzPXg/rTBqqqqzzzkkSHn0IwelbzkGPp16JytO06iROwfrvW+rgEwsoEq0ZTDKwsNMnEiJ/3Dc3YYo9RioYNl2eBXNWtqu27jGzzUFeSNQnI59PhcbeYjpe83L9NunkszEg/TXe2Q5RLTGrwSIQdB04t89/1O/w1cDnyilFU=")
                .build();
        UserProfileResponse userProfileResponse = null;
        try {
            userProfileResponse = client.getProfile(followedUserId).get();
            senderName = userProfileResponse.getDisplayName();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        
        return new TextMessage(replyBotMessage);
    }

    @EventMapping
    public void handleDefaultMessageEvent(Event event) {
        System.out.println("event: " + event);
    }
 
    public String getFromSalesforce(String originalMessage) {
        HttpClient httpclient = HttpClientBuilder.create().build();


        String loginURL = LOGINURL + GRANTSERVICE + "&client_id=" + CLIENTID + "&client_secret=" + CLIENTSECRET
                + "&username=" + USERNAME + "&password=" + PASSWORD;

        // Login requests must be POSTs
        HttpPost httpPost = new HttpPost(loginURL);
        HttpResponse response = null;

        try {
            // Execute the login POST request
            response = httpclient.execute(httpPost);
        } catch (ClientProtocolException cpException) {
            cpException.printStackTrace();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }

        // verify response is HTTP OK
        final int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode != HttpStatus.SC_OK) {
            System.out.println("Error authenticating to Force.com: " + statusCode);
            // Error is in EntityUtils.toString(response.getEntity())
            return null;
        }

        String getResult = null;
        try {
            getResult = EntityUtils.toString(response.getEntity());
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }

        JSONObject jsonObject = null;
        String loginInstanceUrl = null;

        try {
            jsonObject = (JSONObject) new JSONTokener(getResult).nextValue();
            loginAccessToken = jsonObject.getString("access_token");
            loginInstanceUrl = jsonObject.getString("instance_url");
        } catch (JSONException jsonException) {
            jsonException.printStackTrace();
        }

        baseUri = loginInstanceUrl + REST_ENDPOINT + API_VERSION;
        oauthHeader = new BasicHeader("Authorization", "OAuth " + loginAccessToken);
        System.out.println("oauthHeader1: " + oauthHeader);
        System.out.println("\n" + response.getStatusLine());
        System.out.println("Successful login");
        System.out.println("instance URL: " + loginInstanceUrl);
        System.out.println("access token/session ID: " + loginAccessToken);
        System.out.println("baseUri: " + baseUri);

        // Get Message
        //return getMessage(originalMessage);
        return "Get Message now";
    }

    public String getMessage(String originalMessage) {

        String uri = baseUri + "/query/?q=Select+" + "vaultvalue__c+" + "From+" + "vault__c+" + "Where+" + "Name+"
                + "=+" + "'" + originalMessage + "'";

        try {

            HttpClient httpClient = HttpClientBuilder.create().build();

            try {

                HttpGet httpGet = new HttpGet(uri);
                httpGet.addHeader(oauthHeader);
                int statusCode = 0;
                HttpResponse response;
                try {
                    response = httpClient.execute(httpGet);
                    statusCode = response.getStatusLine().getStatusCode();
                } catch (Exception e) {
                    return uri + " error ee: " + e.toString();
                }

                if (statusCode == 200) {
                    String responseString = EntityUtils.toString(response.getEntity());
                    JSONObject jsonObject = new JSONObject(responseString);
                    String result = "";
                    JSONArray jsonArray = jsonObject.getJSONArray("records");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        result = jsonObject.getJSONArray("records").getJSONObject(i).getString("vaultvalue__c");
                    }
                    return result;
                } else {
                    return uri + "status code: " + statusCode;
                }
            } catch (Exception e) {
                return "error e: " + e.toString();
            }

        } catch (Exception e) {
            return "error: " + e.getStackTrace().toString();
        }
    }    
}
