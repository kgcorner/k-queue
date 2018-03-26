package com.queue;

import org.apache.log4j.Logger;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

public class HttpUtilForTests {
    private static final int SUCCESS_CODE = 200;
    private static final int ERROR_CODE = 400;
    private static final Logger LOGGER = Logger.getLogger(HttpUtilForTests.class);
    public static String sendGetRequest(String url, Map<String, String> header) throws NetworkException {
        HttpURLConnection connection = null;
        String response = null;
        URL restUrl = null;
        InputStream is = null;
        try {
            if(url.contains("\"")) {
                url = url.replace("\"","");
            }
            restUrl = new URL(url);
            connection = (HttpURLConnection) restUrl.openConnection();
            connection.setRequestMethod("GET");
            if(header != null) {
                for (String key : header.keySet()) {
                    connection.setRequestProperty(key,  header.get(key));
                }
            }
            is = new BufferedInputStream(connection.getInputStream());
            response = convertStreamToString(is);
            if(connection.getResponseCode() >= ERROR_CODE) {
                throw new NetworkException(response);
            }
        } catch (MalformedURLException e) {
            throw new NetworkException(e.getLocalizedMessage());
        } catch (IOException e) {
            is = new BufferedInputStream(connection.getErrorStream());
            try {
                response = convertStreamToString(is);
                LOGGER.error("Error Response:"+response);
            } catch (IOException e1) {
                LOGGER.error(e1.getMessage(), e1);
            }
            throw new NetworkException(e.getLocalizedMessage());
        }
        return response;
    }

    public static int sendGetResponseCode(String url, Map<String, String> header) throws NetworkException {
        HttpURLConnection connection = null;
        String response = null;
        URL restUrl = null;
        InputStream is = null;
        try {
            if(url.contains("\"")) {
                url = url.replace("\"","");
            }
            restUrl = new URL(url);
            connection = (HttpURLConnection) restUrl.openConnection();
            connection.setRequestMethod("GET");
            if(header != null) {
                for (String key : header.keySet()) {
                    connection.setRequestProperty(key,  header.get(key));
                }
            }
            return connection.getResponseCode();
        } catch (MalformedURLException e) {
            throw new NetworkException(e.getLocalizedMessage());
        } catch (IOException e) {
            is = new BufferedInputStream(connection.getErrorStream());
            try {
                response = convertStreamToString(is);
                LOGGER.error("Error Response:"+response);
            } catch (IOException e1) {
                LOGGER.error(e1.getMessage(), e1);
            }
            throw new NetworkException(e.getLocalizedMessage());
        }
    }

    public static int getPostResponseCode(String url, Map<String, String> header, String body) throws NetworkException {
        HttpURLConnection connection = null;
        String response = null;
        URL restUrl = null;
        InputStream is = null;
        OutputStream os = null;
        int code = 0;
        try {
            if(url.contains("\"")) {
                url = url.replace("\"","");
            }
            restUrl = new URL(url);
            connection = (HttpURLConnection) restUrl.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            if(header != null) {
                for (String key : header.keySet()) {
                    connection.setRequestProperty(key,  header.get(key));
                }
            }
            os = connection.getOutputStream();
            if(!Strings.isNullOrEmpty(body))
                os.write(body.getBytes());
            code = connection.getResponseCode();
        } catch (MalformedURLException e) {
            throw new NetworkException(e.getLocalizedMessage());
        } catch (IOException e) {
            is = new BufferedInputStream(connection.getErrorStream());
            try {
                response = convertStreamToString(is);
                LOGGER.error("Error Response:"+response);
            } catch (IOException e1) {
                LOGGER.error(e1.getMessage(), e1);
            }
            throw new NetworkException(e.getLocalizedMessage());
        }
        return code;
    }

