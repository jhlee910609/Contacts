package com.junhee.android.contacts;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.junhee.android.contacts.domain.Data;

import java.util.ArrayList;
import java.util.List;

// 카카오톡 주소록 동기화 기능도 이런 로직으로 함
// 데이터가 있는 애들만 컨텐트 프로바이더가 존재함
// 컨텐트 프로바이더의 의미는 데이터 송/수신 작업이 있을 때만 의미가 있음
public class ContactActivity extends AppCompatActivity {

    RecyclerAdapter adapter;
    ArrayList<Data> datas = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);


        // List<Data> datas = getContacts(); > 한 줄을 향상된 for문을 통해 줄일 수 있음
        for (Data data : getContacts()) {
            Log.i("Contacts", "이름 = " + data.getName() + ", tels = " + data.getTel());
            datas.add(data);
        }

        adapter = new RecyclerAdapter(datas);
        RecyclerView recycler = (RecyclerView) findViewById(R.id.recycler);
        recycler.setAdapter(adapter);
        recycler.setLayoutManager(new LinearLayoutManager(this));
    }





    public List<Data> getContacts() {

        // 데이터베이스 혹은 content resolver를 통해 가져온 데이터를 적재할
        // 데이터 저장소를 먼저 정의함
        List<Data> datas = new ArrayList<>();

        // 일종의 Database 관리툴
        // 전화번호부 내에 Content Provider를 통해 원하는 데이터를 가져올 수 있음
        // 데이터를 가져올 수 있음
        // 시스템에 접근할 수 있는 권한을 갖고 있어야함 // 쿼리문을 쓰기 위해 꼭 있어야 함
        ContentResolver resolver = getContentResolver();

        // 1. 데이터 컨텐츠 URI(자원의 주소, 하나의 엑셀 테이블로 이해)를 정의
        // 전화번호 URI 따로 있음
        // 테이블의 이름 = Uri (엑셀 시트의 이름)
        Uri phoneUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;

        // 2. 데이터에서 가져올 컬럼명 정의 (데이터 테이블 셋팅)
        // 용어를 projection이라고 부름
        // 새로운 컬럼을 추가할 수 있음
        String[] projections = {ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER};

        // 3. Content Resolver로 쿼리를 날려서 데이터를 가져옴
        // null은 일종의 조건들
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
}
