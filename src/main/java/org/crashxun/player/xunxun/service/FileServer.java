package org.crashxun.player.xunxun.service;

import android.media.MediaMetadataRetriever;
import android.util.Log;

import org.cybergarage.http.HTTPRequest;
import org.cybergarage.http.HTTPRequestListener;
import org.cybergarage.http.HTTPResponse;
import org.cybergarage.http.HTTPServerList;
import org.cybergarage.http.HTTPStatus;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLDecoder;

import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;


public class FileServer extends Thread implements HTTPRequestListener {

    public static final String CONTENT_EXPORT_URI = "/smb";
    private HTTPServerList httpServerList = new HTTPServerList();
    // 默认的共享端口
    private int HTTPPort = 2222;
    // 绑定的ip
    private String bindIP = null;

    public String getBindIP() {
        return bindIP;
    }

    public void setBindIP(String bindIP) {
        this.bindIP = bindIP;
    }

    public HTTPServerList getHttpServerList() {
        return httpServerList;
    }

    public void setHttpServerList(HTTPServerList httpServerList) {
        this.httpServerList = httpServerList;
    }

    public int getHTTPPort() {
        return HTTPPort;
    }

    public void setHTTPPort(int hTTPPort) {
        HTTPPort = hTTPPort;
    }

    @Override
    public void run() {
        super.run();

        /**************************************************
         * 
         * 创建http服务器，接收共享请求
         * 
         *************************************************/
        // 重试次数
        int retryCnt = 0;
        // 获取端口 2222
        int bindPort = getHTTPPort();

        HTTPServerList hsl = getHttpServerList();
        while (hsl.open(bindPort) == false) {
            retryCnt++;
            // 重试次数大于服务器重试次数时返回
            if (100 < retryCnt) {
                return;
            }
            setHTTPPort(bindPort + 1);
            bindPort = getHTTPPort();
        }
        // 给集合中的每个HTTPServer对象添加HTTPRequestListener对象
        hsl.addRequestListener(this);
        // 调用集合中所有HTTPServer的start方法
        hsl.start();

//        PlayFileService.IP = hsl.getHTTPServer(0).getBindAddress();
        PlayFileService.IP = MacAddressGetter.getLocalIpAddress();
        PlayFileService.PORT = hsl.getHTTPServer(0).getBindPort();

    }

    public void setmNtlmPasswordAuthentication(NtlmPasswordAuthentication mNtlmPasswordAuthentication) {
        this.mNtlmPasswordAuthentication = mNtlmPasswordAuthentication;
    }

    NtlmPasswordAuthentication mNtlmPasswordAuthentication;

    @Override
    public void httpRequestRecieved(HTTPRequest httpReq) {

        String uri = httpReq.getURI();
        httpReq.print();
        System.out.println("uri*****" + uri);

        if (uri.startsWith(CONTENT_EXPORT_URI) == false) {
            httpReq.returnBadRequest();
            return;
        }
        try {
            uri = URLDecoder.decode(uri, "UTF-8");
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }
        System.out.println("uri=====" + uri);
        if (uri.length() < 6) {
            return;
        }
        // 截取文件的信息
        String filePaths = "smb:" + uri.substring(5);

        System.out.println("filePaths=" + filePaths);
        // 判断uri中是否包含参数
        int indexOf = filePaths.indexOf("&");

        if (indexOf != -1) {
            filePaths = filePaths.substring(0, indexOf);
        }

        String startByte = httpReq.getHeader("Range").getValue().replaceAll("bytes=","").replaceAll("-","");
        Log.d("_xunxun","startByte:"+startByte);
        try {
            SmbFile file = new SmbFile(filePaths,
                    mNtlmPasswordAuthentication);
            // 获取文件的大小
            long contentLen = file.length();
            // 获取文件类型
            String contentType = getMimeType(file.getName());
            System.out.println("contentType=====" + contentType);
            // 获取文文件流
            InputStream contentIn = file.getInputStream();

            if (contentLen <= 0 || contentType.length() <= 0
                    || contentIn == null) {
                httpReq.returnBadRequest();
                return;
            }

            HTTPResponse httpRes = new HTTPResponse();
            httpRes.setContentType(contentType);
            httpRes.setStatusCode(HTTPStatus.OK);
            httpRes.setContentLength(contentLen);
            httpRes.setContentInputStream(contentIn);

            httpReq.post(httpRes);

            contentIn.close();
        } catch (MalformedURLException e) {
            // httpReq.returnBadRequest();
            return;
        } catch (SmbException e) {
            // httpReq.returnBadRequest();
            return;
        } catch (IOException e) {
            // httpReq.returnBadRequest();
            return;
        }
    }

    public static String getMimeType(String filePath) {
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        String mime = "text/plain";
        if (filePath != null) {
            try {
                mmr.setDataSource(filePath);
                mime = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE);
            } catch (IllegalStateException e) {
                return mime;
            } catch (IllegalArgumentException e) {
                return mime;
            } catch (RuntimeException e) {
                return mime;
            }
        }
        return mime;
    }
}