package com.jacob.a30minutesschedule;

import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class MainActivity extends AppCompatActivity {


     ArrayList<Task> tasks = new ArrayList<>();
     Notification.Builder notification;
     private static final int uniqueId = 42077;
     CustomAdapter listAdapter;
     Date date = new Date();
     Calendar cal = Calendar.getInstance();
     Thread runThread;
     int currentIndex = -1;

     int minC = -1 , maxC = -1;
    Uri alarmSound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main);
        Toolbar tb = findViewById(R.id.toolbar);
        setSupportActionBar(tb);
        setTitle(getDay(cal.getTime().toString().substring(0 , 3))+"  "+cal.getTime().toString().substring(8 , 10)+"."+getMonth(cal.getTime().toString().substring(4 , 7))+"."+cal.getTime().toString().substring(32,34));
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        notification = new Notification.Builder(this);
        notification.setAutoCancel(true);
        alarmSound = RingtoneManager.getActualDefaultRingtoneUri(this, RingtoneManager.TYPE_NOTIFICATION);
        notification.setSound(null);



        for (int i = 0; i < 48; i++) {
            Task task = new Task();
            if(i%2 == 0){
                task.setTime(i/2 + ":00");
                tasks.add(task);
            }
            else{
                task.setTime(i/2 + ":30");
                tasks.add(task);
            }
        }

        currentIndex = getTimeIndex(cal.getTime().toString().substring(11,16));
        tasks.get(currentIndex).setChecked(true);




        ListView listView = (ListView) findViewById(R.id.listH);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listView.setItemsCanFocus(false);
        listAdapter = new CustomAdapter(this, R.layout.custom_row, tasks);
        listView.setAdapter(listAdapter);



        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                if((position >= minC && position <= maxC) || (minC == -1 && maxC == -1) )
                   showInputBox(tasks.get(position).getTask(), position);
                else
                    showQuestBox( position);


                notification.setSmallIcon(R.drawable.dot);
                notification.setTicker("This is the ticker");
                notification.setWhen(System.currentTimeMillis());
                notification.setContentTitle("Here is the title");
                notification.setContentText("Body text");

                NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

                nm.notify(uniqueId , notification.build());

            }


        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View arg1, int pos, long id) {
                if(pos < minC || pos > maxC || minC == -1) {
                    if (minC == -1) { // new
                        Task t;
                        t = tasks.get(pos);
                        t.setChecked(true);
                        minC = pos;
                        maxC = pos;
                        tasks.set(pos, t);


                    } else {
                        if (pos < minC) {

                            if (minC >= maxC)
                                maxC = minC;

                            minC = pos;

                            for (int i = minC; i < maxC; i++) {
                                Task t;
                                t = tasks.get(i);
                                t.setChecked(true);
                                tasks.set(i, t);
                            }

                        } else if (pos > minC) {

                            maxC = pos;
                            for (int i = minC; i <= maxC; i++) {
                                Task t;
                                t = tasks.get(i);
                                t.setChecked(true);
                                tasks.set(i, t);
                            }

                        }
                    }

                }
                listAdapter.notifyDataSetChanged();



                return true;
            }


        });
        runThread = new Thread();
        runThread();





    }
    // Input Box - The box that appear when you choose to edit tasks.
    public void showInputBox(String oldItem , final int index){
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.input_box);
        TextView txtMessage = (TextView) dialog.findViewById(R.id.txtmessage);

        if (index >= minC && index <= maxC){
            txtMessage.setText(" task from " + tasks.get(minC).getTime() + " to " + tasks.get(maxC).getTime());

        }else if (maxC == -1 && minC == -1) {
            txtMessage.setText("Edit task at " + tasks.get(index).getTime());
        }
        final EditText editText = (EditText) dialog.findViewById(R.id.txtinput);

        if(!tasks.get(index).getTask().equals("Empty"))
            editText.setText(oldItem);

        Button bt = (Button) dialog.findViewById(R.id.btdone);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (index >= minC && index <= maxC){
                    for (int i = minC ; i <= maxC ; i++){
                        Task t;
                        t = tasks.get(i);
                        t.setTask(editText.getText().toString());
                        t.setChecked(false);
                        tasks.set(i , t);
                    }
                    maxC = minC = -1;
                }
                else if (maxC == -1 && minC == -1){
                Task t;
                t = tasks.get(index);
                t.setTask(editText.getText().toString());
                tasks.set(index, t);
            }




                listAdapter.notifyDataSetChanged();
                dialog.dismiss();

            }
        });
        dialog.show();


    }
    // Quest Box - The box that appear when you choose to move tasks.
    public void showQuestBox( final int index){
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.quest_input);

        dialog.setContentView(R.layout.quest_input);
        TextView txtQuest = (TextView) dialog.findViewById(R.id.tvques);

        dialog.setContentView(R.layout.quest_input);
        TextView txtData = (TextView) dialog.findViewById(R.id.tvdata);

        //Log.d("MyApp",maxC + index + "");


        if(index + maxC - minC < tasks.size()) {
            txtData.setText(tasks.get(minC).getTime() + " - " + tasks.get(maxC).getTime()
                    + " to " + tasks.get(index).getTime() + " - " + tasks.get(index + (maxC - minC)).getTime());

            // No option
            Button btno = (Button) dialog.findViewById(R.id.nobt);
            btno.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    for (int i = minC; i <= maxC; i++) {
                            Task t;
                            t = tasks.get(i);
                            t.setChecked(false);
                            tasks.set(i, t);

                    }
                    minC = maxC = -1;
                    listAdapter.notifyDataSetChanged();
                    dialog.dismiss();


                }
            });

            // Yes option
            Button btyes = (Button) dialog.findViewById(R.id.yesbt);
            btyes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int count = 0;

                    for (int i = minC; i <= maxC; i++) {
                        if (count + index < tasks.size()) {
                            Task t;
                            t = tasks.get(count + index);
                            t.setTask(tasks.get(i).getTask().toString());
                            tasks.set(count + index, t);
                            count++;
                        }
                    }
                    // Deletes previous tasks
                    for (int i = minC; i <= maxC; i++) {
                        Log.d("MyApp",maxC - minC + "");
                        Task t;
                        t = tasks.get(i);
                        if ( index > maxC|| i >= index + count)
                          t.setTask("");
                        t.setChecked(false);
                        tasks.set(i, t);

                    }

                    minC = maxC = -1;
                    listAdapter.notifyDataSetChanged();
                    dialog.dismiss();

                }
            });
            dialog.show();
        }
        else{
            // Deletes previous check marks
            for (int i = minC; i <= maxC; i++) {
                Task t;
                t = tasks.get(i);
                t.setChecked(false);
                tasks.set(i, t);
            }

            minC = maxC = -1;
            listAdapter.notifyDataSetChanged();
        }

    }
    private static int getTimeIndex(String str){
        int hour = Integer.parseInt(str.substring(0,2));
        int minute = Integer.parseInt(str.substring(3,5));

        int index = 2*hour;
        if(minute > 29) index++;


        return index;
    }
    private static String getDay(String str) {
        String[] days = {"Saturday", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};
        for (String s : days) {
            if (s.substring(0, 3).equals(str))
                return s;
        }
        return "";
    }
    private static String getMonth(String str) {
        String[] months = {"Jen", "Feb", "Mar", "Apr", "May", "Jun", "Jul","Aug" , "Sep" , "Oct" ,"Nov", "Dec" };
        for (int i = 0 ; i < months.length ; i++) {
            if (months[i].substring(0, 3).equals(str)) {
                if(i > 9)
                    return (i+1)+"";
                else
                    return "0"+(i+1);

            }
        }
        return "";
    }




    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mymenue , menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.reset:
                for(Task t : tasks)
                    t.setTask("");
                listAdapter.notifyDataSetChanged();

                break;

            case R.id.info:
                Toast.makeText(this,"Made by Jacob Levin" , Toast.LENGTH_LONG).show();
        }
        return true;
    }

    private void doSomething(){
        if(getTimeIndex(cal.getTime().toString().substring(11,16)) != currentIndex){
            for(Task t : tasks){
                t.setChecked(false);
            }
            currentIndex = getTimeIndex(cal.getTime().toString().substring(11,16));
            tasks.get(currentIndex).setChecked(true);
        }

        listAdapter.notifyDataSetChanged();
    }

    private void runThread() {
        currentIndex = 0;
        new Thread() {
            public void run() {
                while (true) {
                    try {
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                doSomething();
                            }
                        });
                        Thread.sleep(800);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

}





