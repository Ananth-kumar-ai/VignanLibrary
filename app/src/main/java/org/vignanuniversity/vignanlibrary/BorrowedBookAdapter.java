package org.vignanuniversity.vignanlibrary;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
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

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_borrow, parent, false);
        return new BookViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        // Prevent IndexOutOfBoundsException
        if (position < 0 || position >= books.size()) return;

        BorrowedBook book = books.get(position);
        if (book == null) return; // extra safety

        // Defensive null handling for each field
        String title = book.title != null ? book.title : "Unknown Title";
        String dept = book.department != null ? book.department : "Unknown Department";
        String author = book.author != null ? book.author : "Unknown Author";
        String publisher = book.publisher != null ? book.publisher : "Unknown Publisher";

        holder.tvTitle.setText(title);
        holder.tvDepartment.setText(dept);
        holder.tvAuthor.setText("Author: " + author);
        holder.tvPublisher.setText("Publisher: " + publisher);

        // Prevent crashes if listener is null
        holder.itemView.setOnClickListener(v -> {
            if (listener != null && book != null) {
                try {
                    listener.onItemClick(book);
                } catch (Exception e) {
                    e.printStackTrace(); // Log silently, no crash
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return books == null ? 0 : books.size();
    }

    static class BookViewHolder extends RecyclerView.ViewHolder {
        final TextView tvTitle, tvDepartment, tvAuthor, tvPublisher;

        BookViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDepartment = itemView.findViewById(R.id.tvDepartment);
            tvAuthor = itemView.findViewById(R.id.tvAuthor);
            tvPublisher = itemView.findViewById(R.id.tvPublisher);
        }
    }
}
