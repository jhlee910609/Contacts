# Runtime Permission

- Permission은 특정한 기능을 사용한다는 것을 명확히 선언하는 역할을 함
- 안드로이드 6.0부터 퍼미션 모델을 대대로 수정함
- 이전의 경우 설치 시점에 딱 한 번 사용자의 허가를 받았지만 이제는 실행 중에 특정 기능을 사용할 때마다 사요자의 허가를 일일이 받아야 함
- 기능을 실제 사용할 때 사용자에게 이를 분명히 알리고 허락을 받은 후에만 사용할 수 있음

### 1. Manifest에서 permission 설정

```java
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="com.junhee.android.contacts">
	// <uses-permission> 태그를 이용하여 name 속성에 요구하는 퍼미션의 이름을 명시한다.
    // 여러 개가 필요할 경우, 여러 개를 명시하면 된다.
    <uses-permission android:name="android.permission.READ_CONTACTS"/>
    <application
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
```

### 2. Java 코드로 가서 권한 설정 로직 작성 

##### 2.1. 권한 부여 여부 체크 [[해당 소스코드 보기]](https://github.com/jhlee910609/Contacts/blob/master/app/src/main/java/com/junhee/android/contacts/CheckPermissionActivity.java)

- 작업을 진행하는데 있어 필요한 권한이 있는지 시스템에 묻는 작업
- ​

```java
@TargetApi(Build.VERSION_CODES.M)
    public void checkPermission() {
      	// requestcode 구분자, 상수로 정의 
      private final int REQ_PERMISSION = 100;
      
        // 1. 권한체크를 한다. > 특정권한이 있는지 시스템에 물어봄
        if (checkSelfPermission(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            run();
        } else {
            // 1.1 권한이 없다면 사용자에게 권한을 요청함
            // requestcode = 어떤 Activity에서 요청이 왔는지를 판단하는 구분자 (상수 형태로 부여)
            String permissions[] = {Manifest.permission.READ_CONTACTS};
            requestPermissions(permissions, REQ_PERMISSION); // > 권한을 요구하는 팝업이 사용자 화면에 노출됨
        }
    }

  // 2. 사용자에게 권한 부여에 사용자의 수락/거부 응답을 받는다.
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQ_PERMISSION) {
            // 3.1. 사용자가 승인을 했음
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                run();
                // 3.2. 사용자가 거절했음
            } else {
                cancel();
            }
        }
    }
```

##### 2.2 주소록에서 데이터 가져오기 [[해당 소스코드 보기]](https://github.com/jhlee910609/Contacts/blob/master/app/src/main/java/com/junhee/android/contacts/ContactActivity.java)

```java
 public List<Data> getContacts() {

        // 데이터베이스 혹은 content resolver를 통해 가져온 데이터를 적재할 데이터 저장소를 먼저 정의함
        List<Data> datas = new ArrayList<>();

        // 일종의 Database 관리툴
        // 전화번호부 내에 Content Provider를 통해 원하는 데이터를 가져올 수 있음(전제:해당 시스템 접근 권한을 갖고 있어야함)
    	ContentResolver resolver = getContentResolver();

        // 1. 데이터 컨텐츠 URI(자원의 주소, 하나의 엑셀 테이블로 이해)를 정의
        // 테이블의 이름 = Uri (엑셀 시트의 이름)
        Uri phoneUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;

        // 2. 데이터에서 가져올 컬럼명 정의 (데이터 테이블 셋팅)
        // 용어를 projection(예상)이라고 부름
        // 새로운 컬럼을 추가할 수 있음
        String[] projections = {ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER};

        // 3. Content Resolver로 쿼리를 날려서 데이터를 가져옴
        // 현재 아래에 null 값을 지닌 항목들은 일종의 조건들
        // Cursor 객체는 ArrayList처럼 배열형태로 되어 있음
        Cursor cursor = resolver.query(phoneUri, projections, null, null, null);
        // 반복문을 통해 cursor에 담겨있는 데이터를 하나씩 추출함
        if (cursor != null) {
            // 최초에 .moveToNext();(row로 이동함)를 하면 조건에 맞을 때가지 계속 프로젝션을 돔
            // 커서만 움직이는 메소드 // 데이터를 가져오는 행위는 .getasjdfldsjfl(); 해줘야함
            //
            while (cursor.moveToNext()) {

                int idIndex = cursor.getColumnIndex(projections[0]);
                int id = cursor.getInt(idIndex);

                int nameIndex = cursor.getColumnIndex(projections[1]);
                String name = cursor.getString(nameIndex);

                // 4.1. 위에 정의한 프로젝션이ㅡ 컬럼명으로 cursor에 있는 인ㅇ덱스값을 조회하고
                // 데이터 종류가 많을 때 인덱스가 꼬이거나 잘못되어 원하는 데이터를 가져오지 못할 수도 있어
                // 인덱스 가져오는 메소드를 통해 인덱스를 가져옴
                int telIndex = cursor.getColumnIndex(projections[2]);
               
                // 4.2. 해당 index를 사용해서 실제값을 가져옴
                String tel = cursor.getString(telIndex);

                // 5. 내가 설계한 데이터 클래스에 담아줌
                Data data = new Data();
                data.setId(id);
                data.setName(name);
                data.setTel(tel);

                // 6. 여러 개의 객체를 담을 수 있는 저장소에 적재함
                datas.add(data);
            }
        }
        return datas;
    }
```

