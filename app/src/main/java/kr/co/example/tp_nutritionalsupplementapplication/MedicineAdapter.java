package kr.co.example.tp_nutritionalsupplementapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

public class MedicineAdapter extends BaseAdapter {
    private Context context;
    private List<Medicine> medicines;

    public MedicineAdapter(Context context, List<Medicine> medicines) {
        this.context = context;
        this.medicines = medicines;
    }

    @Override
    public int getCount() {
        return medicines.size();
    }

    @Override
    public Object getItem(int position) {
        return medicines.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    // 리스트뷰의 각 항목에 대한 뷰를 생성하고 반환, 각 항목에 대한 뷰를 정의하고 데이터를 연결
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.medicine_item, parent, false);
        }

        Medicine medicine = (Medicine) getItem(position);

        TextView nameTextView = convertView.findViewById(R.id.nameTextView);
        Button timeButton = convertView.findViewById(R.id.timeButton);
        TextView dateTextView = convertView.findViewById(R.id.dateTextView);

        nameTextView.setText(medicine.getName());
        timeButton.setText(medicine.getTime());
        dateTextView.setText(medicine.getDate());

        return convertView;
    }
}
