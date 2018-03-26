package com.bookshop.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTPClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

@Slf4j
public class FTPUtil {
//    private static final Logger log = LoggerFactory.getLogger(FTPUtil.class);

    private static String ftpIp = PropertiesUtil.getProperty("ftp.server.ip");
    private static int ftpPort = Integer.parseInt(PropertiesUtil.getProperty("ftp.port"));
    private static String ftpUsername = PropertiesUtil.getProperty("ftp.username");
    private static String ftpPassword = PropertiesUtil.getProperty("ftp.password");

    private FTPClient ftpClient;
    private String ip;
    private int port;
    private String username;
    private String password;

    private FTPUtil(String ip, int port, String username, String password) {
        this.ip = ip;
        this.port = port;
        this.username = username;
        this.password = password;
    }

    //静态方法, 批量上传
    public static boolean uploadFile(List<File> fileList) throws IOException {
        FTPUtil ftpUtil = new FTPUtil(ftpIp, ftpPort, ftpUsername, ftpPassword);
        log.info("开始连接FTP服务器");

        boolean result = ftpUtil.uploadFile("image", fileList);

        log.info("开始连接FTP服务器, 结束上传, 上传结果: {}", result);

        return result;
    }

    /**
     * 具体的上传逻辑
     *
     * @param remotePath 上传的远程路径
     * @param fileList   文件集合
     * @return 返回是否上传成功
     */
    private boolean uploadFile(String remotePath, List<File> fileList) throws IOException {
        boolean uploaded = false;
        FileInputStream fis = null;

        //连接FTP服务器
        if (connectServer(this.ip, this.port, this.username, this.password)) {
            try {
                if (ftpClient != null) {
                    ftpClient.changeWorkingDirectory(remotePath);
                    ftpClient.setBufferSize(1024);
                    ftpClient.setControlEncoding("UTF-8");
                    //设置文件为二进制形式,防止乱码
                    ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
                    //打开 本地的被动模式
                    ftpClient.enterLocalPassiveMode();
                    for (File fileItem : fileList) {
                        fis = new FileInputStream(fileItem);
                        ftpClient.storeFile(fileItem.getName(), fis);
                    }
                    uploaded = true;
                }
            } catch (IOException e) {
                log.error("上传文件异常", e);
                uploaded = false;
            } finally {
                if (fis != null) {
                    fis.close();
                }
                if (ftpClient != null) {
                    ftpClient.disconnect();
                }
            }
        }
        return uploaded;
    }

    //连接FTP服务器
    private boolean connectServer(String ip, int port, String username, String password) throws IOException {
        boolean isSuccess = false;
        ftpClient = new FTPClient();
        try {
            ftpClient.connect(ip, port);
            isSuccess = ftpClient.login(username, password);
        } catch (IOException e) {
            log.error("连接FTP服务器异常", e);
            if (ftpClient != null) {
                ftpClient.disconnect();
            }
        }

        return isSuccess;
    }


    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public FTPClient getFtpClient() {
        return ftpClient;
    }

    public void setFtpClient(FTPClient ftpClient) {
        this.ftpClient = ftpClient;
    }
}
