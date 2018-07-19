package ltc.aibotvk;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.util.Pair;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;

import com.victor.loading.rotate.RotateLoading;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiModel;
import com.yalantis.guillotine.animation.GuillotineAnimation;
import com.yalantis.guillotine.interfaces.GuillotineListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;

import ltc.aibotvk.Adapters.PageAdapter;
import ltc.aibotvk.Fragments.FrGenerated;
import ltc.aibotvk.Fragments.FrPersonal;
import ltc.aibotvk.Helpers.DatabaseHelperGenerated;
import ltc.aibotvk.Helpers.DatabaseHelperPersonal;
import ltc.aibotvk.Models.DataModel;

/**
 * Created by admin on 28.06.2018.
 */

public class SecondActivity extends ActionBarActivity {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private PagerAdapter pagerAdapter;
    private Toolbar toolbar;
    private static DatabaseHelperPersonal dbHelperPersonal;
    private static DatabaseHelperGenerated dbHelperGenerated;
    private FrameLayout root;
    private View contentHamburger;
    private View guillotineMenu;
    private RelativeLayout relativeLayout;
    public static boolean isPersonal = false, isGenerated = false;
    private CheckMessagesTask checkMessagesTask;
    private AnalyzeTask analyzeTask;
    private TestMessagesTask testMessagesTask;
    private ArrayList<String> userIds;
    private RotateLoading rotateLoading;
    private LinearLayout add_personal, add_generated, update_generated, change_person;
    private Switch aSwitchPersonal, aSwitchGenerated;