    public static String sendPostRequest(String url, Map<String, String> header, String body) throws NetworkException {
        HttpURLConnection connection = null;
        String response = null;
        URL restUrl = null;
        InputStream is = null;
        OutputStream os = null;
        try {
            if(url.contains("\"")) {
                url = url.replace("\"","");
            }
            restUrl = new URL(url);
            connection = (HttpURLConnection) restUrl.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            if(header != null) {
                for (String key : header.keySet()) {
                    connection.setRequestProperty(key,  header.get(key));
                }
            }
            os = connection.getOutputStream();
            if(!Strings.isNullOrEmpty(body))
                os.write(body.getBytes());
            is = new BufferedInputStream(connection.getInputStream());
            response = convertStreamToString(is);
            if(connection.getResponseCode() >= ERROR_CODE) {
                throw new NetworkException(response);
            }
        } catch (MalformedURLException e) {
            throw new NetworkException(e.getLocalizedMessage());
        } catch (IOException e) {
            is = new BufferedInputStream(connection.getErrorStream());
            try {
                response = convertStreamToString(is);
                LOGGER.error("Error Response:"+response);
            } catch (IOException e1) {
                LOGGER.error(e1.getMessage(), e1);
            }
            throw new NetworkException(e.getLocalizedMessage());
        }
        return response;
    }

    public static Response getPostResponse(String url, Map<String, String> header, String body) throws NetworkException {
        Response r = new Response();
        HttpURLConnection connection = null;
        String response = null;
        URL restUrl = null;
        InputStream is = null;
        int status = 0;
        OutputStream os = null;
        try {
            if(url.contains("\"")) {
                url = url.replace("\"","");
            }
            restUrl = new URL(url);
            connection = (HttpURLConnection) restUrl.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            if(header != null) {
                for (String key : header.keySet()) {
                    connection.setRequestProperty(key,  header.get(key));
                }
            }
            os = connection.getOutputStream();
            if(!Strings.isNullOrEmpty(body))
                os.write(body.getBytes());
            is = new BufferedInputStream(connection.getInputStream());
            response = convertStreamToString(is);
            status = connection.getResponseCode();
            if( status >= ERROR_CODE) {
                throw new NetworkException(response);
            }
        } catch (MalformedURLException e) {
            throw new NetworkException(e.getLocalizedMessage());
        } catch (IOException e) {
            is = new BufferedInputStream(connection.getErrorStream());
            try {
                response = convertStreamToString(is);
                LOGGER.error("Error Response:"+response);
            } catch (IOException e1) {
                LOGGER.error(e1.getMessage(), e1);
            }
            throw new NetworkException(e.getLocalizedMessage());
        }
        r.setData(response);
        r.setStatus(status);
        return r;
    }

    public static Response getPutResponse(String url, Map<String, String> header, String body) throws NetworkException {
        Response r = new Response();
        HttpURLConnection connection = null;
        String response = null;
        URL restUrl = null;
        InputStream is = null;
        int status = 0;
        OutputStream os = null;
        try {
            if(url.contains("\"")) {
                url = url.replace("\"","");
            }
            restUrl = new URL(url);
            connection = (HttpURLConnection) restUrl.openConnection();
            connection.setRequestMethod("PUT");
            connection.setDoOutput(true);
            if(header != null) {
                for (String key : header.keySet()) {
                    connection.setRequestProperty(key,  header.get(key));
                }
            }
            os = connection.getOutputStream();
            if(!Strings.isNullOrEmpty(body))
                os.write(body.getBytes());
            is = new BufferedInputStream(connection.getInputStream());
            response = convertStreamToString(is);
            status = connection.getResponseCode();
            if( status >= ERROR_CODE) {
                throw new NetworkException(response);
            }
        } catch (MalformedURLException e) {
            throw new NetworkException(e.getLocalizedMessage());
        } catch (IOException e) {
            is = new BufferedInputStream(connection.getErrorStream());
            try {
                response = convertStreamToString(is);
                LOGGER.error("Error Response:"+response);
            } catch (IOException e1) {
                LOGGER.error(e1.getMessage(), e1);
            }
            throw new NetworkException(e.getLocalizedMessage());
        }
        r.setData(response);
        r.setStatus(status);
        return r;
    }

