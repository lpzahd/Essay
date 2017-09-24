package com.lpzahd.essay.util.note.parse;

import java.util.List;

/**
 * Author : Lpzahd
 * Date : 三月
 * Desction : (•ิ_•ิ)
 */
public interface TagParse {

    List<Tag> parseTxt(String txt);

    class Tag {
        int start;
        int end;
        Object what;
        int flag;

        public Tag() {}

        public Tag( Object what, int start, int end, int flag) {
            this.what = what;
            this.start = start;
            this.end = end;
            this.flag = flag;
        }

        public int getStart() {
            return start;
        }

        public void setStart(int start) {
            this.start = start;
        }

        public int getEnd() {
            return end;
        }

        public void setEnd(int end) {
            this.end = end;
        }

        public Object getWhat() {
            return what;
        }

        public void setWhat(Object what) {
            this.what = what;
        }

        public int getFlag() {
            return flag;
        }

        public void setFlag(int flag) {
            this.flag = flag;
        }
    }
}
