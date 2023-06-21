# shttpd(simple-httpd) 만들기
* python 에 SimpleHTTPServer 라는 모듈이 있습니다. 유사한 프로그램을 작성합니다.
* simple-httpd 는 다음과 같이 동작합니다.
    * 포트번호를 인자로 받아 실행합니다.
    * 입력받은 포트번호로 listen 합니다.
    * 프로그램이 실행된 디렉토리를 document-root 로하는 웹서버로 동작합니다.
    * GET / 요청시에 현재 폴더 내부 목록을 html 로 응답합니다.
    * GET /file-path
    *  요청에 응답합니다.
        * 파일이 존재하면 200 OK 로 파일 컨텐츠를 응답합니다.
        * 파일이 존재하지 않으면 404 Not Found 를 응답합니다.
        * 응답시에 적절한 응답헤더를 포함해야 합니다. (Content-Type, Content-Length)
    * document-root 보다 상위 디렉토리를 요청하면 403 Forbidden 을 응답합니다.
    * 읽기 권한이 없는 파일을 요청하면 403 Forbidden 을 응답합니다.
    * multipart/form-data 파일 업로드를 구현합니다.
        * 실행한 디렉토리에 바로 저장합니다.
        * 저장 권한이 없으면 403 Forbidden을 응답합니다.
        * 같은 이름의 파일이 이미 있으면, 409 Conflict를 응답합니다.
    * multipart/form-data 파일 업로드 외의 POST 요청은 405 Method Not Allowed 를 응답합니다.
    * DELETE
    *  를 구현합니다.
        * URL 에 지정된 파일을 지울 수 있으면 지우고 204 No Content를 응답합니다.
        * URL 에 지정된 파일이 존재하지 않으면 204 No Content를 응답합니다.
        * URL 에 지정된 파일을 지울 수 없으면 403 Forbidden 을 응답합니다.
    * 요청받은 내용을 화면에 출력합니다. (access log)
        * 시간, 요청 method, 경로, 응답 코드, 응답 크기, 응답에 걸린 시간
     *  Usage
        *  Usage) shttpd port
