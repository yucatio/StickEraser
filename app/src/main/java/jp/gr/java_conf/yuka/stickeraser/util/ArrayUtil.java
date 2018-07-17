package jp.gr.java_conf.yuka.stickeraser.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class ArrayUtil {
    private ArrayUtil() {
    }

    /**
     * 0からsizeまでが格納された配列を返します。値の順番はランダムです
     * @param size 配列サイズ
     * @return 値の順番はランダムな、0からsizeまでが格納された配列
     */
    public static List<Integer> getRandomAccessIntArray(int size) {
        List<Integer> integerList = new ArrayList<>();

        // 初期化
        for (int i = 0; i < size; i++) {
            integerList.add(i);
        }

        // シャッフル
        Collections.shuffle(integerList);

        return integerList;
    }
}
