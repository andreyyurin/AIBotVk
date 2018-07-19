package ltc.aibotvk;

import android.support.v4.util.Pair;

import java.util.ArrayList;

/**
 * Created by admin on 11.07.2018.
 */

public class Repository {
    public static ArrayList<String> values = new ArrayList<>();

    public static Integer countMessages = 0;

    public static ArrayList<Pair<Integer, String>> unreadMessages = new ArrayList<>();

    public static Integer chat_id;

    public static String[] dictionary = {"привет", "прив", "здарова", "дарова", "даров",
                                        "здравствуйте", "здравствуй", "глубокое почтение",
                                        "глубочайшее почтение", "доброе утро", "добрый день", "добрый вечер", "душою рад видеть вас",
                                        "добро пожаловать", "дозвольте приветсвовать", "душевно рад видеть вас", "здравия желаю",
                                        "приветствую вас", "разрешите вас приветствовать"};
}
