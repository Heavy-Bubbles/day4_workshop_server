package sg.edu.iss.nus;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws NumberFormatException, IOException
    {
        // 2 args
        // 1 for file
        // 1 for port
        String fileName = args[0];
        String port = args[1];

        // Check if cookie file exists
        File cookieFile = new File(fileName);
        if (!cookieFile.exists()) {
            System.out.println("Cookie file not found!");
            System.exit(0);
        }

        // testing the cookie class
        Cookie cookie = new Cookie();
        cookie.readCookieFile(fileName);
        String myCookie = cookie.getRandomCookie();
        System.out.println(myCookie);
        String myCookie2 = cookie.getRandomCookie();
        System.out.println(myCookie2);

        //Slide 8 - establish a connection
        ServerSocket server = new ServerSocket(Integer.parseInt(port));
        Socket socket = server.accept();

        //Slide 9 - allow server to read and write over the communication channel;
        try(InputStream is = socket.getInputStream()) {
            BufferedInputStream bis = new BufferedInputStream(is);
            DataInputStream dis = new DataInputStream(bis);

            // store the data sent over from client, e.g. get cookie
            String msgReceived = "";

            try (OutputStream os = socket.getOutputStream()) {
                BufferedOutputStream bos = new BufferedOutputStream(os);
                DataOutputStream dos = new DataOutputStream(bos);

                // logic to recieve and send
                while (!msgReceived.equals("close")) {

                    // Slide 9 - recieve message
                    msgReceived = dis.readUTF();

                    if(msgReceived.equalsIgnoreCase("get-cookie")) {
                        // get a random cookie
                        String randomCookie = cookie.getRandomCookie();

                        // send the random cookie out using DataOutputStream (dos.writeUTF(XXXXX))
                        dos.writeUTF(randomCookie);
                        dos.flush();
                    } else {
                        dos.writeUTF("");
                        dos.flush();
                    }
                }
                // closes all output stream in reverse order
                dos.close();
                bos.close();
                os.close();

            } catch (EOFException ex) {
                ex.printStackTrace();
            }

            // closes all input stream in reverse order
            dis.close();
            bis.close();
            is.close();
            
        } catch (EOFException ex) {
            socket.close();
            server.close();

        }

    }
}
