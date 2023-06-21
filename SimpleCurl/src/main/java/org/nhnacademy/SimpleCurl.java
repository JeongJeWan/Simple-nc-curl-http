package org.nhnacademy;

import org.apache.commons.cli.*;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SimpleCurl {
    public static URL url;
    static HttpURLConnection httpURLConnection;

    public SimpleCurl(String httpUrl) {
        try {
            url = new URL(httpUrl);
        } catch (IOException e) {
            Thread.currentThread().interrupt();
        }
    }

    public static void main(String[] args) throws IOException {
        Options options = new Options();

        options.addOption("v", false, "verbose, 요청, 응답 헤더를 출력합니다.");
        options.addOption("L", false, "서버의 응답이 30x 계열이면 다음 응답을 따라 갑니다.");
        options.addOption(Option.builder("H")
                .hasArg()
                .argName("line")
                .desc("임의의 헤더를 서버로 전송합니다.")
                .build());
        options.addOption(Option.builder("d")
                .hasArg()
                .argName("data")
                .desc("POST, PUT 등에 데이타를 전송합니다.")
                .build());
        options.addOption(Option.builder("X")
                .hasArg()
                .argName("command")
                .desc("사용할 method를 지정합니다. 지정되지 않은 경우 기본값을 GET 입니다.")
                .build());
        options.addOption(Option.builder("F")
                .hasArgs()
                .argName("name")
                .valueSeparator('=')
                .build());

        try {
            CommandLineParser parser = new DefaultParser();
            CommandLine cmd = parser.parse(options, args);

            if(args.length < 1){
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp("scurl [options] url",options);
                return;
            }

            String url =args[args.length -1];

            new SimpleCurl(url);
            setMethod("get");

            if (args.length == 1) {
                jsonPrint();
                return;
            }

            for(Option option : cmd.getOptions()) {
                if (option.getOpt().equals("H")) {
                    optionValue(option.getValue());
                }
                if (option.getOpt().equals("H")) {
                    setRequestHeader(cmd.getOptionValue("H"));
                }
                if (option.getOpt().equals("L")) {
                    System.out.println("Error");
                }
                if (option.getOpt().equals("F")) {
                    setMethod("post");
                    uploadFile(option.getValuesList().get(1));
                }
            }
            if (cmd.hasOption("d")) {
                setData(cmd.getOptionValue("d"));
            }
            if (cmd.hasOption("v")) {
                request();
                if(cmd.hasOption("H")) {
                    System.out.println(cmd.getOptionValue("H"));
                }
                response();
            }
            jsonPrint();
        } catch (ParseException e) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("scurl [options] url",options);
        }
    }

    public static void setMethod(String method) throws IOException {
        httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setDoOutput(true);
        httpURLConnection.setDoInput(true);
        httpURLConnection.setRequestMethod(method.toUpperCase());
    }

    public static void setRequestHeader(String header) {
        String[] strArr = header.split(":");
        httpURLConnection.setRequestProperty(strArr[0], strArr[1]);
    }

    public static void optionValue(String value) throws IOException {
        switch (value){
            case "POST": setMethod("post"); break;
            case "PUT": setMethod("put");break;
            case "DELETE" : setMethod("delete");break;
            default: break;
        }
    }

    public static void request() {
        System.out.println("\n\n=======request=======\n");

        System.out.println(httpURLConnection.getRequestMethod()+" "+url.getFile()+" "+url.getProtocol().toUpperCase()+"/1.1");
        System.out.println("Host: "+url.getHost());

    }

    public static void response() {

        Map<String, List<String>> headers = httpURLConnection.getHeaderFields();
        Set<String> keys = headers.keySet();

        System.out.println("\n\n=======response=======\n");

        for (String key : keys) {
            String val = httpURLConnection.getHeaderField(key);
            if (key == null) {
                System.out.println(val);
            }else {
                System.out.println(key+":  "+val);
            }
        }
    }

    public static void jsonPrint() throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
        String line;
        StringBuilder info = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            info.append(line);
            info.append('\n');
        }

        JSONObject object = new JSONObject(info.toString());
        System.out.println("\n" + object.toString(4));
    }


    public static void setData(String data) throws IOException {
        DataOutputStream writer2 = new DataOutputStream(httpURLConnection.getOutputStream());
        JSONObject object = new JSONObject(data);
        writer2.writeBytes(object.toString());
        writer2.flush();
    }

    public static void uploadFile(String path) throws IOException {
        path = path.replace("@","./");
        File file = new File(path);
        httpURLConnection.setRequestProperty("Content-Type", "multipart/form-data;charset=utf-8;boundary=^-----^");
        OutputStream output = httpURLConnection.getOutputStream();
        PrintWriter writer = new PrintWriter(new OutputStreamWriter(output, StandardCharsets.UTF_8),true);
        writer.append("--" + "^-----^").append("\r\n");
        writer.append("Content-Disposition: form-data; name=\"file\"; filename=\"").append(file.getName()).append("\"").append("\r\n");
        writer.append("Content-Type: ").append(URLConnection.guessContentTypeFromName(file.getName())).append("\r\n");
        writer.append("Content-Transfer-Encoding: binary").append("\r\n");
        writer.append("\r\n");
        writer.flush();

        FileInputStream inputStream = new FileInputStream(file);
        byte[] buffer = new byte[(int)file.length()];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            output.write(buffer, 0, bytesRead);
        }
        output.flush();
        inputStream.close();
        writer.append("\r\n");
        writer.flush();
        writer.append("--" + "^-----^" + "--").append("\r\n");
        writer.close();
    }
}
