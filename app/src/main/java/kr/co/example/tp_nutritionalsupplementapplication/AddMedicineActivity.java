package kr.co.example.tp_nutritionalsupplementapplication;

import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class AddMedicineActivity extends AppCompatActivity {

    private GridLayout timeGridLayout;                                  // 시간 표시 GridLayout
    private List<TextView> timeButtons;                                 // 선택 시간 저장 리스트 (버튼형식)
    private List<TextView> dateButtons;                                 // 선택 날짜 저장 리스트 (버튼형식)
    private GridLayout selectedDateGridLayout;                          // 날짜 표시 GridLayout
    private List<Long> selectedDates = new ArrayList<>();               // 선택 날짜 저장 리스트

    // MedicineDatabaseHelper 객체 생성
    private MedicineDatabaseHelper dbHelper;
    private SQLiteDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_medicine);

        Button timeSetBtn = findViewById(R.id.timeSetBtn);                  // 시간 선택 버튼
        timeButtons = new ArrayList<>();                                    // 선택 시간을 저장할 리스트
        timeGridLayout = findViewById(R.id.timeGridLayout);                 // 시간 표시 GridLayout

        selectedDateGridLayout = findViewById(R.id.selectedDateGridLayout); // 날짜 표시 GridLayout
        Button setDateBtn = findViewById(R.id.setDateBtn);                  // 날짜 선택 버튼
        dateButtons = new ArrayList<>();                                    // 선택 날짜를 저장할 리스트

        ImageButton closeBtn = findViewById(R.id.medicineCloseBtn);         // 약 추가 창 닫기 버튼
        Button medicineSaveBtn = findViewById(R.id.medicineSaveBtn);        // 약 정보 저장 버튼

        dbHelper = new MedicineDatabaseHelper(this);
        database = dbHelper.getWritableDatabase();

        // 시간 설정 버튼 클릭 리스너 설정
        timeSetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog();
            }
        });

        // 날짜 설정 버튼 클릭 리스너 설정
        setDateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        // 전체 날짜 삭제 버튼 클릭 리스너 설정
        Button deleteDatesBtn = findViewById(R.id.deleteAllDates);
        deleteDatesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedDateGridLayout.removeAllViews();
                enableRadioButtons();
            }
        });

        // 닫기 버튼 클릭 리스너 설정
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 현재 액티비티를 종료하고 이전 액티비티(메인화면)으로 돌아감
                finish();
            }
        });

        // 저장 버튼 클릭 리스너 설정
        medicineSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveMedicineData();
                Toast.makeText(AddMedicineActivity.this, "약 정보가 저장되었습니다.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(AddMedicineActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }



    // -------------- 시간 설정 -------------------------------------------------------------------------

    // 시간 설정 다이얼로그를 표시하는 메서드
    private void showTimePickerDialog() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                String time = String.format("%02d:%02d", hourOfDay, minute);
                addTimeToGridLayout(time);
            }
        }, 0, 0, true);

        timePickerDialog.show();
    }

    // 선택한 시간을 GridLayout에 추가하는 메서드
    private void addTimeToGridLayout(String time) {
        Button timeButton = new Button(this);
        timeButton.setText(time);
        timeButton.setLayoutParams(new GridLayout.LayoutParams());
        timeButton.setPadding(10, 10, 10, 10);
        timeButtons.add(timeButton);
        timeGridLayout.addView(timeButton);

        // Button 클릭 리스너 설정 (수정, 삭제 기능)
        timeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditDeleteDialog(timeButton);
            }
        });
    }

    // 시간 수정, 삭제 다이얼로그를 보여주는 메서드
    private void showEditDeleteDialog(Button timeButton) {
        // 다이얼로그 빌더 생성
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("시간 수정/삭제");
        builder.setMessage("시간을 수정하거나 삭제할 수 있습니다.");

        // 수정 버튼 설정
        builder.setPositiveButton("수정", (dialog, which) -> {
            showTimePickerDialogForEdit(timeButton);
        });

        // 삭제 버튼 설정
        builder.setNegativeButton("삭제", (dialog, which) -> {
            timeGridLayout.removeView(timeButton);
            timeButtons.remove(timeButton);
        });

        // 다이얼로그 생성 및 표시
        builder.create().show();
    }

    // 기존 시간을 수정하기 위해 TimePickerDialog를 보여주는 메서드
    private void showTimePickerDialogForEdit(Button timeButton) {
        // 버튼의 현재 시간을 가져옴
        String currentTime = timeButton.getText().toString();
        String[] parts = currentTime.split(":");
        int currentHour = Integer.parseInt(parts[0]);
        int currentMinute = Integer.parseInt(parts[1]);

        // 새로운 TimePickerDialog 인스턴스 생성
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                String time = String.format("%02d:%02d", hourOfDay, minute);
                timeButton.setText(time);
            }
        }, currentHour, currentMinute, true);

        timePickerDialog.show();
    }



    // -------- 날짜 설정 -----------------------------------------------------------------------------

    // 날짜 설정 다이얼로그 표시 메서드
    private void showDatePickerDialog() {
        CheckBox checkBoxMon = findViewById(R.id.checkBoxMon);
        CheckBox checkBoxTue = findViewById(R.id.checkBoxTue);
        CheckBox checkBoxWed = findViewById(R.id.checkBoxWed);
        CheckBox checkBoxThu = findViewById(R.id.checkBoxThu);
        CheckBox checkBoxFri = findViewById(R.id.checkBoxFri);
        CheckBox checkBoxSat = findViewById(R.id.checkBoxSat);
        CheckBox checkBoxSun = findViewById(R.id.checkBoxSun);

        // 요일을 선택한 경우에만 달력 띄우기
        if (!(checkBoxMon.isChecked() || checkBoxTue.isChecked() || checkBoxWed.isChecked() ||
                checkBoxThu.isChecked() || checkBoxFri.isChecked() || checkBoxSat.isChecked() || checkBoxSun.isChecked())) {
            MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder.datePicker();
            builder.setTitleText("날짜 선택");

            final MaterialDatePicker<Long> materialDatePicker = builder.build();

            materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {
                @Override
                public void onPositiveButtonClick(Long selection) {
                    Set<Long> selectedDateSet = Collections.singleton(materialDatePicker.getSelection());
                    selectedDates.clear();
                    selectedDates.addAll(selectedDateSet);

                    displaySelectionDates();
                    disableRadioButtons();
                }
            });

            materialDatePicker.show(getSupportFragmentManager(), "MATERIAL_DATE_PICKER");
        } else {
            Toast.makeText(this, "요일을 선택한 경우에는 캘린더를 사용할 수 없습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    // 설정 날짜를 textView로 보여주는 메서드
    private void displaySelectionDates() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        for (Long date : selectedDates) {
            Button dateButton = new Button(this);
            dateButton.setText(sdf.format(new Date(date)));
            dateButton.setLayoutParams(new GridLayout.LayoutParams());
            dateButtons.add(dateButton);
            dateButton.setPadding(10, 10, 10, 10);

            // 선택한 날짜 버튼을 그리드 레이아웃에 추가
            selectedDateGridLayout.addView(dateButton);

            // 버튼 클릭 시 수정 또는 삭제 다이얼로그 표시
            dateButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showEditDeleteDateDialog(dateButton);
                }
            });
        }
    }

    // textView의 날짜를 선택하면 수정 다이얼로그를 보여주는 메서드
    private void showEditDeleteDateDialog(Button dateButton) {
        // 다이얼로그 빌더 생성
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("날짜 수정");
        builder.setMessage("날짜를 수정하시겠습니까?");

        // 수정 버튼 설정
        builder.setPositiveButton("수정하기", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 수정 기능 구현
                showDatePickerDialogForEdit(dateButton);
            }
        });

        // 다이얼로그 생성 및 표시
        builder.create().show();
        updateRadioButtons();
    }

    // 수정 다이얼로그에서 기존의 날짜를 수정하기 위해 호출되는 메서드
    private void showDatePickerDialogForEdit(Button dateButton) {
        MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder.datePicker();
        builder.setTitleText("날짜 선택");

        final MaterialDatePicker<Long> materialDatePicker = builder.build();

        materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {
            @Override
            public void onPositiveButtonClick(Long selection) {
                // 기존 선택된 날짜 버튼의 텍스트 업데이트
                if (dateButton != null) {
                    dateButton.setText(formatSelectedDate(selection));
                }
                // 새로운 선택된 날짜 버튼 생성
                else {
                    Button newDateButton = createButton();
                    selectedDateGridLayout.addView(newDateButton);
                }
            }
        });

        materialDatePicker.show(getSupportFragmentManager(), "MATERIAL_DATE_PICKER");
    }

    // 선택된 날짜를 원하는 형식으로 포맷팅하는 메서드
    private String formatSelectedDate(Long selection) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(new Date(selection));
    }

    // 새로운 날짜를 표시하는 버튼을 생성하는 메서드
    private Button createButton() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Button newButton = new Button(this);
        for (Long date : selectedDates) {
            newButton.setText(sdf.format(new Date(date)));
            newButton.setLayoutParams(new GridLayout.LayoutParams());
            dateButtons.add(newButton);
            newButton.setPadding(10, 10, 10, 10);
        }
        return newButton;
    }

    // 선택 요일 문자열 반환 메서드 (DB 저장에 이용)
    private List<String> getSelectedDaysOfWeek() {
        List<String> days = new ArrayList<>();
        CheckBox checkBoxMon = findViewById(R.id.checkBoxMon);
        CheckBox checkBoxTue = findViewById(R.id.checkBoxTue);
        CheckBox checkBoxWed = findViewById(R.id.checkBoxWed);
        CheckBox checkBoxThu = findViewById(R.id.checkBoxThu);
        CheckBox checkBoxFri = findViewById(R.id.checkBoxFri);
        CheckBox checkBoxSat = findViewById(R.id.checkBoxSat);
        CheckBox checkBoxSun = findViewById(R.id.checkBoxSun);

        if (checkBoxMon.isChecked()) days.add("Mon");
        if (checkBoxTue.isChecked()) days.add("Tue");
        if (checkBoxWed.isChecked()) days.add("Wed");
        if (checkBoxThu.isChecked()) days.add("Thu");
        if (checkBoxFri.isChecked()) days.add("Fri");
        if (checkBoxSat.isChecked()) days.add("Sat");
        if (checkBoxSun.isChecked()) days.add("Sun");

        return days;
    }



    // ---------------- DB 저장 --------------------------------------------------------------------------

    // 내부DB 저장 메서드 (실제 데이터베이스 작업을 담당)
    // MedicineDatabaseHelper클래스와 상호작용
    private void saveMedicineData() {
        String medicineName = ((EditText) findViewById(R.id.medicineName)).getText().toString();

        List<String> times = new ArrayList<>();
        for (TextView timeButton : timeButtons) {
            String time = timeButton.getText().toString();
            times.add(time);
        }

        List<String> daysOfWeek = getSelectedDaysOfWeek();

        // 선택한 날짜가 없는 경우 모든 요일에 대해 데이터베이스에 저장
        if (selectedDates.isEmpty()) {
            for (String day : daysOfWeek) {
                saveToDatabase(medicineName, times, Collections.singletonList(day));
            }
        } else {
            for (Long date : selectedDates) {
                String formattedDate = formatSelectedDate(date);
                saveToDatabase(medicineName, times, Collections.singletonList(formattedDate));
            }
        }
    }

    // DB 저장 메서드 (사용자 인터페이스와의 상호작용 및 데이터 처리 흐름을 관리)
    // 사용자가 입력한 정보를 적당히 가공하여 saveMedicineData() 메서드로 전달
    private void saveToDatabase(String name, List<String> times, List<String> dates) {
        MedicineDatabaseHelper dbHelper = new MedicineDatabaseHelper(this); // MedicineDatabaseHelper 객체 생성
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        for (String time : times) {
            for (String date : dates) {
                ContentValues values = new ContentValues();
                values.put(dbHelper.getColumnName(), name); // 컬럼 이름에 접근하여 값 추가
                values.put(dbHelper.getColumnTime(), time); // 컬럼 시간에 접근하여 값 추가
                values.put(dbHelper.getColumnDate(), date); // 컬럼 날짜에 접근하여 값 추가

                database.insert(dbHelper.getTableMedicine(), null, values); // 데이터베이스에 값 추가
            }

        }

        dbHelper.close(); // 데이터베이스 연결 종료
    }



    // ------------------ 라디오 버튼 -------------------------------------------------------------------


    // 라디오버튼 업데이트 메서드
    private void updateRadioButtons() {
        // 선택된 날짜가 없을 때만 라디오 버튼 활성화
        if (selectedDates.isEmpty()) {
            enableRadioButtons();
        } else {
            disableRadioButtons();
        }
    }

    // 요일 선택 라디오버튼 비활성화 메서드
    private void disableRadioButtons() {
        CheckBox checkBoxMon = findViewById(R.id.checkBoxMon);
        CheckBox checkBoxTue = findViewById(R.id.checkBoxTue);
        CheckBox checkBoxWed = findViewById(R.id.checkBoxWed);
        CheckBox checkBoxThu = findViewById(R.id.checkBoxThu);
        CheckBox checkBoxFri = findViewById(R.id.checkBoxFri);
        CheckBox checkBoxSat = findViewById(R.id.checkBoxSat);
        CheckBox checkBoxSun = findViewById(R.id.checkBoxSun);

        checkBoxMon.setEnabled(false);
        checkBoxTue.setEnabled(false);
        checkBoxWed.setEnabled(false);
        checkBoxThu.setEnabled(false);
        checkBoxFri.setEnabled(false);
        checkBoxSat.setEnabled(false);
        checkBoxSun.setEnabled(false);
    }

    // 요일 선택 라디오버튼 활성화 메서드
    private void enableRadioButtons() {
        CheckBox checkBoxMon = findViewById(R.id.checkBoxMon);
        CheckBox checkBoxTue = findViewById(R.id.checkBoxTue);
        CheckBox checkBoxWed = findViewById(R.id.checkBoxWed);
        CheckBox checkBoxThu = findViewById(R.id.checkBoxThu);
        CheckBox checkBoxFri = findViewById(R.id.checkBoxFri);
        CheckBox checkBoxSat = findViewById(R.id.checkBoxSat);
        CheckBox checkBoxSun = findViewById(R.id.checkBoxSun);

        checkBoxMon.setEnabled(true);
        checkBoxTue.setEnabled(true);
        checkBoxWed.setEnabled(true);
        checkBoxThu.setEnabled(true);
        checkBoxFri.setEnabled(true);
        checkBoxSat.setEnabled(true);
        checkBoxSun.setEnabled(true);
    }
}
