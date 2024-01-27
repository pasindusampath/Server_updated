import com.google.gson.Gson;
import dto.TransferData;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {
    static ArrayList<Socket> sockets = new ArrayList<>();
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(6509);
        while (true){
            System.out.println("Listening ....");
            Socket accept = serverSocket.accept();//stops the thread
            System.out.println("Accepted ....");
            sockets.add(accept);

            Thread ob = new Thread(){
                @Override
                public void run() {
                    InputStream inputStream = null;
                    try {
                        inputStream = accept.getInputStream();
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                        String line = "";
                        while ((line = bufferedReader.readLine()) != null){
                            System.out.println(line);
                            TransferData transferData = new Gson().fromJson(line, TransferData.class);
                            manageTransferData(transferData,accept);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            };
            ob.start();

        }
    }

    public synchronized static void manageTransferData(TransferData object,Socket received){
        if (object.getCommand().equals("TEXT_MSG")){
            if (object.getTo().equals("ALL")){
                forwardMessageToAll(received,object.getMsg());
            }
        }

    }

    public synchronized static void forwardMessageToAll(Socket received,String msg){
        for (Socket socket : sockets) {
            if (socket.getPort()==received.getPort()){
                continue;
            }
            try {
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                out.println(msg);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
