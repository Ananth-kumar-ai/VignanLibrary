package org.vignanuniversity.vignanlibrary;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import org.vignanuniversity.vignanlibrary.R;
import java.util.List;
public class BorrowedBookAdapter extends RecyclerView.Adapter<BorrowedBookAdapter.BookViewHolder> {
    private final List<BorrowedBook> books;
    private final Context context;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(BorrowedBook book);
    }

    public BorrowedBookAdapter(Context context, List<BorrowedBook> books, OnItemClickListener listener) {
        this.context = context;
        this.books = books;
        this.listener = listener;
    }

    @Override
    public BookViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_borrow, parent, false);
        return new BookViewHolder(v);
    }

    @Override
    public void onBindViewHolder(BookViewHolder holder, int position) {
        BorrowedBook book = books.get(position);
        holder.tvTitle.setText(book.title);
        holder.tvDepartment.setText(book.department);
        holder.tvAuthor.setText(book.author);

        holder.itemView.setOnClickListener(v -> listener.onItemClick(book));
    }

    @Override
    public int getItemCount() {
        return books.size();
    }

    static class BookViewHolder extends RecyclerView.ViewHolder {
        final TextView tvTitle, tvDepartment, tvAuthor;

        BookViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDepartment = itemView.findViewById(R.id.tvDepartment);
            tvAuthor = itemView.findViewById(R.id.tvAuthor);
        }
    }
}
