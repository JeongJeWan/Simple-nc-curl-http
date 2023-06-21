# Simple-nc-curl-http
간단한 nc, curl, http 만들기

# snc(simple-nc) 만들기
* nc (netcat) 프로그램이 있습니다. 이 프로그램과 유사하게 동작하는 simple-nc 를 작성합니다.
* nc 는 다음과 같이 동작합니다.
    * 클라이언트 모드
        * 입력인자로 받은 서버에 TCP 연결을 합니다.
        * 사용자로 부터의 입력(STDIN)을 서버로 전송합니다.
        * 서버로 부터 받은 데이타는 표준 출력(STDOUT) 합니다.
        * ctrl-c 로 프로그램을 종료합니다.
    * 서버 모드
        * 입력인자로 listen 포트를 입력 받습니다.
        * 해당 포트로 TCP 서버를 실행하여 접속을 기다립니다.
        * 클라이언트가 접속하여 데이타를 보내면, 표준 출력합니다.
        * 사용자의 입력(STDIN) 을 클라이언트로 전송합니다.
        * ctrl-c 로 프로그램을 종료합니다.
Usage
Usage: snc [option] [hostname] [port]
Options:
-l   <port>     서버 모드로 동작, 입력 받은 포트로 listen
사용 예제1
$ snc -l 12345
* 서버로 동작.
* 사용자 입력을 받아 클라이언트 전송합니다.
* 클라이언트로부터의 응답을 표준 출력 합니다.
* ctrl-c 로 프로그램 종료합니다.
* 한 커넥션만 처리합니다.
사용 예제2
$ snc 127.0.0.1 12345
* 클라이언트로 동작
* 사용자 입력을 받아 서버로 전송합니다.
* 서버로 부터의 응답을 표준 출력 합니다.
* hostname 으로는 fqdn,ip 모두 사용할 수 있습니다.
* ctrl-c 로 프로그램 종료합니다.

# scurl(simple-curl) 만들기
* curl 프로그램이 있습니다. 이 프로그램과 유사하게 동작하는 simple-curl 을 작성합니다.
* scurl 은 다음과 같이 동작합니다.
    * URL 을 입력 인자로 받아 요청을 보내고 응답을 화면에 출력합니다.
    * 옵션으로 GET 외에 다른 method(HEAD, POST, PUT, DELETE) 로 요청할 수 있습니다.
    * POST, PUT 등의 메소드를 사용할 때엔 전송할 데이타를 지정할 수 있습니다.
    * 기본적으로는 요청헤더와 응답헤더를 출력하지 않습니다만, 옵션에 따라 출력할 수 있도록 합니다.
    * 응답의 ContentType 을 확인하여, "text/*", "application/json" 만 화면에 출력합니다.
    * POST, PUT 의 경우 -H 로 Content-Type 이 지정되지 않으면 application/x-www-form-urlencoded 을 기본 타입으로 사용합니다.
Usage
Usage: scurl [options] url
Options:
-v                 verbose, 요청, 응답 헤더를 출력합니다.
-H <line>          임의의 헤더를 서버로 전송합니다.
-d <data>          POST, PUT 등에 데이타를 전송합니다.
-X <command>       사용할 method 를 지정합니다. 지정되지 않은 경우 기본값은 GET 입니다.
-L                 서버의 응딥이 30x 계열이면 다음 응답을 따라 갑니다.
-F <name=content>  multipart/form-data 를 구성하여 전송합니다. content 부분에 @filename 을 사용할 수 있습니다.
호출 예제 1
$ scurl http://httpbin.org/get
* GET 요청 http://httpbin.org/get
호출 예제 2
$ scurl -X GET http://httpbin.org/get
* GET 요청 http://httpbin.org/getmethod 명을 명시적으로 지정
호출 예제 3
$ scurl -v http://httpbin.org/get
* 요청,응답 헤더를 같이 출력합니다.
호출 예제 4
$ scurl -v -H "X-Custom-Header: NA" http://httpbin.org/get
* X-Custom-Header: NA 를 요청헤더에 추가로 전송합니다.
호출 예제 5
$ scurl -v -X POST -d "{ \"hello\": \"world\" }" -H "Content-Type: application/json"  http://httpbin.org/post
* POST 로 요청 전송합니다.
* POST body 는 -d 옵션에 지정합니다.
호출 예제 6
$ scurl -v -L http://httpbin.org/status/302
* 302 응답을 받고, 응답에 지정된 Location 으로 따라갑니다.
    * 지정된 Location 에 다시 요청했을 때 다시, 301, 302, 307, 308 응답이 나오면 다시 따라갑니다.
    * 최대 5번까지 따라갑니다.
    * 6번째 redirection 메세지를 만난경우 에러메세지를 출력합니다.
호출 예제 7
$ scurl -F "upload=@file_path" http://httpbin.org/post
* file_path
*  에 지정된 파일을 multipart/form-data
*  로 전송합니다.
    * multipart/form-data
 
# shttpd(simple-httpd) 만들기
* python 에 SimpleHTTPServer 라는 모듈이 있습니다. 유사한 프로그램을 작성합니다.
* simple-httpd 는 다음과 같이 동작합니다.
    * 포트번호를 인자로 받아 실행합니다.
    * 입력받은 포트번호로 listen 합니다.
    * 프로그램이 실행된 디렉토리를 document-root 로하는 웹서버로 동작합니다.
    * GET / 요청시에 현재 폴더 내부 목록을 html 로 응답합니다.
    * GET /file-path
    *  요청에 응답합니다.
        * 파일이 존재하면 200 OK 로 파일 컨텐츠를 응답합니다.
        * 파일이 존재하지 않으면 404 Not Found 를 응답합니다.
        * 응답시에 적절한 응답헤더를 포함해야 합니다. (Content-Type, Content-Length)
    * document-root 보다 상위 디렉토리를 요청하면 403 Forbidden 을 응답합니다.
    * 읽기 권한이 없는 파일을 요청하면 403 Forbidden 을 응답합니다.
    * multipart/form-data 파일 업로드를 구현합니다.
        * 실행한 디렉토리에 바로 저장합니다.
        * 저장 권한이 없으면 403 Forbidden을 응답합니다.
        * 같은 이름의 파일이 이미 있으면, 409 Conflict를 응답합니다.
    * multipart/form-data 파일 업로드 외의 POST 요청은 405 Method Not Allowed 를 응답합니다.
    * DELETE
    *  를 구현합니다.
        * URL 에 지정된 파일을 지울 수 있으면 지우고 204 No Content를 응답합니다.
        * URL 에 지정된 파일이 존재하지 않으면 204 No Content를 응답합니다.
        * URL 에 지정된 파일을 지울 수 없으면 403 Forbidden 을 응답합니다.
    * 요청받은 내용을 화면에 출력합니다. (access log)
        * 시간, 요청 method, 경로, 응답 코드, 응답 크기, 응답에 걸린 시간
Usage
Usage) shttpd port


