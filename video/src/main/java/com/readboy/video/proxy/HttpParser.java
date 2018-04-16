package com.readboy.video.proxy;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import com.readboy.video.proxy.Config.ProxyRequest;
import com.readboy.video.proxy.Config.ProxyResponse;

public class HttpParser {
    final static public String TAG = "HttpParser";
    final static private String RANGE_PARAMS = "Range: bytes=";
    final static private String RANGE_PARAMS_0 = "Range: bytes=0-";
    final static private String CONTENT_RANGE_PARAMS = "Content-Range: bytes ";
    final static private String CONTENT_LENGTH_PARAMS = "Content-Length: ";

//    private static SimpleDateFormat greenwichDate = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss 'GMT'", Locale.US);
//    static {
//        greenwichDate.setTimeZone(TimeZone.getTimeZone("GMT"));
//    }

    private static final int HEADER_BUFFER_LENGTH_MAX = 100 * 1024;
    private byte[] headerBuffer = new byte[HEADER_BUFFER_LENGTH_MAX];
    private int headerBufferLength = 0;

    public void clearHttpBody() {
        headerBuffer = new byte[HEADER_BUFFER_LENGTH_MAX];
        headerBufferLength = 0;
    }

    public byte[] getRequestBody(byte[] source, int length) {
        List<byte[]> httpRequest = getHttpBody(Config.HTTP_REQUEST_BEGIN,
                Config.HTTP_BODY_END, source, length);
        if (httpRequest.size() > 0) {
            return httpRequest.get(0);
        }
        return null;
    }

    public String modifyHost(String requestStr, String url) {
        String str;
        String urlStrs[];

        urlStrs = url.split("://", 2);
        str = urlStrs[urlStrs.length - 1];
        urlStrs = str.split("/", 2);

        str = Utils.getSubString(requestStr, "Host:", "\r\n");
        if (str == null) {
            return null;
        }

        String result = requestStr.replaceAll(str, " " + urlStrs[0]);
        str = Utils.getSubString(result, "GET ", " HTTP/1.1");
        if (urlStrs.length > 1) {
            result = result.replaceFirst(str, " /" + urlStrs[1]);
        } else {
            result = result.replaceFirst(str, " / ");
        }
        return result;
    }

    public String makeProxyRequest(String url, long position1, long position2) {
        String request = "GET /upload/test.mp4 HTTP/1.1" + "\r\n"
                + "User-Agent: stagefright/1.2 (Linux;Android 5.1.1)" + "\r\n"
                + "Host: test.hgclass.com" + "\r\n"
                + "Connection: close" + "\r\n"
                + "Accept-Encoding: gzip" + "\r\n"
                + "Range: bytes=0-" + "\r\n" + "\r\n";

        request = modifyHost(request, url);
        request = modifyRequestRange(request, position1, position2);

        return request;
    }

    public String makeProxyRequestAlive(String url, long position1, long position2) {
        String request = "GET /upload/test.mp4 HTTP/1.1" + "\r\n"
                + "User-Agent: stagefright/1.2 (Linux;Android 4.4.4)" + "\r\n"
                + "Host: test.hgclass.com" + "\r\n"
                + "Connection: keep-alive" + "\r\n"
                + "Accept-Encoding: gzip,deflate" + "\r\n"
                + "Range: bytes=0-" + "\r\n" + "\r\n";

        request = modifyHost(request, url);
        request = modifyRequestRange(request, position1, position2);

        return request;
    }

    public int getProxyRequestRange(byte[] request, int length) {
        String str;
        str = new String(request, 0, length);
        if (str.contains(RANGE_PARAMS)) {
            str = Utils.getSubString(str, RANGE_PARAMS, "-");
            return Integer.valueOf(str);
        }

        return 0;
    }

    public ProxyRequest modifyProxyRequest(byte[] request, int length, String url) {
        ProxyRequest result = new ProxyRequest();

        result._body = new String(request, 0, length);
        result._body = modifyHost(result._body, url);
        if (result._body.contains(RANGE_PARAMS) == false) {
            result._body = result._body.replace(Config.HTTP_BODY_END,
                    "\r\n" + RANGE_PARAMS_0 + Config.HTTP_BODY_END);
        }

        String rangePosition = Utils.getSubString(result._body, RANGE_PARAMS, "-");
        result._rangePosition = Integer.valueOf(rangePosition);

        return result;
    }

