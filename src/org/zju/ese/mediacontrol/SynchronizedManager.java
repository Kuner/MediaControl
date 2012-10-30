package org.zju.ese.mediacontrol;

import java.io.File;
import java.io.IOException;

import org.apache.commons.net.ftp.FTPFile;

public class SynchronizedManager {
	FTP ftpClient;
	String serverAddress = "192.168.1.115";
	String username = "anonymous";
	String password = "";
	
	final String localBasePath = "/sdcard/ftp/";
	
	public SynchronizedManager(String address)
	{
		serverAddress = address;
		ftpClient = new FTP(serverAddress,username,password);
	}
	
	public void startSynchronize() throws IOException
	{
		ftpClient.openConnect();
		synchronizeDirectory("/",localBasePath);
	}
	
	public void synchronizeDirectory(String remotePath,String localPath) throws IOException
	{
		File f = new File(localPath);
        // 获取根目录下所有文件
        File[] localFiles = f.listFiles();
        FTPFile[] remoteFiles;

        remoteFiles = ftpClient.listFiles(remotePath);

        for(File localFile : localFiles)
        {
        	FTPFile file = getLocalFile(localFile,remoteFiles);
        	if(file == null)
        		ftpClient.uploading(localFile, remotePath);
        	
        	else
        	{
        		if(localFile.isDirectory())
        			synchronizeDirectory(remotePath+localFile.getName()+"/",localPath+localFile.getName()+"/");
        		else
        			if(file.getSize() != localFile.length())
        				ftpClient.uploading(localFile, remotePath);
        	}
        }
	}
	
	public FTPFile getLocalFile(File localFile,FTPFile[] files)
	{
		for(FTPFile file : files)
			if(file.getName().equals(localFile.getName()) && file.isDirectory() == localFile.isDirectory())
				return file;
		return null;
	}
}