    public static String sendDeleteRequest(String url, Map<String, String> header, String body) throws NetworkException {
        HttpURLConnection connection = null;
        String response = null;
        URL restUrl = null;
        InputStream is = null;
        OutputStream os = null;
        try {
            if(url.contains("\"")) {
                url = url.replace("\"","");
            }
            restUrl = new URL(url);
            connection = (HttpURLConnection) restUrl.openConnection();
            connection.setRequestMethod("DELETE");
            connection.setDoOutput(true);
            if(header != null) {
                for (String key : header.keySet()) {
                    connection.setRequestProperty(key,  header.get(key));
                }
            }
            os = connection.getOutputStream();
            if(!Strings.isNullOrEmpty(body))
                os.write(body.getBytes());
            is = new BufferedInputStream(connection.getInputStream());
            response = convertStreamToString(is);
            if(connection.getResponseCode() >= ERROR_CODE) {
                throw new NetworkException(response);
            }
        } catch (MalformedURLException e) {
            throw new NetworkException(e.getLocalizedMessage());
        } catch (IOException e) {
            is = new BufferedInputStream(connection.getErrorStream());
            try {
                response = convertStreamToString(is);
                LOGGER.error("Error Response:"+response);
            } catch (IOException e1) {
                LOGGER.error(e1.getMessage(), e1);
            }
            throw new NetworkException(e.getLocalizedMessage());
        }
        return response;
    }

    public static int getDeleteResponseCode(String url, Map<String, String> header, String body) throws NetworkException {
        HttpURLConnection connection = null;
        String response = null;
        URL restUrl = null;
        InputStream is = null;
        OutputStream os = null;
        int code = 0;
        try {
            if(url.contains("\"")) {
                url = url.replace("\"","");
            }
            restUrl = new URL(url);
            connection = (HttpURLConnection) restUrl.openConnection();
            connection.setRequestMethod("DELETE");
            connection.setDoOutput(true);
            if(header != null) {
                for (String key : header.keySet()) {
                    connection.setRequestProperty(key,  header.get(key));
                }
            }
            os = connection.getOutputStream();
            if(!Strings.isNullOrEmpty(body))
                os.write(body.getBytes());
            is = new BufferedInputStream(connection.getInputStream());
            //response = convertStreamToString(is);
            code = connection.getResponseCode();
        } catch (MalformedURLException e) {
            throw new NetworkException(e.getLocalizedMessage());
        } catch (IOException e) {
            is = new BufferedInputStream(connection.getErrorStream());
            try {
                response = convertStreamToString(is);
                LOGGER.error("Error Response:"+response);
            } catch (IOException e1) {
                LOGGER.error(e1.getMessage(), e1);
            }
            throw new NetworkException(e.getLocalizedMessage());
        }
        return code;
    }

    private static String convertStreamToString(InputStream is) throws IOException{
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;

        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            throw e;
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                throw e;
            }
        }

        return sb.toString();
    }

    public static void downloadFile(String url, String writeTo) throws NetworkException {
        HttpURLConnection connection = null;
        URL restUrl = null;
        InputStream is = null;
        byte[] bytes = new byte[1024];
        ByteArrayOutputStream baos = null;
        FileOutputStream fos = null;
        try {
            if(url.contains("\"")) {
                url = url.replace("\"","");
            }
            restUrl = new URL(url);
            connection = (HttpURLConnection) restUrl.openConnection();
            connection.setRequestMethod("GET");

            is = new BufferedInputStream(connection.getInputStream());
            baos = new ByteArrayOutputStream();
            int n = is.read(bytes);
            do {
                baos.write(bytes);
                n = is.read(bytes);
            }while(n != -1);
            fos = new FileOutputStream(writeTo);
            fos.write(bytes);
        } catch (MalformedURLException e) {
            throw new NetworkException(e.getLocalizedMessage());
        } catch (IOException e) {
            is = new BufferedInputStream(connection.getErrorStream());
            try {
                String response = convertStreamToString(is);
                LOGGER.error("Error Response:"+response);
            } catch (IOException e1) {
                LOGGER.error(e1.getMessage(), e1);
            }
            throw new NetworkException(e.getLocalizedMessage());
        }
        finally {
            close(fos);
            close(is);
            close(baos);
        }
    }

    private static void close(Closeable streams) {
        try {
            if(streams != null)
                streams.close();
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    public static String getPostDataString(Map<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }

        return result.toString();
    }

    private static class Strings {
        public static boolean isNullOrEmpty(String body) {
            return body == null || body.trim().length() == 0;
        }
    }
}
