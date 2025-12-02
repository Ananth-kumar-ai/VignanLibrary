package org.vignanuniversity.vignanlibrary;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.*;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import org.vignanuniversity.vignanlibrary.Models.EbookResource;
import org.vignanuniversity.vignanlibrary.Models.EbooksListItem;
import org.vignanuniversity.vignanlibrary.R;

import java.util.ArrayList;
import java.util.List;

public class EbooksAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<EbooksListItem> items = new ArrayList<>();
    private Context context;

    public EbooksAdapter(Context ctx) {
        this.context = ctx;
    }

    public void setItems(List<EbooksListItem> data) {
        items = data;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int pos) {
        return items.get(pos).type;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int type) {
        LayoutInflater inf = LayoutInflater.from(parent.getContext());

        if (type == EbooksListItem.TYPE_HEADER) {
            return new HeaderHolder(
                    inf.inflate(R.layout.item_ebook_header, parent, false)
            );
        } else {
            return new ResourceHolder(
                    inf.inflate(R.layout.item_ebook_resource, parent, false)
            );
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder h, int pos) {
        EbooksListItem item = items.get(pos);

        if (h instanceof HeaderHolder) {
            ((HeaderHolder) h).header.setText(item.headerTitle);
        }
        else {
            ((ResourceHolder) h).bind(item.resource);
        }
    }

    class HeaderHolder extends RecyclerView.ViewHolder {
        TextView header;
        HeaderHolder(View v) {
            super(v);
            header = v.findViewById(R.id.tvSectionHeader);
        }
    }

    class ResourceHolder extends RecyclerView.ViewHolder {
        TextView name, subtitle;
        CardView card;

        ResourceHolder(View v) {
            super(v);
            name = v.findViewById(R.id.tvResourceName);
            subtitle = v.findViewById(R.id.tvResourceSubtitle);
            card = (CardView) v;
        }

        void bind(EbookResource r) {
            name.setText(r.name);
            subtitle.setText("Tap to open resource");

            card.setOnClickListener(v -> {
                try {
                    Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(r.url));
                    context.startActivity(i);
                } catch (Exception e) {
                    Toast.makeText(context, "Invalid link", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