    public int getHTTPStatusCode(byte[] source, int length) {
        int status = 0;
        String statusStr = null;
        if (source != null) {
            List<byte[]> httpResponse = getHttpBody(Config.HTTP_RESPONSE_BEGIN,
                    Config.HTTP_BODY_END,
                    source,
                    length);

            if (httpResponse.size() > 0) {
                byte[] body = httpResponse.get(0);
                String resultBody = null;
                if (body != null) {
                    resultBody = new String(body);
//					Log.e(TAG, "getHTTPStatusCode resultBody: "+resultBody);
                    statusStr = resultBody.substring(9, 12);
                }
            }
            //TODO: 403错误怎么处理。
            Log.e(TAG, "statusStr:" + statusStr);
            //206
        }
        if (statusStr != null) {
            status = Integer.valueOf(statusStr);
        }
        Log.i(TAG, " getHTTPStatusCode status=" + status);
        return status;
    }

    public ProxyResponse getProxyResponse(byte[] source, int length) {
        List<byte[]> httpResponse = getHttpBody(Config.HTTP_RESPONSE_BEGIN,
                Config.HTTP_BODY_END,
                source,
                length);

        if (httpResponse.size() == 0) {
            return null;
        }

        ProxyResponse result = new ProxyResponse();
        result._body = httpResponse.get(0);
        String text = new String(result._body);
        if (httpResponse.size() == 2) {
            result._other = httpResponse.get(1);
        }

        try {
            String range = Utils.getSubString(text, CONTENT_RANGE_PARAMS, "\r\n");
            int endIndex = range.indexOf("-");
            if (endIndex != -1) {
                String currentPosition = range.substring(0, range.indexOf("-"));
//                Log.e(TAG, "currentPosition=" + currentPosition);
                result._currentPosition = Integer.valueOf(currentPosition);

                String duration = range.substring(range.indexOf("/") + 1);
//			Log.e(TAG, "duration="+duration);
                result._duration = Integer.valueOf(duration);
            } else {
                Log.e(TAG, "getProxyResponse: range = " + range);
            }
        } catch (Exception ex) {
//            Log.e(TAG, Utils.getExceptionMessage(ex));
            Log.e(TAG, "getProxyResponse: e : ", ex);
        }
        return result;
    }

    public String makeProxyResponseString(long range, long totalsize) {
        String response = "HTTP/1.1 206 Partial Content" + "\r\n"
                + "Server: nginx/1.6.0" + "\r\n"
                + "Date: Sat, 20 Aug 2016 07:22:04 GMT" + "\r\n"
                + "Content-Type: video/mp4" + "\r\n"
                + "Content-Length: 90171071" + "\r\n"
                + "Last-Modified: Mon, 25 Apr 2016 00:49:07 GMT" + "\r\n"
                + "Connection: close" + "\r\n"
                + "ETag: \"571d6983-55fe6bf\"" + "\r\n"
                + "Content-Range: bytes 0-90171070/90171071" + "\r\n" + "\r\n";

        String str = Utils.getSubString(response, CONTENT_RANGE_PARAMS, "\r\n");
        String str1 = range + "-" + (totalsize - 1) + "/" + totalsize;
        String newResponse = response.replaceAll(str, str1);
        str = Utils.getSubString(response, CONTENT_LENGTH_PARAMS, "\r\n");
        str1 = "" + (totalsize - range);
        return newResponse.replaceAll(str, str1);
    }

    public String makeProxyResponseStringAlive(long range, long totalsize) {
        SimpleDateFormat greenwichDate = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss 'GMT'", Locale.US);
        // 时区设为格林尼治
        greenwichDate.setTimeZone(TimeZone.getTimeZone("GMT"));
        Calendar calendar = Calendar.getInstance();
        String response = "HTTP/1.1 206 Partial Content" + "\r\n"
                + "Server: nginx/1.6.0" + "\r\n"
                + "Date: Sat, 20 Aug 2016 07:22:04 GMT" + "\r\n"
//                + "Date: " + greenwichDate.format(calendar.getTime()) + "\r\n"
                + "Content-Type: video/mp4" + "\r\n"
//                + "Content-Type: application/octet-stream" + "\r\n"
                + "Content-Length: 90171071" + "\r\n"
                + "Last-Modified: Mon, 25 Apr 2016 00:49:07 GMT" + "\r\n"
                + "Connection: keep-alive" + "\r\n"
//                + "Accept-Ranges: bytes" + "\r\n"
                + "ETag: \"571d6983-55fe6bf\"" + "\r\n"
                + "Content-Range: bytes 0-90171070/90171071" + "\r\n" + "\r\n";

        String str = Utils.getSubString(response, CONTENT_RANGE_PARAMS, "\r\n");
        String str1 = range + "-" + (totalsize - 1) + "/" + totalsize;
        String newResponse = response.replaceAll(str, str1);
        str = Utils.getSubString(response, CONTENT_LENGTH_PARAMS, "\r\n");
        str1 = "" + (totalsize - range);
        return newResponse.replaceAll(str, str1);
    }

