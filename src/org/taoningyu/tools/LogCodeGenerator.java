package org.taoningyu.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;
 
 
public class LogCodeGenerator {
	public static ZipFile zipFile = null;
	private static void doDeComplie(File classFile,String rootDir,String TAG,String TEXT_PREFIX,String clazzRegx,String superNameRegx,String[] intfacRegx,String mezdRegx){
	     try {
	    		InputStream clsFIps = new FileInputStream(classFile);
				ClassReader cr = new ClassReader(clsFIps);
	            ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
	            AddLogAdapter classAdapter = new AddLogAdapter(cw,TAG,TEXT_PREFIX,clazzRegx,superNameRegx,intfacRegx,mezdRegx);
	            cr.accept(classAdapter, ClassReader.SKIP_DEBUG);
	            if(classAdapter.valid==false){
	            	return;
	            }
	            byte[] data = cw.toByteArray();
	            clsFIps.close();
	            File file = new File(classFile.getCanonicalPath());
	            FileOutputStream fout = new FileOutputStream(file);
	            fout.write(data);
	            fout.close();
	            System.out.println(rootDir.replace("\\", "/").trim()+"/");
	            System.out.println(classFile.getAbsolutePath().replace("\\", "/").replace(rootDir.replace("\\", "/").trim()+"/", "")+" success!");
	            String folder = classFile.getAbsolutePath().replace("\\", "/").replace(rootDir.replace("\\", "/").trim()+"/", "").replace(classFile.getName(), "").trim();
	            AddFilesDeflateComp(folder,classFile);
	     } catch (FileNotFoundException e) {
	            e.printStackTrace();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
		
	public static void AddFilesDeflateComp(String folder,File file) {
		try {
			// Initiate ZipFile object with the path/name of the zip file.
			// Zip file may not necessarily exist. If zip file exists, then 
			// all these files are added to the zip file. If zip file does not
			// exist, then a new zip file is created with the files mentioned
			 ArrayList<File> filesToAdd = new ArrayList<File>();
			 filesToAdd.add(file);
			// Build the list of files to be added in the array list
			// Objects of type File have to be added to the ArrayList
			
			
			// Initiate Zip Parameters which define various properties such
			// as compression method, etc. More parameters are explained in other
			// examples
			ZipParameters parameters = new ZipParameters();
			parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE); // set compression method to deflate compression
			
			// Set the compression level. This value has to be in between 0 to 9
			// Several predefined compression levels are available
			// DEFLATE_LEVEL_FASTEST - Lowest compression level but higher speed of compression
			// DEFLATE_LEVEL_FAST - Low compression level but higher speed of compression
			// DEFLATE_LEVEL_NORMAL - Optimal balance between compression level/speed
			// DEFLATE_LEVEL_MAXIMUM - High compression level with a compromise of speed
			// DEFLATE_LEVEL_ULTRA - Highest compression level but low speed
			parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL); 
			parameters.setRootFolderInZip(folder);
			// Now add files to the zip file
			// Note: To add a single file, the method addFile can be used
			// Note: If the zip file already exists and if this zip file is a split file
			// then this method throws an exception as Zip Format Specification does not 
			// allow updating split zip files
			zipFile.addFiles(filesToAdd, parameters);
		} catch (ZipException e) {
			e.printStackTrace();
		} 
		
		
	}
	
	public static void main(String[] args) throws ZipException {
		
		
		//String fileName = args[0]; 
		String TAG = "TNYtaobao";
		String TEXT_PREFIX = "TNYtaobao Test...";
		  String mezdRegx = ".*";
		  String clazzRegx = ".*TimeUtil.*";
		  String superNameRegx = ".*";
		  String[] intfacRegx = {};
		
		
		//String fileName = "E:\\BaiduYunDownload\\dex2jar-2.0\\jars\\shoujitaobao\\shoujitaobao_131.jar";//args[0];
		String fileName = "D:/jars/autoextract1.0.jar";
		zipFile = new ZipFile(fileName);
		
		String outputDir = fileName.replace(".jar", "").replace(".war", "");
		if(fileName.endsWith(".class")){
			doDeComplie(new File(fileName),outputDir,TAG,TEXT_PREFIX,clazzRegx,superNameRegx,intfacRegx,mezdRegx);
		}else if(fileName.endsWith("jar")||fileName.endsWith("war")){
			
			//Unzip.unZip(fileName,outputDir);
			ZipFile zipFile = new ZipFile(new File(fileName));
			zipFile.extractAll(outputDir);
		    doDeComplieAllFiles(outputDir,outputDir,TAG,TEXT_PREFIX,clazzRegx,superNameRegx,intfacRegx,mezdRegx);	
		}
		System.out.println("Add log in all methods done.");
   
		
		

	}

	private static void doDeComplieAllFiles(String rootDir,String outputDir,String TAG,String TEXT_PREFIX,String clazzRegx,String superNameRegx,String[] intfacRegx,String mezdRegx) {
	   
		File rootFile = new File(outputDir);
	    File[] tmpFiles = rootFile.listFiles();
	    for(File tmpFile:tmpFiles){
		if(tmpFile.isDirectory()){
			try {
				doDeComplieAllFiles(rootDir,tmpFile.getCanonicalPath(),TAG,TEXT_PREFIX,clazzRegx,superNameRegx,intfacRegx,mezdRegx);
			} catch (IOException e) {
 				e.printStackTrace();
			}
		}else if(tmpFile.getName().endsWith(".class")){
			doDeComplie(tmpFile,rootDir,TAG,TEXT_PREFIX,clazzRegx,superNameRegx,intfacRegx,mezdRegx);
		}
	}
	}
   

}