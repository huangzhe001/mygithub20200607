package com.changgou.util;

import com.changgou.file.FastDFSFile;
import org.csource.common.MyException;
import org.csource.common.NameValuePair;
import org.csource.fastdfs.*;
import org.springframework.core.io.ClassPathResource;

import java.io.*;


/**
 * @author Alan
 * @version 1.0
 * @date 2019/11/12 20:48
 */
public class FastDFSClient {
    //配置文件读取放在静态代码块中读取，只需要读取一次
    static {
        try {
            //1、读取配置文件获取文件路径-filePath = new ClassPathResource("fdfs_client.conf").getPath()
            String path = new ClassPathResource("fds_client.conf").getPath();
            //2、加载配置文件-ClientGlobal.init(配置文件路径)
            ClientGlobal.init(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 创建TrackerServer对象
     *
     * @return
     */
    public static TrackerServer getTrackerServer() {
        TrackerServer trackerServer = null;
        try {
            //3、创建一个TrackerClient对象。直接new一个。
            TrackerClient trackerClient = new TrackerClient();
            //4、使用TrackerClient对象创建连接，getConnection获得一个TrackerServer对象。
            trackerServer = trackerClient.getConnection();
            return trackerServer;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }

    /**
     * 创建StorageClient对象
     *
     * @return
     */
    public static StorageClient getStorageClient() {
        TrackerServer trackerServer = FastDFSClient.getTrackerServer();
        //5、创建一个StorageClient对象，直接new一个，需要两个参数TrackerServer对象、null
        StorageClient storageClient = new StorageClient(trackerServer, null);
        return storageClient;
    }

    /**
     * 文件上传
     *
     * @param fastDFSFile//上传包装的文件对象
     * @return
     */
    public static String[] upload(FastDFSFile fastDFSFile) {
        String[] uploadFile = null;
        try {
            //附加参数(定义数组长度为1)
            NameValuePair[] meta_list = new NameValuePair[1];
            //添加作者信息
            meta_list[0] = new NameValuePair("author", fastDFSFile.getAuthor());
            //上传文件（文件字节数组，文件的扩展名，附加信息）
            uploadFile = getStorageClient().upload_file(fastDFSFile.getContent(), fastDFSFile.getExt(), meta_list);
            return uploadFile;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取文件的信息
     *
     * @param group_name
     * @param remote_filename
     */

    public static FileInfo getFileInfo(String group_name, String remote_filename) {
        FileInfo file_info = null;
        try {
            file_info = getStorageClient().get_file_info(group_name, remote_filename);
            return file_info;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 文件下载
     *
     * @param group_name:组名
     * @param remote_filename：远程文件名
     */
    public static InputStream downLoadFile(String group_name, String remote_filename) {
        InputStream inputStream = null;
        try {
            byte[] bytes = getStorageClient().download_file(group_name, remote_filename);
            inputStream = new ByteArrayInputStream(bytes);
            return inputStream;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 删除文件
     *
     * @param group_name
     * @param remote_filename
     */
    public static void deleteFile(String group_name, String remote_filename) {
        try {
            getStorageClient().delete_file(group_name, remote_filename);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //测试
    //http://192.168.211.132:8080/group1/M00/00/00/wKjThF3LsU2AKLUJABiaAGP4_I4673.jpg
    public static void main(String[] args) {
        // FileInfo fileInfo = getFileInfo("group1", "M00/00/00/wKjThF3LsU2AKLUJABiaAGP4_I4673.jpg");
        //System.out.println(fileInfo);
        //source_ip_addr = 192.168.211.132, file_size = 1612288,
        // create_timestamp = 2019-11-13 15:31:25, crc32 = 1677261966
        //文件下载测试
       /* try {
            InputStream inputStream = downLoadFile("group1", "M00/00/00/wKjThF3LsU2AKLUJABiaAGP4_I4673.jpg");
            byte[] buff = new byte[1024 * 8];
            OutputStream outputStream = new FileOutputStream("C:\\itheima\\a.jpg");
            while ((inputStream.read(buff)) != -1) {
                outputStream.write(buff);
            }
            //关流
            inputStream.close();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        //删除文件
        //deleteFile("group1","M00/00/00/wKjThF3LsPyABE6SAAXdbydfw8Y763.jpg");
        //获取storage组的信息
        /*StorageServer storageServer = getStorageService("group1");
        System.out.println("storage的下标" + storageServer.getStorePathIndex());
        System.out.println("storage的ip和端口：" + storageServer.getInetSocketAddress());*/
        /*ServerInfo[] infos = getStorageInfo("group1", "M00/00/00/wKjThF3LsPyABE6SAAXdbydfw8Y763.jpg");
        for (ServerInfo info : infos) {
            System.out.println(info.getIpAddr()+":"+info.getPort());
        }*/

    }

    /**
     * 根据组名获取组的信息
     *
     * @param groupName
     * @return
     */
    public static StorageServer getStorageService(String groupName) {
        StorageServer storeStorage = null;
        try {
            //创建trackerClient对象
            TrackerClient trackerClient = new TrackerClient();
            TrackerServer trackerServer = FastDFSClient.getTrackerServer();
            storeStorage = trackerClient.getStoreStorage(trackerServer, groupName);
            return storeStorage;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根据组名和文件名获取storage服务的信息
     *
     * @param group_name
     * @param remote_fileName
     */
    public static ServerInfo[] getStorageInfo(String group_name, String remote_fileName) {
        ServerInfo[] info = null;
        try {
            TrackerClient trackerClient = new TrackerClient();
            TrackerServer trackerServer = FastDFSClient.getTrackerServer();
            info = trackerClient.getFetchStorages(trackerServer, group_name, remote_fileName);
            return info;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**动态获取url地址
     * @return
     */
    public static String getTrackerUrl() {
        TrackerServer trackerServer = FastDFSClient.getTrackerServer();
        //http://192.168.211.132:8080/group1/M00/00/00/wKjThF3LsU2AKLUJABiaAGP4_I4673.jpg
        //拼接url
        String url = "http://" + trackerServer.getInetSocketAddress().getHostName()
                +":"+ClientGlobal.getG_tracker_http_port()+"/";
        return url;
    }
}
