import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.Arrays;

public class Test {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		
		//byte [] bytes = PacketHelp.makePacket(100);
		
		//System.out.println(PacketHelp.get_int(bytes));
		
		BufferedReader stdIn = new BufferedReader(new InputStreamReader( System.in ));
		
		byte [] fileBytes;
		
		System.out.print("Enter file name: ");
		String path = stdIn.readLine();
		
		
		File file = new File(path);
		File file2 = new File("2.txt");
		FileOutputStream fileOutputStream = null;
		FileInputStream fileInputStream = null;
		
		//System.out.println(file.exists());
		
		fileBytes = Files.readAllBytes(file.toPath());
		
		System.out.println("Size of byte array: " + fileBytes.length);
		
		/*try 
		{ 
		    fileInputStream = new FileInputStream(file);
		    fileOutputStream = new FileOutputStream(file2); 
		    fileOutputStream.write(fileBytes);
		    System.out.println("Gets here!");
		    
		}catch(Exception e)
		{
			System.err.println("Something went wrong when we tried to get the fileOutPutStream...");
		}
		finally 
		{
			fileOutputStream.close();
		}*/
		
		int divider = fileBytes.length/4;
		
		byte [] fileBytes1 = null;
		byte [] fileBytes2 = null;
		byte [] fileBytes3 = null;
		byte [] fileBytes4 = null;
		
		fileBytes1 = Arrays.copyOfRange(fileBytes, 0, divider);
		System.out.println("Size of byte array one: " + fileBytes1.length);
		fileBytes2 = Arrays.copyOfRange(fileBytes, divider, divider*2);
		System.out.println("Size of byte array two: " + fileBytes2.length);
		fileBytes3 = Arrays.copyOfRange(fileBytes, divider*2, divider*3);
		System.out.println("Size of byte array three: " + fileBytes3.length);
		fileBytes4 = Arrays.copyOfRange(fileBytes, divider*3, divider*4);
		System.out.println("Size of byte array four: " + fileBytes4.length);
		
		try
		{ 
		    fileOutputStream = new FileOutputStream(file2); 
		    fileOutputStream.write(fileBytes1);
		    fileOutputStream.write(fileBytes2);
		    fileOutputStream.write(fileBytes3);
		    fileOutputStream.write(fileBytes4);
		    System.out.println("Gets here!");
		    
		}catch(Exception e)
		{
			System.err.println("Something went wrong when we tried to get the fileOutPutStream...");
		}
		finally 
		{
			fileOutputStream.close();
		}
		
		
		
	}

}
