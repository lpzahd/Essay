//package com.lpzahd.essay.util.note.parser;
//
//import com.lpzahd.essay.util.note.parse.TagParse;
//
//import java.io.BufferedReader;
//import java.io.ByteArrayInputStream;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.util.List;
//
///**
// * Author : Lpzahd
// * Date : 三月
// * Desction : (•ิ_•ิ)
// */
//public class HTxtParser implements TagParse {
//
//    @Override
//    public List<Tag> parseTxt(String txt) {
//        return null;
//    }
//
//
//    void parseAndChangeTxt(String txt){
//        ByteArrayInputStream tInputStringStream = null;
//        BufferedReader reader = null;
//        StringBuilder builder = new StringBuilder();
//        try {
//            tInputStringStream = new ByteArrayInputStream(txt.getBytes());
//            reader = new BufferedReader(new InputStreamReader(tInputStringStream));
//            String temp;
//            while ((temp = reader.readLine()) != null) {
//
//                final int count = temp.length();
//                boolean isMark = false;
//                for (int i = 0; i < count; i++) {
//                    char s = temp.charAt(i);
//                    if(s == ' ') {
//                        if()
//                        continue;
//                    } else if(isMarkChar(s)) {
//
//                    } else {
//                        break;
//                    }
//                }
//                builder.append(temp).append("\n");
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private boolean isMarkChar(char s) {
//        if(s == '1' || s == '2'|| s == '3'|| s == '4'|| s == '5'|| s == '6'|| s == '7'|| s == '8'|| s == '9'|| s == '0'
//                || s == '一'|| s == '二'|| s == '三'|| s == '四'|| s == '五'|| s == '六'|| s == '七'|| s == '八'|| s == '九'|| s == '十')
//            return true;
//        return false;
//    }
//}
