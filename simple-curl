# scurl(simple-curl) 만들기
* curl 프로그램이 있습니다. 이 프로그램과 유사하게 동작하는 simple-curl 을 작성합니다.
* scurl 은 다음과 같이 동작합니다.
    * URL 을 입력 인자로 받아 요청을 보내고 응답을 화면에 출력합니다.
    * 옵션으로 GET 외에 다른 method(HEAD, POST, PUT, DELETE) 로 요청할 수 있습니다.
    * POST, PUT 등의 메소드를 사용할 때엔 전송할 데이타를 지정할 수 있습니다.
    * 기본적으로는 요청헤더와 응답헤더를 출력하지 않습니다만, 옵션에 따라 출력할 수 있도록 합니다.
    * 응답의 ContentType 을 확인하여, "text/*", "application/json" 만 화면에 출력합니다.
    * POST, PUT 의 경우 -H 로 Content-Type 이 지정되지 않으면 application/x-www-form-urlencoded 을 기본 타입으로 사용합니다.
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
* GET 요청 http://httpbin.org/get
호출 예제 2
$ scurl -X GET http://httpbin.org/get
* GET 요청 http://httpbin.org/getmethod 명을 명시적으로 지정
호출 예제 3
$ scurl -v http://httpbin.org/get
* 요청,응답 헤더를 같이 출력합니다.
호출 예제 4
$ scurl -v -H "X-Custom-Header: NA" http://httpbin.org/get
* X-Custom-Header: NA 를 요청헤더에 추가로 전송합니다.
호출 예제 5
$ scurl -v -X POST -d "{ \"hello\": \"world\" }" -H "Content-Type: application/json"  http://httpbin.org/post
* POST 로 요청 전송합니다.
* POST body 는 -d 옵션에 지정합니다.
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
