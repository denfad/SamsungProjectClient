package ru.denfad.dbuniversity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import ru.denfad.dbuniversity.DAO.DbService;
import ru.denfad.dbuniversity.model.Student;

public class StudentsListActivity extends AppCompatActivity {

    public DbService dbService;
    public ListView listView;
    public ArrayAdapter adapter;
    public List<Student> students = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.students_list);
        final Intent intent = getIntent();
        dbService=new DbService(getApplicationContext());
        students = dbService.findStudentsByGroup(intent.getIntExtra("group_id",Integer.MAX_VALUE));
        adapter=new StudentsAdapter(getApplicationContext(),android.R.layout.simple_list_item_1,students);

        listView=findViewById(R.id.students_list);
        listView.setAdapter(adapter);

        Button button = findViewById(R.id.add);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    Intent intent1 = new Intent(getApplicationContext(),AddStudentActivity.class);
                    intent1.putExtra("group_id",intent.getIntExtra("group_id",Integer.MAX_VALUE));
                    startActivity(intent1);
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent intent1 = new Intent(getApplicationContext(), StudentProfileActivity.class);
                intent1.putExtra("activity","student_list");
                intent1.putExtra("student_id", students.get(i).getStudent_id());
                startActivity(intent1);
            }
        });

    }

    public class StudentsAdapter extends ArrayAdapter<Student> {

        public StudentsAdapter(@NonNull Context context, int resource, @NonNull List<Student> objects) {
            super(context, resource, objects);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull final ViewGroup parent){
            final Student student = getItem(position);

            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.student_item, null);

            StudentHolder holder = new StudentHolder();
            holder.name= convertView.findViewById(R.id.student_name);
            holder.secondName = convertView.findViewById(R.id.student_second_name);
            holder.student_group_id = convertView.findViewById(R.id.student_group_id);



            holder.name.setText(student.getName());
            holder.secondName.setText(student.getSecondName());
            holder.student_group_id.setText(String.valueOf(student.getGroupId()));
            convertView.setTag(holder);

            return convertView;
        }
    }

    private static  class StudentHolder {
        public TextView name;
        public TextView secondName;
        public TextView student_group_id;
    }

    @Override
    public void onBackPressed() {
        Intent intent1 = new Intent(getApplicationContext(),MainActivity.class);
        startActivity(intent1);
        super.onBackPressed();
    }
}
