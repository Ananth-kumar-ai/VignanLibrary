package org.vignanuniversity.vignanlibrary;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.card.MaterialCardView;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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

        // Check if book is overdue
        boolean isOverdue = checkIfOverdue(book);

        if (isOverdue) {
            // Set red background for overdue books
            holder.cardView.setCardBackgroundColor(Color.parseColor("#FFCDD2")); // Light red
            holder.tvTitle.setTextColor(Color.parseColor("#C62828")); // Dark red for title
            holder.tvDepartment.setTextColor(Color.parseColor("#C62828"));
            holder.tvAuthor.setTextColor(Color.parseColor("#C62828"));
            holder.tvPublisher.setTextColor(Color.parseColor("#C62828"));
        } else {
            // Set default white background
            holder.cardView.setCardBackgroundColor(Color.WHITE);
            holder.tvTitle.setTextColor(Color.BLACK);
            holder.tvDepartment.setTextColor(Color.parseColor("#555555"));
            holder.tvAuthor.setTextColor(Color.BLACK);
            holder.tvPublisher.setTextColor(Color.BLACK);
        }

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

    private boolean checkIfOverdue(BorrowedBook book) {
        try {
            // Check if book has NOT been returned (dateOfReturn is 00/00/0000 or -- or empty)
            if (book.dateOfReturn == null ||
                    book.dateOfReturn.equals("00/00/0000") ||
                    book.dateOfReturn.equals("--") ||
                    book.dateOfReturn.trim().isEmpty()) {

                // Check if we have a valid issue date
                if (book.dateOfIssue != null &&
                        !book.dateOfIssue.equals("--") &&
                        !book.dateOfIssue.isEmpty() &&
                        !book.dateOfIssue.equals("00/00/0000")) {

                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                    LocalDate issueDate = LocalDate.parse(book.dateOfIssue.trim(), formatter);
                    LocalDate dueDate = issueDate.plusDays(14);
                    LocalDate today = LocalDate.now();

                    // Return true if current date is after due date
                    return today.isAfter(dueDate);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public int getItemCount() {
        return books == null ? 0 : books.size();
    }

    static class BookViewHolder extends RecyclerView.ViewHolder {
        final TextView tvTitle, tvDepartment, tvAuthor, tvPublisher;
        final MaterialCardView cardView;

        BookViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = (MaterialCardView) itemView;
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDepartment = itemView.findViewById(R.id.tvDepartment);
            tvAuthor = itemView.findViewById(R.id.tvAuthor);
            tvPublisher = itemView.findViewById(R.id.tvPublisher);
        }
    }
}