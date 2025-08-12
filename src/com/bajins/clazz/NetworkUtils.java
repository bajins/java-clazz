package com.bajins.clazz;

import java.io.*;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.concurrent.CompletableFuture;

public class NetworkUtils {

    public static void main(String[] args) {
        /*
        Socket
         */
        try (Socket socket = new Socket("127.0.0.1", 19999);
             InputStreamReader isr = new InputStreamReader(socket.getInputStream());
             BufferedReader in = new BufferedReader(isr);
             OutputStreamWriter osw = new OutputStreamWriter(socket.getOutputStream());
             PrintWriter out = new PrintWriter(osw, true)) {
            // socket.setSoTimeout(100000); // 设置 10 秒超时

            out.println("test");

            String result = in.readLine(); // 阻塞等待服务器响应
            System.out.println(result);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 非阻塞
        try (Selector selector = Selector.open(); SocketChannel socketChannel = SocketChannel.open()) {
            socketChannel.configureBlocking(false); // 设置非阻塞
            socketChannel.connect(new InetSocketAddress("127.0.0.1", 19999));

            while (!socketChannel.finishConnect()) {
                // 等待连接建立完成
            }
            // 同时监听读写事件
            // socketChannel.register(selector,SelectionKey.OP_CONNECT | SelectionKey.OP_READ | SelectionKey.OP_WRITE);
            socketChannel.register(selector, SelectionKey.OP_WRITE);

            while (true) {
                selector.select(); // 阻塞，直到有事件发生

                Iterator<SelectionKey> selectedKeysIterator = selector.selectedKeys().iterator();

                while (selectedKeysIterator.hasNext()) {

                    SelectionKey selectionKey = selectedKeysIterator.next();

                    if (selectionKey.isConnectable()) {
                        // socketChannel.finishConnect(); // 连接成功
                        // socketChannel.register(selector, SelectionKey.OP_READ);
                    } else if (selectionKey.isReadable()) {
                        // 处理读取事件
                        SocketChannel clientChannel = (SocketChannel) selectionKey.channel();
                        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                        int bytesRead = clientChannel.read(byteBuffer);

                        if (bytesRead > 0) {
                            // 处理读取到的数据
                            String response = new String(byteBuffer.array());
                            System.out.println(response);
                        } else if (bytesRead == -1) {
                            // 关闭连接
                            clientChannel.close();
                            selectionKey.cancel();
                        }
                    } else if (selectionKey.isWritable()) {
                        try (SocketChannel clientChannel = (SocketChannel) selectionKey.channel()) {
                            ByteBuffer byteBuffer = ByteBuffer.wrap("test".getBytes());

                            while (byteBuffer.hasRemaining()) { // 确保所有数据都已写入
                                clientChannel.write(byteBuffer);
                            }
                        }
                        // 数据发送完毕
                        selectionKey.interestOps(selectionKey.interestOps() & ~SelectionKey.OP_WRITE);
                    }
                    selectedKeysIterator.remove();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        /*
        HTTP
         */
        // 使用原生Socket手动构建HTTP请求
        try (Socket socket = new Socket("httpbin.org", 80);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            // 发送HTTP请求
            out.println("GET /get HTTP/1.1");
            out.println("Host: httpbin.org");
            out.println("Connection: close");
            out.println(); // 空行表示请求头结束
            out.flush();

            // 读取响应
            String line;
            while ((line = in.readLine()) != null) {
                System.out.println(line);
            }
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // 使用HttpURLConnection构建HTTP请求
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL("https://httpbin.org/get").openConnection();
            connection.setRequestMethod("GET");
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // 使用URLConnection构建HTTP请求
        try {
            URL url = new URL("https://httpbin.org/get");
            // 打开和URL之间的连接
            URLConnection conn = url.openConnection();
            // 设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
            conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            PrintWriter out = new PrintWriter(conn.getOutputStream());
            // 发送请求参数
            out.print("");
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
            String line;
            while ((line = in.readLine()) != null) {
                System.out.println(line);
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // 使用Java 11+ HttpClient构建HTTP请求
        try (HttpClient client = HttpClient.newHttpClient();){
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://httpbin.org/get"))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request,
                    HttpResponse.BodyHandlers.ofString());
            System.out.println(response.body());

            // sendAsync() 是异步非阻塞方法，它会立即返回一个 CompletableFuture
            CompletableFuture<HttpResponse<String>> future = client.sendAsync(request, HttpResponse.BodyHandlers.ofString());

            // 当请求完成时，执行回调
            future.thenAccept(httpResponse -> {
                System.out.println("Status Code: " + httpResponse.statusCode());
                System.out.println("Response Body: " + httpResponse.body());
            }).join(); // join() 等待异步操作完成，在实际应用中你可能会构建更复杂的异步流
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
