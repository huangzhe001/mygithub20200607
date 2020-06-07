package com.changgou.token;

import org.junit.Test;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.RsaVerifier;

/*****
 * @Author: Steven
 * @Date: 2019/7/7 13:48
 * @Description: com.changgou.token
 *  使用公钥解密令牌数据
 ****/
public class ParseJwtTest {

    /***
     * 校验令牌
     */
    @Test
    public void testParseToken(){
        //令牌
        String token = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJyb2xlcyI6IlJPTEVfVklQLFJPTEVfVVNFUiIsIm5hbWUiOiJpdGhlaW1hIiwiaWQiOiIxIn0.ediGyibe5VOaGcgjDQjpdJL5UYIz8DeQDtf-O0U-orz39LE4QS591E3LhgBkHtdP6wV6nwDJqjlaHblkRaI2lU_70VtKwX67jDUVQQkbMStRKV-QQRU5Nd4OLjgFFyzQ8kFO0r8ZAJHiSO2OFhxFJCbAfEISPAF6JLAxDvXzzsIC5Kny9uUFaj_0OSU27RbbSwX2kIZdl3W7eIC9PTNtjvEYdRkDGiB0oPtUNdH_9pOl5X7RLHTzilp1c81xn0wcWS5U36pkeuJoi2x4fuH-SfTRMtRJy6C_Prel5jx3buHptgx8XVLF4zg0bhptkfaGQSB-TtasKlOl1iIwF1S1gQ";

        //公钥
        String publickey = "-----BEGIN PUBLIC KEY-----MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAwc2vyzsYPopKyHb6hT5qCB+y7WOipXwEq6jxSowR8NHR3VNY3hoPqeuW9PwpEstezkKNDKlm3lHNjwGJHhFNVoCuqVKVO6vUrk8BMmBvtfcMRbtUMa/ji1M6/wt5bo6l1vKnq8B9RGwb5MXnB7SAuhCCctiRANGgDYdbkfIHeo1X7j7CXAHI6v6J+g9IQOctqPmr8H1d45FPJOryMb4Rb+Aosz9gB5/OtWZ1aLcS5pWZES+AiosPipx3ekj67XNFIiL+iWudCvfCKOazlRZaRY0hSaPn5j07wZ/fz2mLXW44iZX5WU5/w0STAXkKFlWQF822QgPcbJC0zMGqHtjr9wIDAQAB-----END PUBLIC KEY-----";

        //校验Jwt
        Jwt jwt = JwtHelper.decodeAndVerify(token, new RsaVerifier(publickey));

        //获取Jwt原始内容
        String claims = jwt.getClaims();
        System.out.println(claims);
        //jwt令牌
        String encoded = jwt.getEncoded();
        System.out.println(encoded);
    }
}
