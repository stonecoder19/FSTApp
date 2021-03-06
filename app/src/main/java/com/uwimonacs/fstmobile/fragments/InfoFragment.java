package com.uwimonacs.fstmobile.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.uwimonacs.fstmobile.R;
import com.uwimonacs.fstmobile.activities.ContactsActivity;
import com.uwimonacs.fstmobile.activities.EventActivity;
import com.uwimonacs.fstmobile.activities.FAQActivity;
import com.uwimonacs.fstmobile.activities.GalleryActivity;
import com.uwimonacs.fstmobile.activities.ScholarshipActivity;
import com.uwimonacs.fstmobile.activities.VideoListActivity;

public class InfoFragment extends Fragment
        implements View.OnClickListener {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.frag_info, container, false);

        final CardView card_faq = (CardView) v.findViewById(R.id.faq);
        final CardView card_videos = (CardView) v.findViewById(R.id.videos);
        final CardView card_scholarships = (CardView) v.findViewById(R.id.scholarships);
        final CardView card_contacts = (CardView) v.findViewById(R.id.contacts);
        final CardView card_gallery = (CardView)v.findViewById(R.id.gallery);
        final CardView card_event = (CardView)v.findViewById(R.id.event);

        /*
         * The event handler is registered at runtime instead of in the onClick XML attribute of
         * the Button nodes because the View hierarchy is in a Fragment.
         */
        card_faq.setOnClickListener(this);
        card_videos.setOnClickListener(this);
        card_scholarships.setOnClickListener(this);
        card_contacts.setOnClickListener(this);
        card_gallery.setOnClickListener(this);
        card_event.setOnClickListener(this);

        return v;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.faq:
                v.getContext().startActivity(new Intent(v.getContext(), FAQActivity.class));
                break;

            case R.id.videos:
                v.getContext().startActivity(new Intent(v.getContext(), VideoListActivity.class));
                break;

            case R.id.scholarships:
                v.getContext().startActivity(new Intent(v.getContext(), ScholarshipActivity.class));
                break;

            case R.id.contacts:
                v.getContext().startActivity(new Intent(v.getContext(), ContactsActivity.class));
                break;

            case R.id.gallery:
                v.getContext().startActivity(new Intent(v.getContext(),GalleryActivity.class));
                break;

            case R.id.event:
                v.getContext().startActivity(new Intent(v.getContext(),EventActivity.class));
                break;

            default:
                break;
        }
    }

    public InfoFragment() { /* required empty constructor */ }
}
