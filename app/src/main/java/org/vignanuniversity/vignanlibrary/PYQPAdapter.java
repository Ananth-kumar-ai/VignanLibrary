package org.vignanuniversity.vignanlibrary;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import org.vignanuniversity.vignanlibrary.Models.PYQDepartment;
import org.vignanuniversity.vignanlibrary.Models.PYQPListItem;

import java.util.ArrayList;
import java.util.List;

public class PYQPAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String BASE_URL =
            "http://160.187.169.16:8080/jspui/handle/123456789/";

    private List<PYQPListItem> items = new ArrayList<>();
    private Context context;

    public PYQPAdapter(Context ctx) {
        this.context = ctx;
    }

    public void setItems(List<PYQPListItem> newItems) {
        this.items = newItems;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position).type;
    }

    @Override
    public int getItemCount() {
        return items == null ? 0 : items.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        if (viewType == PYQPListItem.TYPE_HEADER) {
            View v = inflater.inflate(R.layout.item_pyqp_header, parent, false);
            return new HeaderHolder(v);
        } else {
            View v = inflater.inflate(R.layout.item_pyqp_department, parent, false);
            return new DepartmentHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        PYQPListItem item = items.get(position);

        if (holder instanceof HeaderHolder) {
            ((HeaderHolder) holder).bind(item.headerTitle);
        } else if (holder instanceof DepartmentHolder) {
            ((DepartmentHolder) holder).bind(item.department);
        }
    }

    // HEADER HOLDER
    static class HeaderHolder extends RecyclerView.ViewHolder {
        private TextView tvHeader;
        HeaderHolder(View itemView) {
            super(itemView);
            tvHeader = itemView.findViewById(R.id.tvSectionHeader);
        }
        void bind(String title) {
            tvHeader.setText(title);
        }
    }

    // DEPARTMENT HOLDER
    class DepartmentHolder extends RecyclerView.ViewHolder {
        private TextView tvName, tvSubtitle;
        private CardView card;

        DepartmentHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvDeptName);
            tvSubtitle = itemView.findViewById(R.id.tvDeptSubtitle);
            card = (CardView) itemView;
        }

        void bind(final PYQDepartment dept) {
            tvName.setText(dept.deptName);
            tvSubtitle.setText("Tap to view question papers");

            card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openUrl(dept);
                }
            });
        }

        private void openUrl(PYQDepartment dept) {
            try {
                String url = BASE_URL + dept.urlId;
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                context.startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(context, "Unable to open link", Toast.LENGTH_SHORT).show();
            }
        }}
}