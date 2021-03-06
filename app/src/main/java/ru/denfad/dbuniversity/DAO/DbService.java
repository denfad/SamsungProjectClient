package ru.denfad.dbuniversity.DAO;

import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.denfad.dbuniversity.DAO.client.DbFilters;
import ru.denfad.dbuniversity.DAO.client.DbWorker;
import ru.denfad.dbuniversity.DAO.network.NetworkService;
import ru.denfad.dbuniversity.DAO.server.ServerDb;
import ru.denfad.dbuniversity.DAO.server.ServerDbFilters;
import ru.denfad.dbuniversity.MainActivity;
import ru.denfad.dbuniversity.model.Group;
import ru.denfad.dbuniversity.model.ServerStudent;
import ru.denfad.dbuniversity.model.Student;

public class DbService {

    private ServerDb dbWorker;
    private ServerDbFilters dbFilters;

    public DbService(Context context){
        dbWorker = new ServerDb();
        dbFilters = new ServerDbFilters();
    }

    public Student getStudentByID(int id){
        return dbWorker.selectStudent(id);
    }

    public Group getGroupByID(int id){
        return dbWorker.selectGroup(id);
    }

    public void getAllStudents(final ListView list, final ArrayAdapter adapter, String type){
        NetworkService.getInstance()
                .getJSONApi()
                .getAllStudents(type)
                .enqueue(new Callback<List<ServerStudent>>() {
                    @Override
                    public void onResponse(@NonNull Call<List<ServerStudent>> call, @NonNull Response<List<ServerStudent>> response) {
                        List<Student> students = cast(response.body());
                        adapter.clear();
                        adapter.addAll(students);
                        list.setAdapter(adapter);
                    }

                    @Override
                    public void onFailure(@NonNull Call<List<ServerStudent>> call, @NonNull Throwable t) {
                        Log.e("groups", "fail");
                        t.printStackTrace();
                    }
                });

    }

    public void getAllGroups(final ListView list, final ArrayAdapter adapter){
        NetworkService.getInstance()
                .getJSONApi()
                .getAllGroups()
                .enqueue(new Callback<List<Group>>() {
                    @Override
                    public void onResponse(@NonNull Call<List<Group>> call, @NonNull Response<List<Group>> response) {
                        List<Group> groups = response.body();
                        adapter.clear();
                        adapter.addAll(groups);
                        list.setAdapter(adapter);
                    }

                    @Override
                    public void onFailure(@NonNull Call<List<Group>> call, @NonNull Throwable t) {
                        Log.e("groups", "fail");
                        t.printStackTrace();
                    }
                });

    }


    public void deleteStudent(int id){
        NetworkService.getInstance()
                .getJSONApi()
                .deleteStudent(id)
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {

                    }
                });
    }

    public void deleteStudent(Student student){
        NetworkService.getInstance()
                .getJSONApi()
                .deleteStudent(student.getStudent_id())
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        Log.d("response", response.body());
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {

                    }
                });
    }

    public void deleteGroup(final int id, final  ArrayAdapter adapter, final ListView listView, final Context context){

        NetworkService.getInstance()
                .getJSONApi()
                .checkIsEmptyGroup(id)
                .enqueue(new Callback<Boolean>() {
                    @Override
                    public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                        if(response.body()){
                            Toast.makeText(context,"Delete group",Toast.LENGTH_SHORT).show();
                            deleteGroupAnyway(id);
                            getAllGroups(listView, adapter);
                        }
                        else  {
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setTitle("Удаление группы");
                            builder.setMessage("Группа не пустая, вы правда хотите удалить ее со студентами?");
                            builder.setCancelable(true);
                            builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() { // Кнопка ОК
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    deleteGroupAnyway(id);
                                    getAllGroups(listView,adapter);
                                    dialog.dismiss(); // Отпускает диалоговое окно
                                }
                            });
                            builder.setNegativeButton("Отмена",new DialogInterface.OnClickListener() { // Кнопка ОК
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss(); // Отпускает диалоговое окно
                                }
                            });
                            AlertDialog dialog = builder.create();
                            dialog.show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Boolean> call, Throwable t) {

                    }
                });
    }

    public void deleteGroupAnyway(int id){
        NetworkService.getInstance()
                .getJSONApi()
                .deleteGroup(id)
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {

                    }
                });
    }

    public boolean deleteGroup(Group group){
        if(dbFilters.checkIsEmptyGroup(group.getGroupId())){
            dbWorker.deleteGroup(group.getGroupId());
            return true;
        }
        else return  false;
    }

    public void updateStudent(Student student){
        NetworkService.getInstance()
                .getJSONApi()
                .updateStudent(cast(student))
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        Log.d("response", response.body());
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {

                    }
                });
    }

    public void updateGroup(Group group){
        dbWorker.updateGroup(group.getGroupId(),group.getFaculty());
    }

    public void addStudent(Student student) {
        NetworkService.getInstance()
                .getJSONApi()
                .addStudent(student.getGroupId(),student)
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        Log.d("response", response.body());
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {

                    }
                });
    }

    public void addGroup(Group group){
        NetworkService.getInstance()
                .getJSONApi()
                .addGroup(group)
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        Log.d("response", response.body());
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {

                    }
                });
    }

    public List<Student> findStudentsByGroup(Group group){
        return dbFilters.selectStudentsByGroup(group.getGroupId());
    }

    public void findStudentsByGroup(final ListView list, final ArrayAdapter adapter, int group_id){
        NetworkService.getInstance()
                .getJSONApi()
                .selectStudentByGroup(group_id)
                .enqueue(new Callback<List<ServerStudent>>() {
                    @Override
                    public void onResponse(@NonNull Call<List<ServerStudent>> call, @NonNull Response<List<ServerStudent>> response) {
                        List<Student> students = cast(response.body());
                        adapter.clear();
                        adapter.addAll(students);
                        list.setAdapter(adapter);
                    }

                    @Override
                    public void onFailure(@NonNull Call<List<ServerStudent>> call, @NonNull Throwable t) {
                        Log.e("groups", "fail");
                        t.printStackTrace();
                    }
                });

    }

    public void searchStudents(final ListView listView, final ArrayAdapter adapter, String query){
        NetworkService.getInstance()
                .getJSONApi()
                .searchStudent(query)
                .enqueue(new Callback<List<ServerStudent>>() {
                    @Override
                    public void onResponse(Call<List<ServerStudent>> call, Response<List<ServerStudent>> response) {
                        List<Student> students = cast(response.body());
                        adapter.clear();
                        adapter.addAll(students);
                        listView.setAdapter(adapter);
                    }

                    @Override
                    public void onFailure(Call<List<ServerStudent>> call, Throwable t) {

                    }
                });
    }
    private List<Student> cast(List<ServerStudent> serverStudents){
        List<Student> students = new ArrayList<>();
        for(ServerStudent serverStudent:serverStudents) {
            students.add(new Student(
                    serverStudent.getStudent_id(),
                    serverStudent.getName(),
                    serverStudent.getSecondName(),
                    serverStudent.getMiddleName(),
                    serverStudent.getBirthDate(),
                    serverStudent.getGroup().getGroupId()));
        }
        return students;
    }

    private ServerStudent cast(Student student){
        return new ServerStudent(
                student.getStudent_id(),
                student.getName(),
                student.getSecondName(),
                student.getMiddleName(),
                student.getBirthDate(),
                new Group(student.getGroupId(),null));
    }
}
