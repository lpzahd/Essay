package com.lpzahd.essay.util.note.parser;

import android.graphics.Color;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.BackgroundColorSpan;

import com.lpzahd.atool.constant.Constance;
import com.lpzahd.essay.util.note.parse.TagParse;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Author : Lpzahd
 * Date : 三月
 * Desction : (•ิ_•ิ)
 */
public class HTagParser implements TagParse {

    private List<Tag> tags = new ArrayList<>();

    public HTagParser() {
    }

    /**
     * 解析方式：
     * 单独处理每行文本
     * 如果空个开始则继续查询下一个字符，直到数字开头为止开始解析。
     * 如果形似"1 ", "1、", "1. " 之类就认为改行是 -，-？？？ 好吧，就是我要的东西，不晓得是什么
     *
     * @param txt  解析文本
     * @param tags 标签储存的结合
     */
    private void parseTags(String txt, List<Tag> tags) {
        if (TextUtils.isEmpty(txt)) return;

        tags.clear();

        ByteArrayInputStream inputStream = null;
        BufferedReader reader = null;

        try {
            inputStream = new ByteArrayInputStream(txt.getBytes());
            reader = new BufferedReader(new InputStreamReader(inputStream));
            String temp;
            int currCount = 0;
            String lineSe = Constance.lineSeparator();
            final int lineLen = lineSe.length();
            a:
            while ((temp = reader.readLine()) != null) {

                final int count = temp.length();
                currCount += count + lineLen;
                int start = currCount - count - 1;

                boolean isMark = false;
                b:
                for (int i = 0; i < count; i++) {
                    char s = temp.charAt(i);

                    if (s == ' ') {
                        if (isMark) {
                            for (int j = i; j < count; j++) {
                                char c = temp.charAt(j);
                                if (c != ' ' && !isMarkChar(c)) {
                                    List<Tag> tag = generateTag(start, currCount);
                                    if (tag != null)
                                        tags.addAll(tag);

                                    continue a;
                                }
                            }
                        }
                    } else if (isMarkChar(s)) {
                        isMark = true;
                    } else {
                        if (isMark) {
                            for (int j = i; j < count; j++) {
                                char c = temp.charAt(j);
                                if (!isMarkChar(c)) {
                                    if (c == ' ' || c == '、') {
                                        List<Tag> tag = generateTag(start, currCount);
                                        if (tag != null)
                                            tags.addAll(tag);

                                        continue a;
                                    } else if (c == '.') {
                                        int next = j + 1;
                                        if (next < count && temp.charAt(next) == ' ') {
                                            List<Tag> tag = generateTag(start, currCount);
                                            if (tag != null)
                                                tags.addAll(tag);

                                            continue a;
                                        }
                                    } else {
                                        break b;
                                    }
                                }
                            }
                        }
                        break;
                    }
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


    }

    private List<Tag> generateTag(int start, int end) {
        List<Tag> tags = new ArrayList<>();
        tags.add(new Tag(genereateTxtSpan(), start, end, Spanned.SPAN_INCLUSIVE_INCLUSIVE));

        return tags;
    }

    private Object genereateTxtSpan() {
        return new BackgroundColorSpan(Color.RED);
    }

    private boolean isMarkChar(char c) {
        return (c == '1' || c == '2' || c == '3' || c == '4' || c == '5' || c == '6' || c == '7' || c == '8' || c == '9' || c == '0'
                || c == '一' || c == '二' || c == '三' || c == '四' || c == '五' || c == '六' || c == '七' || c == '八' || c == '九' || c == '十');
    }

    @Override
    public List<Tag> parseTxt(String txt) {
        parseTags(txt, tags);
        return tags;
    }
}