    public ProxyResponse makeProxyResponse(long range, long totalsize) {
        String response = "HTTP/1.1 206 Partial Content" + "\r\n"
                + "Server: nginx/1.6.0" + "\r\n"
                + "Date: Sat, 20 Aug 2016 07:22:04 GMT" + "\r\n"
                + "Content-Type: video/mp4" + "\r\n"
                + "Content-Length: 90171071" + "\r\n"
                + "Last-Modified: Mon, 25 Apr 2016 00:49:07 GMT" + "\r\n"
                + "Connection: close" + "\r\n"
                + "ETag: \"571d6983-55fe6bf\"" + "\r\n"
                + "Content-Range: bytes 0-90171070/90171071" + "\r\n" + "\r\n";

        String str = Utils.getSubString(response, CONTENT_RANGE_PARAMS, "\r\n");
        String str1 = range + "-" + (totalsize - 1) + "/" + totalsize;
        String newResponse = response.replaceAll(str, str1);
        str = Utils.getSubString(response, CONTENT_LENGTH_PARAMS, "\r\n");
        str1 = "" + (totalsize - range);
        newResponse = newResponse.replaceAll(str, str1);

        ProxyResponse result = new ProxyResponse();
        result._body = newResponse.getBytes();
        String text = new String(result._body);
        try {
            String currentPosition = Utils.getSubString(text, CONTENT_RANGE_PARAMS, "-");
            result._currentPosition = range;

            String startStr = CONTENT_RANGE_PARAMS + currentPosition + "-";
            String duration = Utils.getSubString(text, startStr, "/");
            result._duration = Integer.valueOf(duration);
        } catch (Exception ex) {
            Log.e(TAG, Utils.getExceptionMessage(ex));
        }
        return result;
    }

    public String modifyRequestRange(String requestStr, long position) {
        String str = Utils.getSubString(requestStr, RANGE_PARAMS, "\r\n");
        String result = requestStr.replaceAll(str, position + "-");
        return result;
    }

    public String modifyRequestRange2(String requestStr, long position1, long position2) {
        String str = Utils.getSubString(requestStr, RANGE_PARAMS, "\r\n");
        return requestStr.replaceAll(str, position1 + "-" + position2);
    }

    public String modifyRequestRange(String requestStr, long position1, long position2) {
        String str = Utils.getSubStringContainStartString(requestStr, RANGE_PARAMS, "\r\n");
        return requestStr.replaceAll(str, RANGE_PARAMS + position1 + "-" + position2);
    }

    public String modifyResponseLength(String responseStr, long length) {
        String str = Utils.getSubString(responseStr, CONTENT_LENGTH_PARAMS, "\r\n");
        String result = responseStr.replaceAll(str, "" + length);
        return result;
    }

    public String modifyResponseRange(String responseStr, long position, long length) {
        String str = Utils.getSubString(responseStr, CONTENT_RANGE_PARAMS, "\r\n");
        String result = responseStr.replaceAll(str, position + "-" + (length - 1) + "/" + length);
        result = modifyResponseLength(result, length - position);
        return result;
    }

    private List<byte[]> getHttpBody(String beginStr, String endStr, byte[] source, int length) {
        if ((headerBufferLength + length) >= headerBuffer.length) {
            clearHttpBody();
        }
        if (length > headerBuffer.length) {
            length = headerBuffer.length;
        }
        System.arraycopy(source, 0, headerBuffer, headerBufferLength, length);
        headerBufferLength += length;

        List<byte[]> result = new ArrayList<byte[]>();
        String responseStr = new String(headerBuffer);
        if (responseStr.contains(beginStr)
                && responseStr.contains(endStr)) {

            int startIndex = responseStr.indexOf(beginStr, 0);
            int endIndex = responseStr.indexOf(endStr, startIndex);
            endIndex += endStr.length();

            byte[] header = new byte[endIndex - startIndex];
            System.arraycopy(headerBuffer, startIndex, header, 0, header.length);
            result.add(header);

            if (headerBufferLength > header.length) {
                byte[] other = new byte[headerBufferLength - header.length];
                System.arraycopy(headerBuffer, header.length, other, 0, other.length);
                result.add(other);
            }
            clearHttpBody();
        }

        return result;
    }

}
