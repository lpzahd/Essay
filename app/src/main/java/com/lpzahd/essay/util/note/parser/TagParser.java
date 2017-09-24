package com.lpzahd.essay.util.note.parser;

import android.text.TextUtils;

import com.lpzahd.Objects;
import com.lpzahd.aop.api.Log;
import com.lpzahd.essay.util.note.parse.TagParse;

import java.util.ArrayList;
import java.util.List;

/**
 * Author : Lpzahd
 * Date : 三月
 * Desction : (•ิ_•ิ)
 */
public abstract class TagParser {

    private final CharSequence sTag;
    private final CharSequence eTag;

    private char sOne;
    private char eOne;

    private List<TagParse.Tag> tags = new ArrayList<>();

    public TagParser(String sTag, String eTag) {
        if(sTag == null || sTag.length() < 3) {
            throw new AssertionError("前置标签长度低于3解析会出现问题！");
        }

        if(eTag == null || eTag.length() < 3) {
            throw new AssertionError("后置标签长度低于3解析会出现问题！");
        }

        this.sTag = sTag;
        this.eTag = eTag;

        sOne = sTag.charAt(0);
        eOne = sTag.charAt(0);
    }

    public List<TagParse.Tag> getTags() {
        return tags;
    }

    /**
     * 解析出所有指定标签
     */
    @Log
    public void parseTags(CharSequence txt) {
        if(TextUtils.isEmpty(txt)) return ;

        tags.clear();

        final int len = txt.length();

        final int sLen = sTag.length();
        final int eLen = eTag.length();
        final int tagLen = sLen + eLen;

        a : for (int i = 0; i < len; i++) {
            char s = txt.charAt(i);

            // 前置标签首字符匹配成功
            if (s == sOne) {
                if (i + tagLen < len) {
                    CharSequence sTemp = txt.subSequence(i, i + sLen);

                    if (Objects.equals(sTemp, sTag)) {
                         for (int j = i + sLen; j < len; j++) {
                            char t = txt.charAt(j);

                            // 后置标签首字符匹配成功
                            if (t == eOne) {
                                CharSequence eTemp = txt.subSequence(j, j + eLen);

                                if (Objects.equals(eTemp, eTag)) {
                                    final int start = i;
                                    final int end = j + eLen;

                                    List<TagParse.Tag> tag = generateTag(start, end, sLen, eLen);
                                    if(tag != null)
                                        tags.addAll(tag);

                                    i = end;
                                    continue a;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    abstract List<TagParse.Tag> generateTag(int start, int end, int sLen, int eLen);
//
//    @Override
//    public List<Tag> parseTxt(String txt) {
//        parseTags(txt, tags);
//        return tags;
//    }
}
