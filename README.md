# Z Card
<img width="50%" alt="image" src="https://github.com/user-attachments/assets/98665fe0-b17a-4c71-b40f-857adbbe37cc" /><br/>    

> 인터랙티브 3D 카드 제작 앱, Z Card  
> _Interactive 3D card creation app, Z Card_

---

## 📌 프로젝트 소개 / Project Overview

Z Card는 사용자가 3D 카드를 만들고 공유할 수 있는 앱입니다.  
- 유니티 씬을 통해 3D 오브젝트와 텍스트를 배치하여 카드 디자인  
- 완성된 디자인을 `.glb` 파일로 추출  
- Firebase에 업로드 후 URL 생성, 공유  

_Z Card allows users to:_  
- _Create interactive 3D cards with custom objects and text through Unity Scene_  
- _Export final designs as `.glb` files_  
- _Upload to Firebase and generate shareable URLs_  

---

## 🛠️ 기술 스택 / Technology Stack

- **3D 환경:** Unity (UAAL)  
- **웹 호스팅 및 원격 저장:** Firebase Storage  
- **익명 인증:** Firebase Auth  
- **웹 3D 뷰어:** Babylon.js
- **로컬 DB:** Room (Android)  
- **네이티브 프레임워크 (Android):**  
  - 앱 생명주기 관리  
  - 시스템 자원 접근  
  - Unity ↔ Android 데이터 브릿지  
  - 사용자 인터랙션 처리 (터치, 제스처, UI)  

- _**3D Environment:** Unity (UAAL)_  
- _**Web Hosting & Remote Storage:** Firebase Storage_  
- _**Anonymous Authentication:** Firebase Auth_  
- _**Web 3D Viewer:** Babylon.js_
- _**Local DB:** Room (Android)_  
- _**Native Framework (Android):**_  
  - _App lifecycle management_  
  - _System resource access_  
  - _Data bridge between Unity and Android_  
  - _User interaction (touch, gestures, UI components)_  

---

## ⚡ 메인 기능 / Main Features
### 1. 카드 미리보기 
- 씬 캡쳐를 통한 홈 화면 카드 썸네일 제공
- Unity Message - DB Flow - Coil로 썸네일 변경 실시간 반영

### 2. 3D 카드 디자인
- 3D 오브젝트 배치 및 커스터마이징  
- 텍스트 추가 및 레이아웃 조정  
- `.glb` 파일로 디자인 추출  

### 3. 카드 공유
- Unity에서 GLB 추출 및 Firebase Storage 업로드  
- 업로드 완료 후 URL 생성
- URL 기반 3D 카드 공유 및 Firebase Hosting을 통한 실시간 웹 뷰어 구현

### _1. Card Preview_
- _Provide home screen card thumbnails via scene capture_
- _Reflect real-time thumbnail updates utilizing Unity Message, DB Flow, and Coil

### _1. 3D Card Design_
- _Place and customize 3D objects_  
- _Add text and arrange layouts for personalized design_  
- _Export the final design as a `.glb` file_  

### _2. Card Sharing_
- _Extract the `.glb` file and Upload the file to Firebase Storage in Unity_  
- _Generate a shareable URL for others to view the card_  
- _URL-based 3D card sharing and real-time web viewing via Firebase Hosting_  
