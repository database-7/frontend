# frontend

독서 모임 플랫폼 ReadMate의 Android 앱입니다.

## 기술 스택

| 영역 | 기술 |
|------|------|
| Language | Kotlin |
| UI | Jetpack Compose |
| Architecture | MVVM |
| DI | Hilt |
| Network | Retrofit2 + OkHttp |
| Async | Coroutines + StateFlow |
| Image | Coil |
| Push | Firebase Cloud Messaging (FCM) |
| Local Storage | SharedPreferences |

## 주요 기능

- **회원** — 회원가입, 로그인, JWT 기반 인증
- **독서 그룹** — 그룹 생성·가입·관리, 멤버 권한 분리
- **카테고리** — 그룹 내 카테고리 단위 책 교환 릴레이 관리
- **책 교환 릴레이** — 라운드별 진행 현황, 순서 확인 및 독촉 알림
- **포스트 / 댓글** — 독후감 작성·조회·삭제, 댓글 CRUD
- **북카드** — 사용자별 보유 도서 기록
- **홈 피드** — 책 관련 뉴스 피드
- **푸시 알림** — FCM을 통한 독촉 알림 수신

## 프로젝트 구조
app/src/main/java/com/example/readmate_frontend/
├── data/
│   ├── api/            # Retrofit API 인터페이스
│   ├── local/          # SharedPreferences (UserPreferences, NotificationStore)
│   ├── model/          # Request / Response / Local 데이터 모델
│   └── repository/     # Repository 계층
├── di/                 # Hilt 모듈 (NetworkModule 등)
├── service/            # FCM 서비스 (ReadMateFcmService)
├── ui/
│   ├── alarm/          # 알림 화면
│   ├── bookcard/       # 북카드 화면
│   ├── component/      # 공통 UI 컴포넌트
│   ├── group/          # 그룹 홈, 포스트, 카테고리 생성
│   ├── home/           # 홈 피드
│   ├── login/          # 로그인
│   ├── mypage/         # 마이페이지
│   ├── navigate/       # NavGraph
│   ├── register/       # 회원가입
│   ├── splash/         # 스플래시
│   └── tabbar/         # 하단 탭바
└── viewmodel/          # ViewModel 계층

## 시작하기

### 사전 요구사항

- Android Studio Hedgehog 이상
- Android SDK 26+
- `google-services.json` (Firebase 콘솔에서 발급)

### 실행

1. `app/` 폴더에 `google-services.json` 배치
2. Android Studio에서 프로젝트 열기
3. 에뮬레이터 또는 실기기에서 Run
