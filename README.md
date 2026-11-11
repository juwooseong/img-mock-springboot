# img-mock-springboot

Spring Boot 기반 **CA 이미지 서버 Mock**입니다.  
메인 WAS(`aisanction`)의 `imgRest`가 호출하는 외부 이미지 API를 로컬에서 대체합니다.

## API

| 항목 | 값 |
|------|-----|
| URL | `POST http://127.0.0.1:10917/sa/g001` |
| Request | `{"inpt_task_id":"10001021","inpt_mst_srno":"10001","swift_yn":"N"}` |
| Response (이미지) | `{"imgStr":"<Base64 JPEG>","type":"image"}` |
| Response (SWIFT 전문) | `{"imgStr":"<전문 원문 텍스트>","type":"text"}` |
| Health | `GET http://127.0.0.1:10917/health` |

### SWIFT 전문 (문서분류코드 01)

aisanctions `imgRest` 는 문서분류코드(`IMEX_HIS_CD`)가 `01` 이면 `swift_yn=Y` 를 보냅니다.
아래 중 하나면 텍스트(`type=text`)로 응답하고, 화면에서 텍스트를 이미지로 그려 표시합니다.

- `swift_yn` 이 `Y`
- `images/{task_id}.txt` 파일이 존재

전문 내용은 다음 순서로 찾습니다 (UTF-8, CRLF 는 LF 로 변환).

1. `images/{task_id}.txt`
2. `img-mock.swift-sample-file` (기본 `sample.txt`, 환경 변수 `IMG_MOCK_SWIFT_SAMPLE`)
3. 내장 샘플 전문

## 실행

```powershell
# Windows
.\tools\img-mock-springboot\start.ps1
```

```bash
# Linux / macOS
chmod +x tools/img-mock-springboot/start.sh
./tools/img-mock-springboot/start.sh
```

JAR 빌드 후 실행:

```bash
cd tools/img-mock-springboot
mvn -DskipTests package
java -jar target/img-mock-server-1.0.0.jar
```

## 메인 애플리케이션 연동

`src/main/resources/egovframework/properties/system.properties`:

```properties
img.url=http://127.0.0.1:10917/sa/g001
```

WAS 재기동 후 심사 상세 화면에서 이미지가 표시됩니다.

## 구성

| 파일 | 역할 |
|------|------|
| `ImageMockController` | `POST /sa/g001` |
| `ImageRenderService` | JPEG 생성·캐시·정적 파일 로드 |
| `application.yml` | 포트 `10917`, 이미지 디렉터리 |

## 이미지 파일

- 정적 파일: `images/{task_id}.jpg`
- 생성 캐시: `images/cache/{task_id}.jpg`
- Python Mock(`tools/img-mock-server/images`)과 동일 디렉터리 구조 사용 가능

## 환경 변수

| 변수 | 기본값 | 설명 |
|------|--------|------|
| `IMG_MOCK_PORT` | `10917` | 서버 포트 |
| `IMG_MOCK_HOST` | `127.0.0.1` | 바인드 주소 |
| `IMG_MOCK_IMAGE_DIR` | `images` | 이미지 루트 (상대경로 시 실행 디렉터리 기준) |

## Python Mock과 비교

| | Spring Boot | Python |
|---|-------------|--------|
| 경로 | `tools/img-mock-springboot` | `tools/img-mock-server` |
| 실행 | `mvn spring-boot:run` | `python server.py` |
| API | 동일 (`POST /sa/g001`) | 동일 |

둘 중 하나만 실행하면 됩니다. 동일 포트(`10917`)를 사용하므로 동시에 띄우지 마세요.
