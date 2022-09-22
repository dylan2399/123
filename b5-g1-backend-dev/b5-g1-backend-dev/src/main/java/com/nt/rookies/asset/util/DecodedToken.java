package com.nt.rookies.asset.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.tomcat.util.codec.binary.Base64;

public class DecodedToken {
    public String sub;
    public String staffCode;
    public String username;
    public static DecodedToken getDecoded(String encodedToken) {
        try {
            String[] pieces = encodedToken.split("\\.");
            String b64payload = pieces[1];
            String jsonString = new String(Base64.decodeBase64(b64payload), "UTF-8");

            return new Gson().fromJson(jsonString, DecodedToken.class);
        }catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    public String toString() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(this);
    }
}
