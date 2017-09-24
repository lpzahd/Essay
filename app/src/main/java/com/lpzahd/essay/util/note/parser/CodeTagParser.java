package com.lpzahd.essay.util.note.parser;

import android.graphics.Color;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;

import com.lpzahd.essay.util.note.parse.TagParse;

import java.util.ArrayList;
import java.util.List;

/**
 * Author : Lpzahd
 * Date : 三月
 * Desction : (•ิ_•ิ)
 */
public class CodeTagParser implements TagParse {

    private TagParser tagParser;

    int flag = Spanned.SPAN_INCLUSIVE_INCLUSIVE;

    Object bgSpan = new BackgroundColorSpan(Color.RED);

//    Object tagSpan = new TextAppearanceSpan("monospace",android.graphics.Typeface.BOLD_ITALIC, 30, csl, csllink);
    Object tagSpan = new ForegroundColorSpan(Color.BLUE);

    Object contentSpan = new ForegroundColorSpan(Color.BLUE);

    public CodeTagParser(String sTag, String eTag) {
        tagParser = new TagParser(sTag, eTag) {
            @Override
            List<Tag> generateTag(int start, int end, int sLen, int eLen) {
                List<Tag> tags = new ArrayList<>();

                if(end - start != sLen + eLen) {
                    tags.add(new Tag(generateSTagSpan() , start, start + sLen, flag));

                    tags.add(new Tag(genereateTxtSpan(), start + sLen, end - eLen, flag));

                    tags.add(new Tag(generateETagSpan(), end - eLen, end, flag));
                }

                return tags;
            }
        };
    }


    private Object generateSTagSpan() {
        return new ForegroundColorSpan(Color.BLUE);
    }

    private Object generateETagSpan() {
        return new ForegroundColorSpan(Color.BLUE);
    }

    private Object genereateTxtSpan() {
        return new BackgroundColorSpan(Color.RED);
    }

    @Override
    public List<Tag> parseTxt(String txt) {
        tagParser.parseTags(txt);
        return tagParser.getTags();
    }
}
