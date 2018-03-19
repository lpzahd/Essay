package com.lpzahd.essay.context.note.waiter;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.drawee.view.SimpleDraweeView;
import com.lpzahd.essay.R;
import com.lpzahd.essay.db.file.Image;
import com.lpzahd.essay.db.note.NoteTypes;
import com.lpzahd.common.tone.adapter.ToneAdapter;
import com.lpzahd.common.tone.waiter.ToneWindowWaiter;
import com.lpzahd.common.util.fresco.Frescoer;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Author : Lpzahd
 * Date : 三月
 * Desction : (•ิ_•ิ)
 */
public class NotesWaiter extends ToneWindowWaiter {

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;


    Realm realm;

    NotesAdapter adapter;

    @Override
    protected void create(View rootView) {
        super.create(rootView);

        initRecyclerView();

        realm = Realm.getDefaultInstance();

        searchNotes();
    }

    private void initRecyclerView() {
        recyclerView.setHasFixedSize(true);

        GridLayoutManager layoutManager = new GridLayoutManager(context, 3);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new NotesAdapter(context);
        recyclerView.setAdapter(adapter);
    }

    private void searchNotes() {
        RealmResults<NoteTypes> noteResults = realm.where(NoteTypes.class)
                .sort("position")
                .findAll();
//                .findAllSorted("position");
        adapter.setData(noteResults);
    }

    @Override
    protected void destroy() {
        super.destroy();
        if(realm != null && !realm.isClosed()) {
            realm.close();
        }
    }

    static class NotesAdapter extends ToneAdapter<NoteTypes, NotesHolder> {

        public NotesAdapter(Context context) {
            super(context);
        }

        @Override
        public NotesHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new NotesHolder(inflateItemView(R.layout.item_notes, parent));
        }

        @Override
        public void onBindViewHolder(NotesHolder holder, int position) {
            NoteTypes note = getItem(position);

            holder.noteTv.setText(note.getName());

            Image image = note.getImage();
            holder.noteDraweeView.setImageURI(Frescoer.uri(image.getPath(), image.getSource()));

        }
    }

    static class NotesHolder extends ToneAdapter.ToneHolder {

        @BindView(R.id.note_drawee_view)
        SimpleDraweeView noteDraweeView;

        @BindView(R.id.note_tv)
        AppCompatTextView noteTv;

        public NotesHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