    public static ArrayList<Pair<Integer, String>> lastmessages;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);


        guillotineMenu = LayoutInflater.from(this).inflate(R.layout.menu_layout, null);

        add_personal = (LinearLayout) guillotineMenu.findViewById(R.id.add_personal);
        add_generated = (LinearLayout) guillotineMenu.findViewById(R.id.add_generated);
        update_generated = (LinearLayout) guillotineMenu.findViewById(R.id.update_generated);
        rotateLoading = (RotateLoading) guillotineMenu.findViewById(R.id.rotateloading);
        aSwitchPersonal = (Switch) guillotineMenu.findViewById(R.id.switch_personal);
        aSwitchGenerated = (Switch) guillotineMenu.findViewById(R.id.switch_generated);
        change_person = (LinearLayout) guillotineMenu.findViewById(R.id.change_person);

        root = (FrameLayout) findViewById(R.id.root);
        relativeLayout = (RelativeLayout) findViewById(R.id.relativelayout);
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        viewPager = (ViewPager) findViewById(R.id.pager);
        contentHamburger = (View) findViewById(R.id.content_hamburger);
        toolbar = (Toolbar) findViewById(R.id.toolbar);


        userIds = new ArrayList<String>();

        dbHelperGenerated = new DatabaseHelperGenerated(this);
        dbHelperPersonal = new DatabaseHelperPersonal(this);
        lastmessages = new ArrayList<Pair<Integer, String>>();
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle(null);
        }

        root.addView(guillotineMenu); // Добавляем меню
        add_personal.setOnClickListener(new View.OnClickListener() { // Кнопка добавления в личную БД
            @Override
            public void onClick(View v) {
                showDialog(true);
            }
        });

        add_generated.setOnClickListener(new View.OnClickListener() { // Кнопка добавления в сгенерированную БД
            @Override
            public void onClick(View v) {
                showDialog(false);
            }
        });

        update_generated.setOnClickListener(new View.OnClickListener() {  // Кнопка перегенерации БД
            @Override
            public void onClick(View v) {
                rotateLoading.start();
                startAnalyzing();
            }
        });

        //Включение/Выключение личного бота
        aSwitchPersonal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isPersonal = !isPersonal;
                isGenerated = false;
                aSwitchPersonal.setChecked(isPersonal);
                aSwitchGenerated.setChecked(isGenerated);
            }
        });

        //Включение/Выключение сгенерированного бота
        aSwitchGenerated.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isGenerated = !isGenerated;
                isPersonal =  false;
                aSwitchPersonal.setChecked(false);
                aSwitchGenerated.setChecked(isGenerated);
            }
        });

        //Выход из пользователя
        change_person.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VKSdk.logout();
                startActivity(new Intent(SecondActivity.this, MainActivity.class));
                finish();
            }
        });

        // Настройка меню
        new GuillotineAnimation.GuillotineBuilder(guillotineMenu, guillotineMenu.findViewById(R.id.guillotine_hamburger), contentHamburger)
                .setStartDelay(100)
                .setActionBarViewForAnimation(toolbar)
                .setClosedOnStart(true)
                .setGuillotineListener(new GuillotineListener() {
                 @Override
                    public void onGuillotineOpened() {
                        relativeLayout.setVisibility(View.INVISIBLE);
                    }

                 @Override
                    public void onGuillotineClosed() {
                     relativeLayout.setVisibility(View.VISIBLE);
                    }
                })
                .build();


        // Табы
        tabLayout.addTab(tabLayout.newTab().setText(getApplicationContext().getResources().getString(R.string.first_name)));
        tabLayout.addTab(tabLayout.newTab().setText(getApplicationContext().getResources().getString(R.string.second_name)));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        pagerAdapter = new PageAdapter(getSupportFragmentManager(), tabLayout.getTabCount());

        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
     //   getLastMessages();
        startChecking();
    }

    void startAnalyzing(){ //Начало перегенерации
        deleteTableGenerated();
        analyzeTask = new AnalyzeTask();
        analyzeTask.execute();
    }

    void startChecking(){ // Запуск проверки сообщений
        checkMessagesTask = new CheckMessagesTask();
        checkMessagesTask.execute();
    }

    //Получаем количество непрочитанных сообщений
    void getCountUnreaded(){
        VKRequest request = new VKRequest("messages.getConversations" , VKParameters.from(VKApiConst.COUNT, 1,
                "filter", "unread"));
        request.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(final VKResponse response) {
                super.onComplete(response);
                ArrayList<Pair<Integer, String>> lastMessages = new ArrayList<>();
                JSONObject object = response.json;
                try {
                    Repository.countMessages = Integer.valueOf(object.getJSONObject("response").get("count").toString());
                } catch (JSONException e) {
                }
            }
        });
    }

    void getLastMessages(){ // Сохраняем последние непрочитанные сообщения
        getCountUnreaded();



        int countUnreaded = Repository.countMessages;
        int offset = 0;

        for(int i = 0; i<countUnreaded/200;i++) {
            VKRequest request = new VKRequest("messages.getConversations",
                    VKParameters.from(VKApiConst.COUNT, 200,
                            "filter", "unread", VKApiConst.OFFSET, offset));
            request.executeWithListener(new VKRequest.VKRequestListener() {
                @Override
                public void onComplete(final VKResponse response) {
                    super.onComplete(response);

                    if( Repository.unreadMessages.size() > 0 ) Repository.unreadMessages.clear();

                    JSONObject object = response.json;
                    try {
                        JSONObject objectResponse = object.getJSONObject("response");
                        JSONArray jsonArray = objectResponse.getJSONArray("items");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject arr = jsonArray.getJSONObject(i);

                            String type = arr.getJSONObject("conversation").getJSONObject("peer").get("type").toString();
                            String userId = arr.getJSONObject("conversation").getJSONObject("peer").get("id").toString();
                            String msg = arr.getJSONObject("last_message").get("text").toString();

                            Repository.unreadMessages.add(new Pair<Integer, String>(Integer.valueOf(userId), msg));
                        }
                        testMessagesTask = new TestMessagesTask();
                        testMessagesTask.execute();
                    } catch (JSONException e) {
                    }


                }
            });
            offset+=201;
        }
        offset = 0;




    }

    public static void insertDataPersonal(String sentence, String answer){ // Добавление в личную бд
        boolean isInserted = dbHelperPersonal.insertData(sentence, answer);
        if (isInserted) Log.d("DB_INSERT", "true"); else Log.d("DB_INSERT", "false");
    }

    public static ArrayList<DataModel> viewAllPersonal(){ // Получение всех записей из личной бд
        ArrayList<DataModel> allValues = new ArrayList<DataModel>();
        Cursor res = dbHelperPersonal.getAllData();

        while (res.moveToNext()){
            allValues.add(new DataModel(res.getString(1), res.getString(2)));
        }

        return allValues;
    }

    // Перезапись строк в личной БД
    public static void UpdateDataPersonal(String id, String sentence, String answer, String oldSent, String oldAns){
        boolean isUpdate = dbHelperPersonal.updateData(id, sentence, answer, oldSent, oldAns);
        if(isUpdate) Log.d("DB_UPDATE", "true"); else Log.d("DB_UPDATE", "false");
    }

    // Удаление записи из личной БД
    public static void deleteDataPersonal(String id, String oldSent, String oldAns){
        Integer numRows = dbHelperPersonal.deleteData(id, oldSent, oldAns);
        if(numRows>0) Log.d("DB_DELETE", "true"); else Log.d("DB_DELETE", "false");
    }

    // Вставка в сгенерированную БД
    public static void insertDataGenerated(String sentence, String answer){
        boolean isInserted = dbHelperGenerated.insertData(sentence, answer);
        if (isInserted) Log.d("DB_INSERT", "true"); else Log.d("DB_INSERT", "false");
    }

    //Получение всех сгенерированных записей
    public static ArrayList<DataModel> viewAllGenerated(){
        ArrayList<DataModel> allValues = new ArrayList<DataModel>();
        Cursor res = dbHelperGenerated.getAllData();

        while (res.moveToNext()){
            allValues.add(new DataModel(res.getString(1), res.getString(2)));
        }

        return allValues;
    }

    // Обновление сгенерированных записей
    public static void UpdateDataGenerated(String id, String sentence, String answer, String oldSent, String oldAns){
        boolean isUpdate = dbHelperGenerated.updateData(id, sentence, answer, oldSent, oldAns);
        Log.d("GEN", sentence);
        if(isUpdate) Log.d("DB_UPDATE", "true"); else Log.d("DB_UPDATE", "false");
    }

    // Удаление записи из сгенерированной БД
    public static void deleteDataGenerated(String id, String oldSent, String oldAns){
        Integer numRows = dbHelperGenerated.deleteData(id, oldSent, oldAns);
        if(numRows>0) Log.d("DB_DELETE", "true"); else Log.d("DB_DELETE", "false");
    }

    // Очистка всей сгенерированной БД
    public static void deleteTableGenerated(){
        Integer numRows = dbHelperGenerated.deleteTable();
        if(numRows>0) Log.d("DB_DELETE", "true"); else Log.d("DB_DELETE", "false");
    }

    // Вывод диалога для добавления записи
    public void showDialog(final boolean isPersonal){
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.alert_frame, null);
        final EditText etsentence = ((EditText) alertLayout.findViewById(R.id.et_sen));
        final EditText etanswer = ((EditText) alertLayout.findViewById(R.id.et_ans));

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setView(alertLayout)
                .setCancelable(false)
                .setNegativeButton("Отменить", null)
                .setPositiveButton("Добавить", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if(isPersonal) {
                            insertDataPersonal(etsentence.getText().toString(), etanswer.getText().toString());
                            FrPersonal.updateAdapter();
                        }else{
                            insertDataGenerated(etsentence.getText().toString(), etanswer.getText().toString());
                            FrGenerated.updateAdapter();
                        }
                    }
                });
        AlertDialog dialog = alert.create();
        dialog.show();
    }

    // Сравнение сообщений с данными из БД
    void checkMessages(ArrayList<Pair<Integer, String>> lastMessages){
        ArrayList<DataModel> dbMessages = null;
        if(isPersonal) dbMessages = viewAllPersonal(); else if(isGenerated) dbMessages = sortBase(viewAllGenerated());
        if((isGenerated)||(isPersonal)){
            for(int i = 0; i<lastMessages.size(); i++){
                if(fixString(lastMessages.get(i).second).equals(fixString("в смысле?"))) Log.d("fix", String.valueOf(i));
                for(int j = 0; j<dbMessages.size(); j++){
                    String tmp = lastMessages.get(i).second;
                    if(lastMessages.get(i).second.length()>0 &&fixString(dbMessages.get(j).getSentence()).length()>0 && i!=j) {
                        if (fixString(lastMessages.get(i).second).equals(fixString(dbMessages.get(j).getSentence()))) {
                            sendMessage(lastMessages.get(i).first.toString(), dbMessages.get(j).getAnswer());
                            break;
                        }
                    }
                }
            }
        }


    }

    // Сортировка и настройка данных, полученных из БД
    public static  ArrayList<DataModel> sortBase(ArrayList<DataModel> dbData){
        int n = dbData.size();
        boolean[] check = new boolean[n];
        int[] rating = new int[n];
        for(int i = 0; i<n;i++){
            rating[i] = 0;
            check[i] = false;
        }

        for(int i = 0; i<n;i++){
            if(!check[i]){
                for(int j = 0; j<n; j++){
                    if(dbData.get(i).getSentence().equals(dbData.get(j).getSentence()) && dbData.get(i).getAnswer().equals(dbData.get(j).getAnswer()) && i!=j){
                        check[j] = true;
                        rating[i]++;
                    }
                }
                check[i] = true;
            }
        }

        for(int i = 0; i < dbData.size(); i++){
            int tmp = 0;
            for(int j = 0; j<dbData.size(); j++){
                if(rating[j]>rating[i] && dbData.get(i).getSentence().equals(dbData.get(j).getSentence())){
                    dbData.get(i).setData(dbData.get(j).getSentence(), dbData.get(j).getAnswer());
                    dbData.remove(j);
                    j--;
                    tmp++;
                }
                i-=tmp;
            }
        }
        return dbData;
    }

    // Отправка сообщения
    void sendMessage(String id, String message){
        Log.d("mess", message+":"+id);

        VKRequest request = new VKRequest("messages.send", VKParameters.from("peer_id",
                        id, VKApiConst.MESSAGE, message));
                request.executeWithListener(new VKRequest.VKRequestListener() {
                    @Override
                    public void onError(VKError error) {
                        super.onError(error);
                        Log.d("id", error.toString());
                    }

                    @Override
                    public void onComplete(VKResponse response) {
                        super.onComplete(response);
                        Log.d("id", "come");
                    }
                });
    }

    // Поправка строки
    String fixString(String sentence){
        sentence = sentence.replaceAll(" ", "");
        sentence = sentence.toLowerCase();
        return sentence;
    }

    // Запуск постоянной проверки сообщений с ответом
    class CheckMessagesTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                getLastMessages();
                Thread.sleep(10000);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            checkMessagesTask = new CheckMessagesTask();
            checkMessagesTask.execute();
        }
    }

    // Функции для анализа
    class AnalyzeTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(Void... params) {
            userIds.clear();
            getFriends();

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            getFullDialog(Repository.values);
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            rotateLoading.stop();
        }
    }

    //Получение списка друзей
    void getFriends(){

       // Log.d("GENERATED", "gF entered");
        VKRequest request = VKApi.friends().get(VKParameters.from(VKApiConst.COUNT, 5000));
        request.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                VKApiModel vkApiModel = (VKApiModel) response.parsedModel;
                ArrayList<String> ids = new ArrayList<>();
                JSONObject object = response.json;
                try {
                    JSONObject objectResponse = object.getJSONObject("response");
                    JSONArray jsonArray = objectResponse.getJSONArray("items");

                    for(int i = 0; i<jsonArray.length(); i++){
                        ids.add(jsonArray.get(i).toString());
                        Repository.values.add(ids.get(i));

                        //insertDataGet(ids.get(i), ids.get(i));
                    }

                } catch (JSONException e) {
                }
            }
        });

    }

    //Получение списка диалога с другом
    void getFullDialog(ArrayList<String> user_ids){
        for(int i = 0; i<user_ids.size(); i++) {
            String user_id = user_ids.get(i);
            VKRequest request = new VKRequest("messages.getHistory", VKParameters.from(VKApiConst.USER_ID, user_id, VKApiConst.COUNT, 200));
            request.executeWithListener(new VKRequest.VKRequestListener() {
                @Override
                public void onComplete(VKResponse response) {
                    super.onComplete(response);
                    ArrayList<String> ids = new ArrayList<>();
                    JSONObject object = response.json;
                    try {
                        JSONObject objectResponse = object.getJSONObject("response");
                        JSONArray jsonArray = objectResponse.getJSONArray("items");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            ids.add(jsonArray.get(i).toString());
                        }
                        generaterSents(ids);
                        ids.clear();

                    } catch (JSONException e) {
                    }

                }
            });
        }
    }

    //Поиск совпадений сообщений с сообщениями из БД
    class TestMessagesTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(Void... params) {
            checkMessages(Repository.unreadMessages);


            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }
    }



    //Генерация сообщений
    void generaterSents(ArrayList<String> messages){
      boolean changedPerson = false;
      int typePerson = 0;
      for(int i = messages.size()-1; i>=0; i--) {
          try {
              JSONObject obj = new JSONObject(messages.get(i));
              String out = obj.getString("out");
              String message = obj.getString("body");

              if(Integer.valueOf(out)==0){
                  for(int j = 0; j<Repository.dictionary.length; j++){
                      if(fixString(message).indexOf(fixString(Repository.dictionary[j]))!=-1){


                          int k = i;


                          StringBuilder answerMessage = new StringBuilder();
                          StringBuilder sendedMessage = new StringBuilder(message+" ");

                          JSONObject objTemp = new JSONObject(messages.get(k));
                          String outTemp = objTemp.getString("out");
                          String messageTemp = objTemp.getString("body");

                          typePerson = Integer.valueOf(outTemp);
                          boolean changed = false;

                          k--;

                          while (k>=0){
                              objTemp = new JSONObject(messages.get(k));
                              outTemp = objTemp.getString("out");
                              messageTemp = objTemp.getString("body");


                              if(typePerson != Integer.valueOf(outTemp)){
                                  if(findWord(messageTemp) && changed) {
                                      if(sendedMessage.toString().length()>0 && answerMessage.toString().length()>0) insertDataGenerated(sendedMessage.toString(), answerMessage.toString());
                                      break;
                                  }
                                  if(!changed) changed = true;
                                  answerMessage.append(messageTemp).append(" ");
                              }else{
                                  if(!changed){
                                      if(findWord(messageTemp)) {
                                          Log.d("generated","exited");
                                          if(sendedMessage.toString().length()>0 && answerMessage.toString().length()>0) insertDataGenerated(sendedMessage.toString(), answerMessage.toString());
                                          break;
                                      }
                                       sendedMessage.append(messageTemp).append(" ");
                                  }else{
                                      if(sendedMessage.toString().length()>0 && answerMessage.toString().length()>0) insertDataGenerated(sendedMessage.toString(), answerMessage.toString());

                                      answerMessage = new StringBuilder("");
                                      sendedMessage = new StringBuilder(messageTemp+" ");
                                      changed = false;
                                  }

                              }
                              k--;
                          }
                          i = k+1;
                          break;
                      }
                  }
              }
          } catch (JSONException e) {
              e.printStackTrace();
          }
      }
      FrGenerated.updateAdapter();
    }

    boolean findWord(String word){
        for(int j = 0; j<Repository.dictionary.length; j++){
            if(word.indexOf(Repository.dictionary[j])==1) return true;
        }
        return false;
    }

}