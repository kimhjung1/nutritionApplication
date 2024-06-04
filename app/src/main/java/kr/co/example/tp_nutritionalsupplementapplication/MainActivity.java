package kr.co.example.tp_nutritionalsupplementapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.PopupMenu;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private MedicineDatabaseHelper dbHelper;
    private ListView medicineListView;
    private MedicineAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        dbHelper = new MedicineDatabaseHelper(this);
        medicineListView = findViewById(R.id.medicineListView);

        loadMedicines();
    }

    // 약, 영양제 추가 팝업메뉴 표시 메서드
    public void dropMenu(View button) {
        PopupMenu popup = new PopupMenu(this, button);
        popup.getMenuInflater().inflate(R.menu.popup, popup.getMenu());
        popup.setOnMenuItemClickListener(
                item -> {
                    int itemId = item.getItemId();
                    if (itemId == R.id.addMedicine) {
                        Intent intent = new Intent(MainActivity.this, AddMedicineActivity.class);
                        startActivity(intent);
                        return true;
                    } else if (itemId == R.id.addNutritionalSupplement) {
                        Intent intent = new Intent(MainActivity.this, AddNutritionalSupplementActivity.class);
                        startActivity(intent);
                        return true;
                    }
                    return false;
                }
        );
        popup.show();
    }

    // DB의 모든 약 정보 데이터를 가져와 리스트 형태로 화면에 표시
    private void loadMedicines() {
        List<Medicine> medicines = dbHelper.getAllMedicines();
        adapter = new MedicineAdapter(this, medicines);
        medicineListView.setAdapter(adapter);
    }
}
