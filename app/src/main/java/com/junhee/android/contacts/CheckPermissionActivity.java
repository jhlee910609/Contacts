package com.junhee.android.contacts;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class CheckPermissionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_permission);

        // api lever 이 23이상일 경우만 실행
        // 설치 안드로이드폰의 api level 가져오기
        // VERSION_CODES 아래에 상수로 각 버전별 api level이 작성되어 있음
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkPermission();
        } else {
            // 아니면 그냥 run();
            run();
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    public void checkPermission() {

        // 1. 권한체크를 한다. > 특정권한이 있는지 시스템에 물어봄
        if (checkSelfPermission(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            run();
        } else {
            // 2. 권한이 없다면 사용자에게 권한을 요청함
            // rrequestcode = 어디서 요청이 왔는지를 판단하는 구분자
            String permissions[] = {Manifest.permission.READ_CONTACTS};
            requestPermissions(permissions, REQ_PERMISSION); // > 권한을 요구하는 팝업이 사용자 화면에 노출됨
        }
    }

    private final int REQ_PERMISSION = 100;
    // 2. 사용자에게 권한체크
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

    public void run() {

        Intent intent = new Intent(this, ContactActivity.class);
        startActivity(intent);
    }

    public void cancel() {
        Toast.makeText(this, "권한요청을 승인하셔야 앱을 사용할 수 있습니다.", Toast.LENGTH_SHORT).show();
        finish();
    }


}
