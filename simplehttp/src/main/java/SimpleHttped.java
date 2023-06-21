import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class SimpleHttped {
    private static final Path DOCUMENT_ROOT = Paths.get(".").toAbsolutePath().normalize();

    public static void main(String[] args) throws IOException {
        int port = 8080;
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/", new RootHandler());
        server.createContext("/file", new FileHandler());
        server.setExecutor(null);
        server.start();
        System.out.println("Server started on port " + port);
    }

    static class RootHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            LocalDateTime requestTime = LocalDateTime.now();

            if (!exchange.getRequestMethod().equals("GET")) {
                exchange.sendResponseHeaders(405, -1);
                return;
            }

            if (exchange.getRequestMethod().equals("POST") && !exchange.getRequestHeaders().get("Content-Type").equals("multipart/form-data")) {
                exchange.sendResponseHeaders(405, -1);
                return;
            }

            String pathStr = exchange.getRequestURI().getPath();
            Path path = Paths.get(DOCUMENT_ROOT.toString(), pathStr).normalize();
            if (!path.startsWith(DOCUMENT_ROOT) || !Files.isDirectory(path) || !Files.isReadable(path)) {
                exchange.sendResponseHeaders(403, -1);
                return;
            }

            StringBuilder response = new StringBuilder();
            response.append("<html><head><title>Index of ").append(path).append("</title></head>");
            response.append("<body><h1>Index of ").append(path).append("</h1><ul>");

            Files.list(path).forEach(p -> {
                File file = p.toFile();
                response.append("<li><a href=\"").append(path.relativize(p)).append("\">").append(file.getName()).append("</a></li>");
            });

            response.append("</ul></body></html>");
            sendResponse(exchange, response.toString().getBytes());

            // 응답 시간 기록
            LocalDateTime responseTime = LocalDateTime.now();

            // 로그 출력
            Duration duration = Duration.between(requestTime, responseTime);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String log = String.format("[%s] %s %s %d %dms", formatter.format(responseTime), exchange.getRequestMethod(), exchange.getRequestURI(), 200, duration.toMillis());
            System.out.println(log);
        }
    }

    static class FileHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            LocalDateTime requestTime = LocalDateTime.now();

            if (!exchange.getRequestMethod().equals("GET") && !exchange.getRequestMethod().equals("POST") && !exchange.getRequestMethod().equals("DELETE")) {
                exchange.sendResponseHeaders(405, -1);
                return;
            }

            String uri = exchange.getRequestURI().toString();
            String path = uri.substring(uri.indexOf("/file") + "/file".length());
            Path filePath = Paths.get(DOCUMENT_ROOT.toString(), path.substring(1)).normalize();

            if (!filePath.startsWith(DOCUMENT_ROOT) || !Files.isRegularFile(filePath) || !Files.isReadable(filePath)) {
                exchange.sendResponseHeaders(404, -1);
                return;
            }

            if (!filePath.startsWith(DOCUMENT_ROOT) || !Files.isRegularFile(filePath) || !Files.isReadable(filePath)) {
                exchange.sendResponseHeaders(403, -1);
                return;
            }

            // POST 요청 처리
            if (exchange.getRequestMethod().equals("POST")) {
                if (!exchange.getRequestHeaders().get("Content-Type").equals("multipart/form-data")) {
                    exchange.sendResponseHeaders(405, -1);
                    return;
                }

                String filename = exchange.getRequestHeaders().getFirst("Filename");
                Path newFilePath = Paths.get(filePath.getParent().toString(), filename).normalize();
                if (Files.exists(newFilePath)) {
                    exchange.sendResponseHeaders(409, -1);
                    return;
                }
            }

            // DELETE 요청 처리
            if (exchange.getRequestMethod().equals("DELETE")) {
                try {
                    Files.delete(filePath);
                    exchange.sendResponseHeaders(204, -1);
                } catch (IOException e) {
                    exchange.sendResponseHeaders(403, -1);
                }
            } else {
                exchange.getResponseHeaders().set("Content-Length", String.valueOf(Files.size(filePath)));
                sendResponse(exchange, Files.readAllBytes(filePath));
            }


            String contentType = Files.probeContentType(filePath);
            exchange.getResponseHeaders().set("Content-Type", contentType);
            exchange.getResponseHeaders().set("Content-Length", String.valueOf(Files.size(filePath)));
            sendResponse(exchange, Files.readAllBytes(filePath));

            // 응답 시간 기록
            LocalDateTime responseTime = LocalDateTime.now();

            // 로그 출력
            Duration duration = Duration.between(requestTime, responseTime);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String log = String.format("[%s] %s %s %d %dms", formatter.format(responseTime), exchange.getRequestMethod(), exchange.getRequestURI(), 200, duration.toMillis());
            System.out.println(log);
        }
    }


    static void sendResponse(HttpExchange exchange, byte[] body) throws IOException {
        exchange.sendResponseHeaders(200, body.length);
        OutputStream os = exchange.getResponseBody();
        os.write(body);
        os.close();
    }
}
