package com.robo.cryptoportfolio.HelperClasses;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import okhttp3.RequestBody;

public class SignatureGenerator
{
    private JSONObject timestamp;
    private String signature;
    private RequestBody body;

    public String getSignature(String secret)
    {
        try
        {
            timestamp = new JSONObject().put("timestamp", System.currentTimeMillis());
            byte[] hmacSha256 = calcHmacSha256(secret.getBytes(StandardCharsets.UTF_8), timestamp.toString().getBytes(StandardCharsets.UTF_8));
            signature = String.format("%032x", new BigInteger( 1, hmacSha256));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return signature;
    }

    private byte[] calcHmacSha256(byte[] secretKey, byte[] message)
    {
        byte[] hmacSha256;
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey, mac.getAlgorithm());
            mac.init(secretKeySpec);
            hmacSha256 = mac.doFinal(message);
        } catch (Exception e) {
            throw new RuntimeException("Failed to calculate hmac-sha256", e);
        }
        return hmacSha256;
    }

    public JSONObject getTimestamp() { return timestamp; }

    public RequestBody getBody()
    {
        try
        {
            body = RequestBody.create((new JSONObject(timestamp.toString())).toString(),okhttp3.MediaType.parse("application/json; charset=utf-8"));
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        return body;
    }
}
