package com.example.weather_app;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private EditText user_field;
    private Button main_button;
    private TextView result_info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //за счет этого метода находим определенный объект в дизайне, его id и ссылку на него
        user_field = findViewById(R.id.user_field);
        main_button = findViewById(R.id.main_button);
        result_info = findViewById(R.id.result_info);

        //обработчик события
        main_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* getText получает то что пользователь пишет в поле, преобразовываем этот текст в строку,
                 * через trim убираем лишние пробелы перед и после, и проверяем не пустая ли строка */
                if (user_field.getText().toString().trim().equals("")) {
                    /*если ничего не ввели то показываем всплывающее окно обращаясь к встроеному классу Toast и методу makeText
                     *указываем 3 параметра: MainActivity.this(окно должно быть показано на самой этой странице
                     * далее указываем текст который будет показан пользователю через strings.xml
                     * далее указываем длительность окна */
                    Toast.makeText(MainActivity.this, R.string.no_user_input, Toast.LENGTH_LONG).show();
                } else {
                    //получаем тот текст который ввел пользователь
                    String city = user_field.getText().toString();
                    String key = "18e1af735faa79883cfab9b3d2d2f9be";
                    String url = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + key + "&units=metric&lang=ru";

                    /*Выполняет задачу с указанными параметрами. Задача возвращает себя (this),
                    чтобы вызывающий мог сохранить ссылку на нее.*/
                    new GetURLData().execute(url);
                    // добавляем в AndroidManifest разрешение  для работы с интернетом <uses-permission android:name="android.permission.INTERNET"/>
                }
            }
        });
    }

    /*создаем вложенный класс для работы с URL, наследуется от класса AsyncTask для асинхронного кода
    т.е. пока основной код работает, асинхронно\паралельно будет работать еще часть кода
    в этом случае это подключение к url адрессу и считывание из этого адресса*/
    private class GetURLData extends AsyncTask<String, String, String> {

        //реализуем метод который будет срабатывать в тот момент когда отправляем данные по url (ожидаем получение пакета)
        protected void onPreExecute() {
            super.onPreExecute();
            result_info.setText("Ожидайте");
        }


        @Override
        protected String doInBackground(String... strings) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                /*создали объект на основе которого мы сможем обращатся по определенному url адресу*/
                URL url = new URL(strings[0]);
                //открываем само соединение
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                //считываем данные с url адресса
                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));
                //метод для добавление записей к текущей строке
                StringBuilder buffer = new StringBuilder();
                String line = "";

                while ((line = reader.readLine()) != null) //цикл будет считывать пока не будет null
                    buffer.append(line).append("\n"); //каждый раз добавляем строку и переход на новую линию
                return buffer.toString();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally { //закрываем соединения
                if (connection != null)
                    connection.disconnect(); //если соединение открыто, ту будем закрывать, если не закрывать будет перегруз
                try {
                    if (reader != null) reader.close(); //ели reader не 0 то закрываем объект
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } return null;
        }
        //метод который срабатывать в момент когда мы полностью получили все данные по url и показываем пользователю
        @SuppressLint("SetTextI18n")
        @Override
        protected void onPostExecute (String result) {
            super.onPostExecute(result);

            /*{"coord":{"lon":37.6156,"lat":55.7522}, "weather":[{"id":804,"main":"Clouds","description":"пасмурно","icon":"04n"}], "base":"stations",
            "main":{"temp":-2.96,"feels_like":-2.96,"temp_min":-4.76,"temp_max":-1.46,"pressure":1017,"humidity":98,"sea_level":1017,"grnd_level":998},
            "visibility":10000,"wind":{"speed":1.26,"deg":184,"gust":2.01},"clouds":{"all":100},"dt":1638729076,
            "sys":{"type":2,"id":2000314,"country":"RU","sunrise":1638682876,"sunset":1638709164},"timezone":10800,"id":524901,"name":"Москва","cod":200}*/
            try {
                JSONObject jsonObject = new JSONObject(result);
                //результат выводим внутри окна для пользователя
                //result_info.setText("Заголовок: " + jsonObject.getJSONObject("главный ключ").getDouble-тип переменной-("второстепенный ключ"));
                //result_info.setText("Город: " + jsonObject.getJSONObject("name").getString("name"));
                //result_info.setText("Облачность: " + jsonObject.getJSONObject("weather").getString("description"));
                //result_info.setText("Температура: " + jsonObject.getJSONObject("main").getDouble("temp") + "°C");
                result_info.setText("По ощущению: " + jsonObject.getJSONObject("main").getDouble("feels_like") + "°C");
                //result_info.setText("Давление: " + jsonObject.getJSONObject("main").getInt("pressure") + " мм рт. ст.");
                //result_info.setText("Видимость: " + jsonObject.getJSONObject("visibility") + " км");
                //result_info.setText("Скорость ветра: " + jsonObject.getJSONObject("wind").getDouble("speed") + " м/с");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
