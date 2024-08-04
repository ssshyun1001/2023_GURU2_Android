# 🚶‍♀️ 안전한 귀갓길을 위해 : 지켜주길 🚶‍♀️ 
![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![Android Studio](https://img.shields.io/badge/android%20studio-346ac1?style=for-the-badge&logo=android%20studio&logoColor=white)
![Kotlin](https://img.shields.io/badge/kotlin-%237F52FF.svg?style=for-the-badge&logo=kotlin&logoColor=white)

<br>

본 안드로이드 어플리케이션 프로젝트는 SWU GURU2 안드로이드 분반의 5조, 데사쥬쥬팀의 해커톤 프로젝트입니다.

<br>

![Video Label](http://img.youtube.com/vi/VS67vxgCh0k/0.jpg)

- [어플리케이션 유튜브 링크](https://youtu.be/VS67vxgCh0k)

<br>

**<< 귀갓길에 안심을 더하다, 지켜주길 >>**

: 밤길을 홀로 걸으며 *무섭다고* 느끼신 경험, 다들 한 번쯤을 있으셨을 텐데요. 

: 저희는 그러한 경험을 바탕으로, 사용자가 밤에 귀가하는 길이 조금 더 “안전하길“ 바라는 마음으로 지도, SOS, 사이렌과 같은 안전을 위한 기능이 구현된 안심 귀가 서비스를 기획하였습니다.

<br>

## How to build

1. 

<br>
<br>

## Credits

: SWU DS 23 _ 공다원, 백채원, 윤서현, 장예서(팀장)

<br>
<br>

## Changelog
- <code>2024/08/04</code>

  SOS 상태(On/Off) 액티비티 간 공유

  편의점 마커 생성 및 버튼 연동

  메인 버튼 연동 최종 완료

  사이렌 테스트 버튼 오류 수정

  코드 정리 및 주석 추가

  튜토리얼 png 파일 업로드 완료!!!

<br>

- <code>2024/08/03</code>

  Main에 Map 코드 통합 완료

  cctv 히트맵 구현 (범위 수정 : 서울 전체 > 내 위치 반경 5km)

  편의점 마커 구현 시도

  사이렌 소리 파일 변경

  json empty 예외처리 추가

  메세지 전송 주기 오류 수정

  불필요 코드 제거

  전화번호 입력란 제한 조건 추가 (숫자, 11자리)

<br>

- <code>2024/08/02</code>

  UI 최종 수정 및 폰트 변경

  Map 기능 Main으로 통합

  설정 id 전달 오류 수정

  지도 시작 오류 수정, Settings 전화번호 추가시 예외 처리 추가

  사이렌 소리 변경 오류 수정 (파일 업데이트 과정 충돌)

  커스텀 SOS 메세지 설정 오류 수정 (파일 업데이트 과정 충돌)

<br>

- <code>2024/08/01</code>

  cctv, 편의점 위치 지도 API 추가

  지도 실행시 현재 위치에서 시작하도록 기능 구현

  Call 화면에서 현재 통화 시간 타이머 기능

  지도 API 서비스 키 변경

  커스텀 SOS 메세지 기능 오류 해결

  Call 화면 기능 통합, 사이렌 미출력 오류 해결

  Settings 화면 저장 버튼 오류 수정, 사이렌 버튼 출력 오류 수정, Call 종료 버튼 오류 수정

  불필요해진 일부 파일 제거 및 정리

  편의점 위치 로드 오류 수정

  버튼의 불필요한 딜레이 제거

<br>

- <code>2024/07/31</code>

  전체 UI 개선 및 테마 통일

  SOS 메세지 설정 주기마다 전송, 커스텀 SOS 메세지 일부 구현 시도, SOS 버튼 기능 연결 완료

  Settings id 연결 오류 수정

  메세지 설정 및 로그인 상태 유지 저장

  커스텀 SOS 메세지 구현 (+ 상단 commit과 병합)

  자동 로그인 기능, 로그아웃 버튼 기능 구현

  회원가입 시 아이디 중복확인 필수 기능 추가

<br>

- <code>2024/07/30</code>

  회원가입 창 뒤로가기 버튼

  Settings에 로그아웃 버튼 추가

  cctv 지도 API 추가 완료

  사이렌 기능 복구, 메인·설정·사이렌러닝 화면 UI 및 디자인(테마·컬러) 변경, 전반적인 SOS 기능 구현 완료

  일부 UI 개선 및 테마 통일

  Settings에 id 연결, 현재 로그인한 id만 조회되도록 수정

  로그인 조건 미충족시 경고 표시 기능 구현

<br>

- <code>2024/07/29</code>

  옵션 메뉴 오류 fix, 메뉴 아이템 id 일부 변경, 튜토리얼 화면 구현

  로그인 >> 메인 화면 전환 오류 수정

  회원가입 DB 구성, 회원가입 xml 레이아웃 및 기능 구현

  SOS 팝업 화면, SOS 실행 변수, 실행에 따른 icon 변화 구현

  Contact DB 구현 완료

  사용자 정보 DB 생성 완료

<br>

- <code>2024/07/28</code>

  사이렌 소리 추가 완료

  아빠 전화 녹음 추가 및 Call 화면 구현

  Call 화면 레이아웃 세분화, 제작 png 및 svg 소스로 일부 이미지 변경

  MapActivity 기능 복구 및 통합, Settings 기능 수정

  Login의 로그인 버튼, ReciveCall 화면 버튼으로 화면 전환 기능 구현

<br>

- <code>2024/07/27</code>

  불필요해진 일부 파일 제거 및 정리

  Login 화면 및 통화 수신 화면 추가

  Siren 페이지 일부 구현 (Settings의 라디오 버튼과 미연결)

<br>

- <code>2024/07/26</code>

  X

<br>

- <code>2024/07/25</code>

  Settings 기초 기능 구현 완료

  오류 수정 및 Siren/SOS 팝업 설정

  Settings 옵션 메뉴 아이템 삭제

<br>

- <code>2024/07/24</code>

  MainActivity 내부 권한 설정 코드 수정

  지도 클래스 추가 (xml 수정)

  메뉴 및 레이아웃 구현 진행

  컬러 추가

  SOS 이력 화면 및 메뉴 제거

  홈·설정 화면 팝업 메뉴 버튼 및 화면 전환 기능 추가

  경찰서 DB 생성 클래스
    
<br>

- <code>2024/07/23</code>

  Call, Soshistory, Settings 화면 초기 구현
  
  Settings 화면 업데이트 및 SendMessage 클래스 초기 구현

  MainActivity 초기 구현, Soshistory 화면 구현, menu 설정 구현

  오타/오류 수정, 긴급 연락처 추가/삭제, 사이렌 선택, 위치기록주기 설정

  gradle 내부 mediarouter implementation 오류 수정 및 일부 라이브러리 버전업

<br>

- <code>2024/07/22</code>

  프로젝트 생성

  권한 설정 및 사이렌 기능 클래스 구현
